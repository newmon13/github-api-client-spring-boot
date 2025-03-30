package dev.jlipka.githubapiclientspringboot.model;

import dev.jlipka.githubapiclientspringboot.dto.github.GithubBranch;

import java.util.List;

public record Repository(String name, String owner, List<GithubBranch> branches) {
}
