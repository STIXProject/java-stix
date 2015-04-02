# Contributing to java-stix

We'd welcome you contributing to java-stix. Here are the guidelines to follow:

 - [Code of Conduct](#code_of_conduct)
 - [Question or Problem?](#question)
 - [Issues and Bugs](#issue)
 - [Feature Requests](#feature)
 - [Submission Guidelines](#submission_guidelines)

## <a name="code_of_conduct"></a> Code of Conduct
Help us keep java-stix open and inclusive.

## <a name="question"></a> Got a Question or Problem?
If you have questions about how to use java-stix, please direct these to the [STIX Community Mailing List][list].

## <a name="issue"></a> Found an Issue?
If you find a bug in the source code or a mistake in the documentation, you can help us by submitting an issue to our [GitHub Repository][github]. Please see the Submission Guidelines below.

## <a name="feature"></a> Want a Feature?
You can request a new feature by submitting an issue to our [GitHub Repository][github].

If you would like to implement a new feature on your own, then consider the change's scope:

* **Major Changes** should be discussed first on our [STIX Community Mailing List][list] so that we can better coordinate our efforts, prevent duplication of work, and help you to craft the change so that it is successfully accepted into the project.
* **Small Changes** can be crafted and submitted to the [GitHub Repository][github] as a Pull Request.

## <a name="docs"></a> Want a Documentation Fix?
If you want to help improve the project's documentation, it's a good idea to let others know what you're working to minimize the duplication of effort. Before starting, check out the issue queue, comment on an existing issue to let others know what you're working on, or create a new issue if your work doesn't fit any of the existing issues.

For large changes, before submitting the pull request be sure you haven't accidentally introduced any layout or formatting issues. You should also make sure that your commit message is labeled "docs:" and follows the **Git Commit Guidelines** outlined below.

## <a name="submission_guidelines"></a> Submission Guidelines

### Submitting an Issue
Before you submit your [issue][issues] search the archive, maybe your question was already answered or resolved.

If your issue appears to be a bug, and hasn't been reported, open a new issue. Help us to maximize the effort we can spend fixing issues and adding new features, by not reporting duplicate issues. Providing the following information will increase the chances of your issue being dealt with quickly:

* **Explain** - Explain why this is a bug for you.
* **The Version(s)** - Is it a regression?
* **Steps to Reproduce the Error** - Provide an example. Code is best.
* **Related Issues** - Has a similar issue been reported before?
* **Suggest a Fix** - If you can't fix the bug yourself, perhaps you can point to the line of code or commit that might be causing the problem.

### Submitting a Pull Request
Before you submit your [pull request][pulls] consider the following guidelines:

* Search the existing [pull requests][pulls] for an open or closed Pull Request that relates to your submission to avoid duplicating effort.
* Make your changes in a new git branch:

     ```shell
     git checkout -b my-fix-branch master
     ```

* Create your patch, **including appropriate unit tests**.
* Commit your changes using a descriptive commit message 

     ```shell
     git commit -a
     ```
Note: the optional commit `-a` command line option will automatically "add" and "rm" edited files.

* Push your branch to GitHub:

    ```shell
    git push origin my-fix-branch
    ```

* In GitHub, send a pull request to `java_stix:master`.
* If we suggest changes then:
  * Make the required updates.
  * Rebase your branch and force push to your GitHub repository (this will update your Pull Request):

    ```shell
    git rebase master -i
    git push origin my-fix-branch -f
    ```

#### After your pull request is merged

After your pull request is merged, you can safely delete your branch and pull the changes
from the main (upstream) repository:

* Delete the remote branch on GitHub:

    ```shell
    git push origin --delete my-fix-branch
    ```

* Check out the master branch:

    ```shell
    git checkout master -f
    ```

* Delete the local branch:

    ```shell
    git branch -D my-fix-branch
    ```

* Update your master with the latest upstream version:

    ```shell
    git pull --ff upstream master
    ```

[list]: https://stix.mitre.org/community/registration.html
[github]: https://github.com/nemonik/java_stix
[issues]: https://github.com/nemonik/java_stix/issues
[pulls]: https://github.com/nemonik/java_stix/pulls