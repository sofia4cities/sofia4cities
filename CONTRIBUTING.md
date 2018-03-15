Welcome to the Sofia4Cities community
============================


As an open-source project, [Sofia4Cities IoT Platform](https://www.sofia4cities.com) is waiting for your contributions. 

Feel free to contribute to Sofia4Cities project repositories on [GitHub](https://github.com/sofia4cities/sofia4cities/).

## Contribute to Sofia4Cities Community
You can contribute to the Community en various ways:

- Creating and submitting sample applications
- Resolving bugs in modules and components
- Creating new components and plugins that extend the plataform
- Improving our tests and quality of the code
- Collaborating in the documentation: Documentation is a part of the Sofia4Cities code base, you can find the documentation files in the [Docs](https://github.com/sofia4cities/sofia4cities/docs/) subdirectory of the main repository.

The contribution process is the same for the source code and documentation.

To contribute to project on [GitHub](https://github.com/sofia4cities/sofia4cities.git), use the [GitHub flow](https://guides.github.com/introduction/flow/).
It means that you should branch from the main repository and contribute back by making [pull requests](https://help.github.com/articles/using-pull-requests/).
(That is a generally accepted process flow on GitHub).

## Getting started

To follow the instructions in this guide and start contributing to S4C project on GitHub:


1. [Fork](https://help.github.com/articles/fork-a-repo/) the [main repository](https://github.com/sofia4cities/sofia4cities.git).
2. [Clone](https://help.github.com/articles/cloning-a-repository/) your new repository. To do this, run the following command.
```sh
git clone git@github.com:<your_github_name>/sofia4cities.git # Replace <your_github_name> with your GitHub profile name.
cd sofia4cities
```
3. To synchronize with the main repository, add it to the remotes:
```sh
git remote add upstream https://github.com/sofia4cities/sofia4cities.git
```

Now your **upstream** points to **sofia4cities/sofia4cities**.

## Branches

The **master** branch represents the latest development version of Sofia4Cities.
Most changes should go there.

The **release-x.x** branches are used as stabilizing branches for maintenance releases.
All actual releases are tagged.

If you want to backport a bug fix, open your pull request (PR) against the appropriate release branch.

### Testing

All pull requests will be automatically tested using [Travis](https://travis-ci.org/) and [Jenkins](https://jenkins.io/).
In case some tests fail, fix the issues or describe why the fix cannot be done.

### Review

Every pull request is reviewed by the assigned team members as per standard [GitHub procedure](https://help.github.com/articles/about-pull-request-reviews/).
Reviewers can comment on a PR, approve it, or request changes to it.
A PR can be merged when it is approved by at least two assigned reviewers and has no pending requests for changes.

### Gatekeepers

Gatekeeper is a person responsible for the final review and merge.
They are also responsible for managing Git repositories.
Only gatekeepers can write to **master** and release branches.

