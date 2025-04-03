package dev.jlipka.githubapiclientspringboot.consumer;

import dev.jlipka.githubapiclientspringboot.client.dto.GithubBranch;
import dev.jlipka.githubapiclientspringboot.client.dto.GithubRepository;
import dev.jlipka.githubapiclientspringboot.consumer.model.Branch;
import dev.jlipka.githubapiclientspringboot.consumer.model.Repository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GithubMapper {
    @Mapping(source = "commit.sha", target = "lastCommitSha")
    Branch mapToBranch(GithubBranch githubBranch);

    @Mapping(source = "githubRepository.owner.login", target = "owner")
    Repository mapToRepository(GithubRepository githubRepository, List<Branch> branches);

    RepositoryDto mapToRepositoryDto(Repository repository);
}
