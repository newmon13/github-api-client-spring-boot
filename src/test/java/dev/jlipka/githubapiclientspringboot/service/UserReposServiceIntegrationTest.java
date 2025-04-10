package dev.jlipka.githubapiclientspringboot.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.http.Fault;
import com.github.tomakehurst.wiremock.junit5.WireMockTest;
import com.github.tomakehurst.wiremock.matching.UrlPattern;
import dev.jlipka.githubapiclientspringboot.client.dto.GithubBranch;
import dev.jlipka.githubapiclientspringboot.client.dto.GithubCommit;
import dev.jlipka.githubapiclientspringboot.client.dto.GithubOwner;
import dev.jlipka.githubapiclientspringboot.client.dto.GithubRepository;
import dev.jlipka.githubapiclientspringboot.consumer.RepositoryDto;
import dev.jlipka.githubapiclientspringboot.consumer.UserReposService;
import dev.jlipka.githubapiclientspringboot.consumer.error.ExternalApiException;
import dev.jlipka.githubapiclientspringboot.consumer.error.UserNotFoundException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

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
    private static final String TEST_OWNER_LOGIN = "test-owner-login";

    private static final UrlPattern reposUrlPattern = urlPathMatching("/users/" + TEST_USERNAME + "/repos");
    private static final UrlPattern branchesUrlPattern = urlPathMatching("/repos/" + TEST_OWNER_LOGIN + "/non-fork-repository/branches");

    @Autowired
    UserReposService userReposService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getUserNonForkRepositories_ShouldReturnRepositories_WhenUserExists() {
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
        List<RepositoryDto> userRepositories = userReposService.getUserRepositories(TEST_USERNAME, false);

        // then
        verify(getRequestedFor(reposUrlPattern));
        verify(getRequestedFor(branchesUrlPattern));

        assertAll(() -> assertThat(userRepositories).isNotNull(), () -> assertThat(userRepositories).isNotEmpty(), () -> assertThat(userRepositories.size()).isEqualTo(1), () -> assertThat(userRepositories.get(0)
                .name()).isEqualTo("non-fork-repository"));
    }

    @Test
    void getUserNonForkRepositories_ShouldThrowUserNotFoundException_WhenUserDoesNotExist() {
        //given
        stubFor(get(reposUrlPattern).willReturn(aResponse().withStatus(404)));

        //when & then
        assertThrows(UserNotFoundException.class, () -> userReposService.getUserRepositories(TEST_USERNAME, true));

        verify(getRequestedFor(reposUrlPattern));
    }

    @Test
    void getUserNonForkRepositories_ShouldThrowExternalApiException_WhenConnectionWithGithubApiCrashed() {
        // given
        stubFor(get(reposUrlPattern).willReturn(aResponse().withFault(Fault.CONNECTION_RESET_BY_PEER)));

        // when & then
        ExternalApiException exception = assertThrows(ExternalApiException.class, () -> userReposService.getUserRepositories(TEST_USERNAME, true));

        assertThat(exception.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        verify(getRequestedFor(reposUrlPattern));
    }

    private JsonNode getExampleGithubRepositoriesJson() {
        GithubRepository nonForkRepo = new GithubRepository("non-fork-repository", new GithubOwner(TEST_OWNER_LOGIN), false);
        GithubRepository forkRepo = new GithubRepository("fork-repository", new GithubOwner(TEST_OWNER_LOGIN), true);
        return objectMapper.valueToTree(List.of(nonForkRepo, forkRepo));
    }

    private JsonNode getExampleGithubRepositoryBranchesJson() {
        GithubBranch githubBranch = new GithubBranch("test_branch_name", new GithubCommit("test_sha"));
        return objectMapper.valueToTree(List.of(githubBranch));
    }
}