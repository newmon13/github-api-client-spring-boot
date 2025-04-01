package dev.jlipka.githubapiclientspringboot.client;

import dev.jlipka.githubapiclientspringboot.client.dto.GithubBranch;
import dev.jlipka.githubapiclientspringboot.client.dto.GithubRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class GithubClient {

    @Value("${github.url}")
    private String GITHUB_API_URL;

    private final RestClient restClient;

    public GithubClient(RestClient restClient) {
        this.restClient = restClient;
    }

    public GithubRepository[] fetchUserRepositories(String username) {
        return restClient.get()
                .uri(GITHUB_API_URL + "/users/" + username + "/repos")
                .retrieve()
                .body(GithubRepository[].class);
    }

    public GithubBranch[] fetchRepositoryBranches(String username, String repositoryName) {
        return restClient.get()
                .uri(GITHUB_API_URL + "/repos/" + username + "/" + repositoryName + "/branches")
                .retrieve()
                .body(GithubBranch[].class);
    }
}
