
package com.githubanalyzer.controller;

import com.githubanalyzer.model.GitHubProfile;
import com.githubanalyzer.model.GitHubRepository;
import com.githubanalyzer.service.GitHubService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/github")
public class GitHubController {

    private final GitHubService gitHubService;

    public GitHubController(GitHubService gitHubService) {
        this.gitHubService = gitHubService;
    }

    @GetMapping("/health")
    public Map<String, String> healthCheck() {
        return Map.of(
                "status", "UP",
                "message", "GitHub Profile Analyzer backend is running"
        );
    }

    @GetMapping("/{username}")
    public ResponseEntity<?> getUserProfile(@PathVariable String username) {
        try {
            GitHubProfile profile = gitHubService.getUserProfile(username);
            return ResponseEntity.ok(profile);
        } catch (IllegalArgumentException exception) {
            boolean userNotFound = exception.getMessage() != null
                    && exception.getMessage().startsWith("GitHub user not found:");
            HttpStatus status = userNotFound ? HttpStatus.NOT_FOUND : HttpStatus.BAD_REQUEST;
            String message = userNotFound ? "GitHub user not found" : "Invalid GitHub username";
            return ResponseEntity.status(status).body(Map.of("error", message));
        } catch (IllegalStateException exception) {
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body(Map.of("error", "Unable to retrieve profile from GitHub"));
        }
    }

    @GetMapping("/{username}/repositories")
    public List<GitHubRepository> getUserRepositories(@PathVariable String username) {
        return gitHubService.getUserRepositories(username);
    }

    @GetMapping("/{username}/analysis")
    public Map<String, Object> analyzeUserRepositories(@PathVariable String username) {
        return gitHubService.analyzeUserRepositories(username);
    }

    @GetMapping("/{username}/languages")
    public Map<String, Integer> getRepositoryLanguageCounts(@PathVariable String username) {
        return gitHubService.getRepositoryLanguageCounts(username);
    }
}
