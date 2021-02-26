# Git branch conventions

We will be using [git flow](https://github.com/nvie/gitflow) for branch name conventions.

## Git branch name conventions

1. Each branch should start with the id of the issue followed by a dash (`-`)
   1. There should always be three digits for the issue number
   2. For example issue `1` must be written as `001-`
2. After issue number it should have a short (<50 chars) describing name.
   1. Use dash `-` for space replacement.
   2. DOT NOT USE UNDERSCORE `_` IN THE BRANCH NAME.

## Examples

* `feature/003-add-steering-wings`
  * Created with `git flow feature start 003-add-steering-wings`
* `bugfix/123-fix-off-by-one-in-haunted-house`
  * Created with `git flow bugfix start 123-fix-off-by-one-in-haunted-house`

## Usage

See [cheatsheet](https://danielkummer.github.io/git-flow-cheatsheet/index.html)

## Git flow branch types

| Branch type | desc                                  |
| :---------- | :------------------------------------ |
| Feature     | Will add a new feature to the project |
| bugfix      | Will fix a bug within the program     |
| release     | Bump version in as a release          |
| hotfix      | Bump version in as a hotfix           |
| support     | Not to be used (No one to support)    |

## Installation

Follow the [official guide](https://github.com/nvie/gitflow/wiki/Installation).

Execute `git flow init` in the shell, press enter once then change `Branch name for "next release" development` to `dev`. Press enter till the program exits

### Example output

```bash
$ git flow init
Initialized empty Git repository in <whereever>/fjellturchallenge-backend/.git/
No branches exist yet. Base branches must be created now.
Branch name for production releases: [master] 
Branch name for "next release" development: [develop] dev

How to name your supporting branch prefixes?
Feature branches? [feature/] 
Bugfix branches? [bugfix/] 
Release branches? [release/] 
Hotfix branches? [hotfix/] 
Support branches? [support/] 
Version tag prefix? [] 
Hooks and filters directory? [<whereever>/fjellturchallenge-backend/.git/hooks]
```
