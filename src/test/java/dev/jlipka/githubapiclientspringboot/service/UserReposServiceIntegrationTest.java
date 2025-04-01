package dev.jlipka.githubapiclientspringboot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import dev.jlipka.githubapiclientspringboot.client.dto.GithubBranch;
import dev.jlipka.githubapiclientspringboot.client.dto.GithubCommit;
import dev.jlipka.githubapiclientspringboot.client.dto.GithubOwner;
import dev.jlipka.githubapiclientspringboot.client.dto.GithubRepository;
import dev.jlipka.githubapiclientspringboot.error.UserNotFoundException;
import dev.jlipka.githubapiclientspringboot.model.Repository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.getRequestedFor;
import static com.github.tomakehurst.wiremock.client.WireMock.stubFor;
import static com.github.tomakehurst.wiremock.client.WireMock.urlPathMatching;
import static com.github.tomakehurst.wiremock.client.WireMock.verify;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrows;


@SpringBootTest
@WireMockTest(httpPort = 8081)
class UserReposServiceIntegrationTest {

    private static final String TEST_USERNAME = "test";
    private static final UrlPattern reposUrlPattern = urlPathMatching("/users/" + TEST_USERNAME + "/repos");
    private static final UrlPattern branchesUrlPattern = urlPathMatching("/repos/" + TEST_USERNAME + "/[^/]+/branches");

    @Autowired
    UserReposService userReposService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getUserNonForkRepositories_ShouldReturnOnlyNonForkRepositories_WhenUserExists() {
        //given
        JsonNode reposResponseBody = getExampleGithubRepositoriesJson();
        JsonNode branchesResponseBody = getExampleGithubRepositoryBranchesJson();

        stubFor(get(reposUrlPattern).willReturn(aResponse().withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withJsonBody(reposResponseBody)));

        stubFor(get(branchesUrlPattern).willReturn(aResponse().withStatus(200)
                .withHeader("Content-Type", "application/json")
                .withJsonBody(branchesResponseBody)));

        // when
        List<Repository> userRepositories = userReposService.getUserNonForkRepositories(TEST_USERNAME);

        // then
        assertAll(() -> assertThat(userRepositories).isNotNull(), () -> assertThat(userRepositories).isNotEmpty(), () -> assertThat(userRepositories.size()).isEqualTo(1), () -> assertThat(userRepositories.get(0)
                .name()).isEqualTo("non-fork-repository"), () -> verify(getRequestedFor(reposUrlPattern)), () -> verify(getRequestedFor(branchesUrlPattern)));
    }

    @Test
    public void getUserNonForkRepositories_ShouldThrowException_WhenUserDoesNotExist() {
        //given
        stubFor(get(reposUrlPattern).willReturn(aResponse().withStatus(404)));

        //when & then
        assertThrows(UserNotFoundException.class, () -> {
            userReposService.getUserNonForkRepositories(TEST_USERNAME);
        });
    }

    private JsonNode getExampleGithubRepositoriesJson() {
        GithubRepository nonForkRepo = new GithubRepository("non-fork-repository", new GithubOwner("test-owner-login"), false);

        GithubRepository forkRepo = new GithubRepository("fork-repository", new GithubOwner("test-owner-login"), true);

        return objectMapper.valueToTree(List.of(nonForkRepo, forkRepo));
    }

    private JsonNode getExampleGithubRepositoryBranchesJson() {
        GithubBranch githubBranch = new GithubBranch("test_branch_name", new GithubCommit("test_sha"));

        return objectMapper.valueToTree(List.of(githubBranch));
    }
}