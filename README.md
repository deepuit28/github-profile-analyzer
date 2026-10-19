# GitHub Profile Analyzer

A Java and Spring Boot REST API for retrieving public GitHub profile and repository data and summarizing repository activity. The application calls the GitHub REST API and returns profile, repository, and analysis results as JSON.

## Features

- Look up a GitHub user's public profile.
- List the user's public repositories.
- Calculate total stars and forks across the repositories returned by GitHub.
- Identify the most-starred repository.
- Count repositories by programming language and identify the most-used language.
- Check whether the backend is running with a health endpoint.

> **Repository results:** The current implementation uses GitHub's default repository-list page (up to 30 repositories). Analysis and language counts are calculated from the repositories returned on that page.

## Tech stack

- Java 21
- Spring Boot 3.5.6
- Maven
- GitHub REST API

## API endpoints

The examples below assume the application is running locally on port `8080`. Replace `octocat` with a GitHub username.

| Method | Endpoint | Description |
| --- | --- | --- |
| `GET` | `http://localhost:8080/api/github/health` | Backend health check |
| `GET` | `http://localhost:8080/api/github/octocat` | Public profile details |
| `GET` | `http://localhost:8080/api/github/octocat/repositories` | Public repositories |
| `GET` | `http://localhost:8080/api/github/octocat/analysis` | Repository totals and summary |
| `GET` | `http://localhost:8080/api/github/octocat/languages` | Repository counts grouped by language |

### Example responses

**Health**

```json
{
  "status": "UP",
  "message": "GitHub Profile Analyzer backend is running"
}
```

**Profile**

```json
{
  "username": "octocat",
  "name": "The Octocat",
  "bio": null,
  "avatarUrl": "https://avatars.githubusercontent.com/u/583231?v=4",
  "publicRepos": 8,
  "followers": 10000,
  "following": 9,
  "profileUrl": "https://github.com/octocat"
}
```

**Repositories**

```json
[
  {
    "name": "Hello-World",
    "description": "My first repository on GitHub!",
    "htmlUrl": "https://github.com/octocat/Hello-World",
    "language": null,
    "stargazersCount": 3000,
    "forksCount": 3500
  }
]
```

**Analysis**

```json
{
  "totalStars": 4200,
  "totalForks": 5100,
  "totalPublicRepositories": 8,
  "mostStarredRepository": "Spoon-Knife",
  "mostUsedLanguage": "JavaScript"
}
```

**Languages**

```json
{
  "JavaScript": 3,
  "Java": 2,
  "Python": 1
}
```

The example data is illustrative; actual results depend on the GitHub account and the repositories returned by GitHub.

## Setup and run on Windows

### Requirements

- JDK 21
- IntelliJ IDEA
- Maven installed and available as `mvn` in your terminal
- Internet access to reach the GitHub REST API

### Run with IntelliJ IDEA

1. Open the project folder in IntelliJ IDEA.
2. Allow IntelliJ to load the Maven project and configure a JDK 21 project SDK.
3. Open `src\main\java\com\githubanalyzer\GitHubProfileAnalyzerApplication.java`.
4. Run `GitHubProfileAnalyzerApplication` using the Run icon beside its `main` method.
5. Visit `http://localhost:8080/api/github/health` to confirm the backend is running.

### Run with Maven

Open PowerShell or Command Prompt in the project root (the folder containing `pom.xml`) and run:

```powershell
mvn spring-boot:run
```

When startup completes, call the endpoints listed above using a browser, `curl`, or an API client.

## Future improvements

- Add pagination to retrieve and analyze all public repositories, not only the default API page.
- Add automated tests for service logic, endpoint responses, and error cases.
- Improve consistent error-response handling across all endpoints.
- Support configurable GitHub API credentials to increase rate limits.
- Add optional sorting and filtering for repository results.
- Build a frontend to visualize profile and repository statistics.
