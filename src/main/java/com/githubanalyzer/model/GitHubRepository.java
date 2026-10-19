package com.githubanalyzer.model;

public class GitHubRepository {

    private String name;
    private String description;
    private String htmlUrl;
    private String language;
    private int stargazersCount;
    private int forksCount;

    public GitHubRepository() {
    }

    public GitHubRepository(
            String name,
            String description,
            String htmlUrl,
            String language,
            int stargazersCount,
            int forksCount
    ) {
        this.name = name;
        this.description = description;
        this.htmlUrl = htmlUrl;
        this.language = language;
        this.stargazersCount = stargazersCount;
        this.forksCount = forksCount;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getHtmlUrl() {
        return htmlUrl;
    }

    public String getLanguage() {
        return language;
    }

    public int getStargazersCount() {
        return stargazersCount;
    }

    public int getForksCount() {
        return forksCount;
    }
}
