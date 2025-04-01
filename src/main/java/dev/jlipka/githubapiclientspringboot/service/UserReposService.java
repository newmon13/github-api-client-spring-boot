package dev.jlipka.githubapiclientspringboot.service;

import dev.jlipka.githubapiclientspringboot.client.GithubClient;
import dev.jlipka.githubapiclientspringboot.client.dto.GithubRepository;
import dev.jlipka.githubapiclientspringboot.error.UserNotFoundException;
import dev.jlipka.githubapiclientspringboot.mapper.GithubMapper;
import dev.jlipka.githubapiclientspringboot.model.Branch;
import dev.jlipka.githubapiclientspringboot.model.Repository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserReposService {

    private final GithubMapper githubMapper;
    private final GithubClient githubClient;

    public List<Repository> getUserNonForkRepositories(String username) {
        List<Repository> resultList = new ArrayList<>();
        try {
            List<GithubRepository> nonForkGithubRepositories = getNonForkGithubRepositories(username);

            for (GithubRepository githubRepository : nonForkGithubRepositories) {
                List<Branch> branches = Arrays.stream(githubClient.fetchRepositoryBranches(username, githubRepository.name()))
                        .map(githubMapper::mapToBranch)
                        .toList();

                Repository repository = githubMapper.mapToRepository(githubRepository, branches);
                resultList.add(repository);
            }

            return resultList;
        } catch (HttpClientErrorException.NotFound e) {
            throw new UserNotFoundException("Could not find github user with given username");
        }
    }

    private List<GithubRepository> getNonForkGithubRepositories(String username) {
        List<GithubRepository> githubRepositories = Arrays.asList(githubClient.fetchUserRepositories(username));
        return githubRepositories.stream()
                .filter(githubRepository -> !githubRepository.isFork())
                .toList();
    }
}