package dev.jlipka.githubapiclientspringboot.service;

import dev.jlipka.githubapiclientspringboot.client.GithubClient;
import dev.jlipka.githubapiclientspringboot.client.dto.GithubBranch;
import dev.jlipka.githubapiclientspringboot.client.dto.GithubOwner;
import dev.jlipka.githubapiclientspringboot.client.dto.GithubRepository;
import dev.jlipka.githubapiclientspringboot.consumer.GithubMapper;
import dev.jlipka.githubapiclientspringboot.consumer.RepositoryDto;
import dev.jlipka.githubapiclientspringboot.consumer.UserReposService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class UserReposServiceUnitTest {

    @Mock
    GithubMapper githubMapper;

    @Mock
    GithubClient githubClient;

    @InjectMocks
    UserReposService userReposService;

    @Captor
    ArgumentCaptor<GithubRepository> repoCaptor;

    @Test
    void getUserRepositories_ShouldReturnOnlyNonForkRepositories() {
        //given
        when(githubClient.fetchUserRepositories(anyString())).thenReturn(getExampleGithubRepositories());
        when(githubClient.fetchRepositoryBranches(anyString(), anyString())).thenReturn(new GithubBranch[]{});

        //when
        List<RepositoryDto> test = userReposService.getUserRepositories("test", false);

        //then
        verify(githubMapper).mapToRepository(repoCaptor.capture(), any());
        List<GithubRepository> capturedRepos = repoCaptor.getAllValues();
        assertThat(test.size()).isEqualTo(1);
        assertThat(capturedRepos).allMatch(capturedRepo -> capturedRepo.isFork() == false);
    }

    @Test
    void getUserRepositories_ShouldReturnOnlyForkRepositories() {
        //given
        when(githubClient.fetchUserRepositories(anyString())).thenReturn(getExampleGithubRepositories());
        when(githubClient.fetchRepositoryBranches(anyString(), anyString())).thenReturn(new GithubBranch[]{});

        //when
        List<RepositoryDto> test = userReposService.getUserRepositories("test", true);

        //then
        verify(githubMapper).mapToRepository(repoCaptor.capture(), any());
        List<GithubRepository> capturedRepos = repoCaptor.getAllValues();

        assertThat(test.size()).isEqualTo(1);
        assertThat(capturedRepos).allMatch(capturedRepo -> capturedRepo.isFork() == true);
    }

    private GithubRepository[] getExampleGithubRepositories() {
        GithubRepository nonForkRepo = new GithubRepository("non-fork-repository", new GithubOwner("test-owner-login"), false);

        GithubRepository forkRepo = new GithubRepository("fork-repository", new GithubOwner("test-owner-login"), true);

        return new GithubRepository[]{nonForkRepo, forkRepo};
    }
}
