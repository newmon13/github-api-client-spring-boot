package dev.jlipka.githubapiclientspringboot.controller;

import dev.jlipka.githubapiclientspringboot.GithubApiClientSpringBootApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest(classes = GithubApiClientSpringBootApplication.class)
class UserReposControllerIntegrationTest {

    @Autowired
    MockMvc mockMvc;

    @Test
    public void shouldGetRepositoryOfExistingUser() throws Exception {
        mockMvc.perform(get("/github/octocat").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(8)))
                .andExpect(jsonPath("$[0].name", is("boysenberry-repo-1")))
                .andExpect(jsonPath("$[0].owner", is("octocat")))
                .andExpect(jsonPath("$[0].branches", hasSize(8)))
                .andExpect(jsonPath("$[0].branches[0].name", is("LyaLya-pie-patch")))
                .andExpect(jsonPath("$[0].branches[0].last_commit_sha", is("0f69ad5cf7ff57e91487568cdd2d9c8a9d2e855a")));
    }

    @Test
    public void shouldFailAndReturn404StatusAndUserDoesNotExist() throws Exception {
        mockMvc.perform(get("/github/hfghfhdthdthdhdfhdfhdfgdgf").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.message", is("Could not find github user with given username")));
    }
}