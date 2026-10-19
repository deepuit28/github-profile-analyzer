
package com.githubanalyzer.model;

public class GitHubProfile {

    private String username;
    private String name;
    private String bio;
    private String avatarUrl;
    private int publicRepos;
    private int followers;
    private int following;
    private String profileUrl;

    public GitHubProfile() {
    }

    public GitHubProfile(
            String username,
            String name,
            String bio,
            String avatarUrl,
            int publicRepos,
            int followers,
            int following,
            String profileUrl
    ) {
        this.username = username;
        this.name = name;
        this.bio = bio;
        this.avatarUrl = avatarUrl;
        this.publicRepos = publicRepos;
        this.followers = followers;
        this.following = following;
        this.profileUrl = profileUrl;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getBio() {
        return bio;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public int getPublicRepos() {
        return publicRepos;
    }

    public int getFollowers() {
        return followers;
    }

    public int getFollowing() {
        return following;
    }

    public String getProfileUrl() {
        return profileUrl;
    }
}
