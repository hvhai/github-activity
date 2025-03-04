# Github User Activity CLI with Kotlin
> Sample solution for the [Github User Activity](https://roadmap.sh/projects/github-user-activity) challenge from [roadmap.sh](https://roadmap.sh/).

## 🎯 Overview
This is a simple CLI application that allows you to fetch the recent activity of a GitHub user and display it in the terminal. Also handle the API error responses by displaying a proper error message.

## 🚀 How to Run
1. Clone the repository:

    ```bash
    git clone https://github.com/hvhai/github-activity.git
    cd github-activity
    ```
2. Run the application:
- Using Gradle:
    ```bash
    ./gradlew --console=plain -q run
    ```
- Using IDE: \
  Compile and run `Main.kt` file.

## 📘 Usage Example

```shell
github-activity kamranahmedse
Output:
 - Starred philippgille/chromem-go
 - Starred wickedest/Mergely
 - Pushed 1 commits to kamranahmedse/developer-roadmap
 - Closed a pull request in kamranahmedse/developer-roadmap
 - Pushed 1 commits to arikchakma/maily.to
 - Pushed 1 commits to arikchakma/maily.to
 - Pushed 1 commits to arikchakma/maily.to
 - Pushed 1 commits to arikchakma/maily.to
 - Pushed 1 commits to arikchakma/maily.to
 - Starred logseq/logseq
 - Created a branch in kamranahmedse/developer-roadmap
 - Pushed 1 commits to kamranahmedse/developer-roadmap
 - Starred asim/reminder
 - Starred ishubin/schemio
 - Pushed 1 commits to kamranahmedse/developer-roadmap
 - Closed a pull request in kamranahmedse/developer-roadmap
 - Closed a pull request in kamranahmedse/developer-roadmap
 - Pushed 1 commits to kamranahmedse/developer-roadmap
 - Starred react-grid-layout/react-grid-layout
 - Pushed 2 commits to kamranahmedse/developer-roadmap
 - Pushed 1 commits to kamranahmedse/developer-roadmap
 - Pushed 1 commits to kamranahmedse/developer-roadmap
 - Pushed 2 commits to kamranahmedse/developer-roadmap
 - Created an issue comment in kamranahmedse/developer-roadmap
 - Closed an issue in kamranahmedse/developer-roadmap
 - Created an issue comment in kamranahmedse/developer-roadmap
 - Closed an issue in kamranahmedse/developer-roadmap
 - Created an issue comment in kamranahmedse/developer-roadmap
 - Closed an issue in kamranahmedse/developer-roadmap
 - Pushed 1 commits to kamranahmedse/developer-roadmap
# case of user not found
github-activity oweirj8929
Failed to get events: statusCode=404: Message=
# case of no events
github-activity test
Output:
# exit CLI
github-activity exit
Exiting...
```