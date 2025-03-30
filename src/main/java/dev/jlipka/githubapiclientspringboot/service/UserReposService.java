package dev.jlipka.githubapiclientspringboot.service;

import dev.jlipka.githubapiclientspringboot.dto.github.GithubBranch;
import dev.jlipka.githubapiclientspringboot.dto.github.GithubRepository;
import dev.jlipka.githubapiclientspringboot.error.UserNotFoundException;
import dev.jlipka.githubapiclientspringboot.mapper.GithubMapper;
import dev.jlipka.githubapiclientspringboot.model.Branch;
import dev.jlipka.githubapiclientspringboot.model.Repository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserReposService {

    private static final String GITHUB_API_URL = "https://api.github.com";

    private final RestClient restClient;

    private final GithubMapper githubMapper;

    public List<Repository> getUserRepositories(String username) {
        try {
            List<Repository> resultRepositories = new ArrayList<>();
            GithubRepository[] githubRepositories = fetchUserRepositories(username);

            for (GithubRepository githubRepository : githubRepositories) {
                List<Branch> branches = Arrays.stream(fetchRepositoryBranches(username, githubRepository.name()))
                        .map(githubMapper::mapToBranch)
                        .toList();

                Repository repository = githubMapper.mapToRepository(githubRepository, branches);
                resultRepositories.add(repository);
            }

            return resultRepositories;
        } catch (HttpClientErrorException.NotFound e) {
            throw new UserNotFoundException("Could not find github user with given username");
        }
    }

    private GithubRepository[] fetchUserRepositories(String username) {
        return restClient.get()
                .uri(GITHUB_API_URL + "/users/" + username + "/repos")
                .retrieve()
                .body(GithubRepository[].class);
    }

    private GithubBranch[] fetchRepositoryBranches(String username, String repositoryName) {
        return restClient.get()
                .uri(GITHUB_API_URL + "/repos/" + username + "/" + repositoryName + "/branches")
                .retrieve()
                .body(GithubBranch[].class);
    }
}