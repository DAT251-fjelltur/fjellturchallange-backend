# Issues Life Cycle

## Creating an Issue

Each feature, bugfix, enchantment, documentation, etc. are issues on the GitHub project. When creating an issue three things are required: Title, label, and
project. The title of an issue gives a short description of the issue. The label is what kind of issue this is. The project should always
be `Fjelltur-challange`.

## Creating a Branch for the Issue

See [git branch names](./git_branch_names.md)

## Creating a Pull Request for the Issue

When an issue have been resolved on the issue branch, a pull request should be made from the given branch to `dev`. At least one other member of the back end
team must review the pull request to make sure the code quality is as expected.

### Reviewing Issue

* The pull request must not be merged before the CI checks are done.
* If the issue was solved via pair programming, the ones involved in the pair programming should NOT be one who are reviewing the issue.
* If everyone on the team is involved with the issue, the one with the least involvement should do the reviewing.

## Close Issue

After the pull request have been accepted its branch must be deleted from origin, and the issue closed. It is possible to automatically close issues when the
linked pull request is merged by linking the issue to the pull request. It can be found on the right-hand side of the issue page.
