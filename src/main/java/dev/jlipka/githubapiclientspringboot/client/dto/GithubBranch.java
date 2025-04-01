package dev.jlipka.githubapiclientspringboot.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GithubBranch(
        String name,
        GithubCommit commit
) {
}