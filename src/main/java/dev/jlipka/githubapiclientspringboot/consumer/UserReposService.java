package dev.jlipka.githubapiclientspringboot.consumer;

import dev.jlipka.githubapiclientspringboot.client.GithubClient;
import dev.jlipka.githubapiclientspringboot.client.dto.GithubRepository;
import dev.jlipka.githubapiclientspringboot.consumer.error.ExternalApiException;
import dev.jlipka.githubapiclientspringboot.consumer.error.UserNotFoundException;
import dev.jlipka.githubapiclientspringboot.consumer.model.Branch;
import dev.jlipka.githubapiclientspringboot.consumer.model.Repository;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserReposService {

    private static final String GITHUB_API_CB = "githubApiCircuitBreaker";
    private final GithubMapper githubMapper;
    private final GithubClient githubClient;

    @CircuitBreaker(name = GITHUB_API_CB, fallbackMethod = "getUserRepositoriesFallback")
    public List<RepositoryDto> getUserRepositories(String username, boolean isFork) {
        List<Repository> filteredRepositories = getFilteredRepositories(username, isFork);

        return filteredRepositories.stream()
                .map(githubMapper::mapToRepositoryDto)
                .toList();
    }

    private List<Repository> getFilteredRepositories(String username, boolean isFork) {
        List<GithubRepository> githubRepositories = Arrays.asList(githubClient.fetchUserRepositories(username));

        return githubRepositories.stream()
                .filter(repo -> repo.isFork() == isFork)
                .map(this::getRepositoryWithBranches)
                .toList();
    }

    private Repository getRepositoryWithBranches(GithubRepository githubRepository) {
        List<Branch> branches = Arrays.stream(githubClient.fetchRepositoryBranches(githubRepository.owner()
                        .login(), githubRepository.name()))
                .map(githubMapper::mapToBranch)
                .toList();

        return githubMapper.mapToRepository(githubRepository, branches);
    }

    private List<RepositoryDto> getUserRepositoriesFallback(String username, boolean isFork, Throwable throwable) {
        log.error("Circuit breaker fallback activated for user {}: {}", username, throwable.getMessage(), throwable);

        if (throwable instanceof HttpClientErrorException.NotFound) {
            throw new UserNotFoundException("Cannot find user: " + username);
        }

        throw new ExternalApiException(HttpStatus.SERVICE_UNAVAILABLE, "Connection with GitHub API currently unavailable: " + throwable.getMessage());
    }
}