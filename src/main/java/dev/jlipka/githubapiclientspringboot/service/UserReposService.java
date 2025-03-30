package dev.jlipka.githubapiclientspringboot.service;

import dev.jlipka.githubapiclientspringboot.dto.github.GithubBranch;
import dev.jlipka.githubapiclientspringboot.dto.github.GithubRepository;
import dev.jlipka.githubapiclientspringboot.model.Repository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserReposService {

    private static final String GITHUB_API_URL = "https://api.github.com";

    private final RestClient restClient;

    public List<Repository> getUserRepositories(String username) {
        List<Repository> completeRepositories = new ArrayList<>();
        GithubRepository[] repositories = fetchUserRepositories(username);

        for (GithubRepository repository : repositories) {
            GithubBranch[] branches = fetchRepositoryBranches(username, repository.name());

            Repository completeRepository = new Repository(
                    repository.name(),
                    repository.owner().login(),
                    Arrays.asList(branches)
            );

            completeRepositories.add(completeRepository);
        }

        return completeRepositories;
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