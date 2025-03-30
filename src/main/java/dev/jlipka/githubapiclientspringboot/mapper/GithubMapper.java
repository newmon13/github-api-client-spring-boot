package dev.jlipka.githubapiclientspringboot.mapper;

import dev.jlipka.githubapiclientspringboot.dto.github.GithubBranch;
import dev.jlipka.githubapiclientspringboot.dto.github.GithubOwner;
import dev.jlipka.githubapiclientspringboot.dto.github.GithubRepository;
import dev.jlipka.githubapiclientspringboot.model.Branch;
import dev.jlipka.githubapiclientspringboot.model.Repository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface GithubMapper {
    @Mapping(source = "commit.sha", target = "lastCommitSha")
    Branch mapToBranch(GithubBranch githubBranch);

    String mapToOwnerName(GithubOwner githubOwner);

    Repository mapToRepository(GithubRepository githubRepository, List<Branch> branches);
}
