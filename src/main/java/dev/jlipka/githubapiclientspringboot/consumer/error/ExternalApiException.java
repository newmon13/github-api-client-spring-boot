package dev.jlipka.githubapiclientspringboot.consumer.error;

import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpClientErrorException;

public class ExternalApiException extends HttpClientErrorException {
    public ExternalApiException(HttpStatusCode statusCode, String message) {
        super(statusCode, message);
    }
}