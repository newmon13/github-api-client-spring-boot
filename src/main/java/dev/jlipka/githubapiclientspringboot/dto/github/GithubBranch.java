package dev.jlipka.githubapiclientspringboot.dto.github;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GithubBranch(
        String name,
        GithubCommit commit
) {
}