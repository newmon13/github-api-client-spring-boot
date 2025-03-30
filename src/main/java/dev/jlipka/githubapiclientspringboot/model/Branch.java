package dev.jlipka.githubapiclientspringboot.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public record Branch(
        String name,
        @JsonProperty(value = "last_commit_sha")
        String lastCommitSha
) {
}
