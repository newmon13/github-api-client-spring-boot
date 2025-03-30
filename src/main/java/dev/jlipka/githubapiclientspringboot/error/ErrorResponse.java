package dev.jlipka.githubapiclientspringboot.error;

import org.springframework.http.HttpStatus;

public record ErrorResponse(HttpStatus status,  String message) {
}
