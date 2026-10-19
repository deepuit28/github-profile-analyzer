package com.githubanalyzer.service;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.githubanalyzer.model.GitHubProfile;
import com.githubanalyzer.model.GitHubRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestClientResponseException;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

@Service
public class GitHubService {

    private static final String GITHUB_USER_URL = "https://api.github.com/users/{username}";
    private static final String GITHUB_USER_REPOSITORIES_URL =
            "https://api.github.com/users/{username}/repos";
    private static final Pattern VALID_USERNAME = Pattern.compile(
            "^[A-Za-z0-9](?:[A-Za-z0-9-]{0,37}[A-Za-z0-9])?$"
    );

    private final RestClient restClient = RestClient.create();

    public GitHubProfile getUserProfile(String username) {
        if (username == null || username.isBlank() || !VALID_USERNAME.matcher(username).matches()) {
            throw new IllegalArgumentException("Invalid GitHub username: " + username);
        }

        try {
            GitHubUserResponse response = restClient.get()
                    .uri(GITHUB_USER_URL, username)
                    .retrieve()
                    .body(GitHubUserResponse.class);

            if (response == null) {
                throw new IllegalStateException("GitHub returned an empty response for user: " + username);
            }

            return new GitHubProfile(
                    response.login,
                    response.name,
                    response.bio,
                    response.avatarUrl,
                    response.publicRepos,
                    response.followers,
                    response.following,
                    response.profileUrl
            );
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new IllegalArgumentException("GitHub user not found: " + username, exception);
            }
            throw new IllegalStateException(
                    "GitHub API rejected the request with status " + exception.getStatusCode(),
                    exception
            );
        } catch (RestClientResponseException exception) {
            throw new IllegalStateException(
                    "GitHub API request failed with status " + exception.getStatusCode(),
                    exception
            );
        } catch (RestClientException exception) {
            throw new IllegalStateException("Unable to communicate with the GitHub API", exception);
        }
    }

    public List<GitHubRepository> getUserRepositories(String username) {
        if (username == null || username.isBlank() || !VALID_USERNAME.matcher(username).matches()) {
            throw new IllegalArgumentException("Invalid GitHub username: " + username);
        }

        try {
            GitHubRepositoryResponse[] response = restClient.get()
                    .uri(GITHUB_USER_REPOSITORIES_URL, username)
                    .retrieve()
                    .body(GitHubRepositoryResponse[].class);

            if (response == null) {
                throw new IllegalStateException(
                        "GitHub returned an empty repository response for user: " + username
                );
            }

            return Arrays.stream(response)
                    .map(repository -> new GitHubRepository(
                            repository.name,
                            repository.description,
                            repository.htmlUrl,
                            repository.language,
                            repository.stargazersCount,
                            repository.forksCount
                    ))
                    .toList();
        } catch (HttpClientErrorException exception) {
            if (exception.getStatusCode() == HttpStatus.NOT_FOUND) {
                throw new IllegalArgumentException("GitHub user not found: " + username, exception);
            }
            throw new IllegalStateException(
                    "GitHub API rejected the request with status " + exception.getStatusCode(),
                    exception
            );
        } catch (RestClientResponseException exception) {
            throw new IllegalStateException(
                    "GitHub API request failed with status " + exception.getStatusCode(),
                    exception
            );
        } catch (RestClientException exception) {
            throw new IllegalStateException("Unable to communicate with the GitHub API", exception);
        }
    }

    public Map<String, Object> analyzeUserRepositories(String username) {
        List<GitHubRepository> repositories = getUserRepositories(username);

        int totalStars = 0;
        int totalForks = 0;
        GitHubRepository mostStarredRepository = null;
        Map<String, Integer> languageCounts = new LinkedHashMap<>();

        for (GitHubRepository repository : repositories) {
            totalStars += repository.getStargazersCount();
            totalForks += repository.getForksCount();

            if (mostStarredRepository == null
                    || repository.getStargazersCount() > mostStarredRepository.getStargazersCount()) {
                mostStarredRepository = repository;
            }

            String language = repository.getLanguage();
            if (language != null && !language.isBlank()) {
                languageCounts.merge(language, 1, Integer::sum);
            }
        }

        String mostUsedLanguage = null;
        int highestLanguageCount = 0;
        for (Map.Entry<String, Integer> languageEntry : languageCounts.entrySet()) {
            if (languageEntry.getValue() > highestLanguageCount) {
                mostUsedLanguage = languageEntry.getKey();
                highestLanguageCount = languageEntry.getValue();
            }
        }

        Map<String, Object> analysis = new LinkedHashMap<>();
        analysis.put("totalStars", totalStars);
        analysis.put("totalForks", totalForks);
        analysis.put("totalPublicRepositories", repositories.size());
        analysis.put(
                "mostStarredRepository",
                mostStarredRepository == null ? null : mostStarredRepository.getName()
        );
        analysis.put("mostUsedLanguage", mostUsedLanguage);
        return analysis;
    }

    public Map<String, Integer> getRepositoryLanguageCounts(String username) {
        List<GitHubRepository> repositories = getUserRepositories(username);
        Map<String, Integer> languageCounts = new LinkedHashMap<>();

        for (GitHubRepository repository : repositories) {
            String language = repository.getLanguage();
            if (language != null && !language.isBlank()) {
                languageCounts.merge(language, 1, Integer::sum);
            }
        }

        return languageCounts;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class GitHubUserResponse {
        @JsonProperty("login")
        private String login;

        @JsonProperty("name")
        private String name;

        @JsonProperty("bio")
        private String bio;

        @JsonProperty("avatar_url")
        private String avatarUrl;

        @JsonProperty("public_repos")
        private int publicRepos;

        @JsonProperty("followers")
        private int followers;

        @JsonProperty("following")
        private int following;

        @JsonProperty("html_url")
        private String profileUrl;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class GitHubRepositoryResponse {
        @JsonProperty("name")
        private String name;

        @JsonProperty("description")
        private String description;

        @JsonProperty("html_url")
        private String htmlUrl;

        @JsonProperty("language")
        private String language;

        @JsonProperty("stargazers_count")
        private int stargazersCount;

        @JsonProperty("forks_count")
        private int forksCount;
    }
}
