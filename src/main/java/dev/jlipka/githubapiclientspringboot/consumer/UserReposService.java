package dev.jlipka.githubapiclientspringboot.consumer;

import dev.jlipka.githubapiclientspringboot.client.GithubClient;
import dev.jlipka.githubapiclientspringboot.client.dto.GithubRepository;
import dev.jlipka.githubapiclientspringboot.consumer.error.UserNotFoundException;
import dev.jlipka.githubapiclientspringboot.consumer.model.Branch;
import dev.jlipka.githubapiclientspringboot.consumer.model.Repository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserReposService {

    private final GithubMapper githubMapper;
    private final GithubClient githubClient;

    public List<RepositoryDto> getUserRepositories(String username, boolean isFork) {
        try {
            List<Repository> filteredRepositories = getFilteredRepositories(username, isFork);

            return filteredRepositories.stream()
                    .map(githubMapper::mapToRepositoryDto)
                    .toList();
        } catch (HttpClientErrorException e) {
            throw new UserNotFoundException("Could not find github user with given username");
        }
    }

    private List<Repository> getFilteredRepositories(String username, boolean isFork) {
        List<GithubRepository> githubRepositories = Arrays.asList(githubClient.fetchUserRepositories(username));

        return githubRepositories.stream()
                .filter(repo -> repo.isFork() == isFork)
                .map(this::mapRepositoryWithBranches)
                .toList();
    }

    private Repository mapRepositoryWithBranches(GithubRepository githubRepository) {
        List<Branch> branches = Arrays.stream(githubClient.fetchRepositoryBranches(githubRepository.owner()
                        .login(), githubRepository.name()))
                .map(githubMapper::mapToBranch)
                .toList();

        return githubMapper.mapToRepository(githubRepository, branches);
    }
}
