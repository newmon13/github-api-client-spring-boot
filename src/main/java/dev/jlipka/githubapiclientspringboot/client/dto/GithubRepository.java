package dev.jlipka.githubapiclientspringboot.client.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GithubRepository(
        String name,
        GithubOwner owner,
        boolean isFork
) {
}
