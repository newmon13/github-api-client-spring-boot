package dev.jlipka.githubapiclientspringboot.consumer;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class ConsumerConfig {

    @Bean
    public RestClient restClient() {
        return RestClient.create();
    }
}
