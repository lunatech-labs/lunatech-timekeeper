# Timekeeper project

[![Codeship Status for lunatech-labs/lunatech-timekeeper](https://app.codeship.com/projects/352930a0-589d-0138-5f43-3e74b59257eb/status?branch=develop)](https://app.codeship.com/projects/391390)

![Quarkus on develop](https://github.com/lunatech-labs/lunatech-timekeeper/workflows/Java%20CI%20with%20Maven/badge.svg?branch=develop)

![Frontend CI](https://github.com/lunatech-labs/lunatech-timekeeper/workflows/Frontend%20CI/badge.svg?branch=develop)

Why another Toggl you might ask… 

That’s a good question.

We know that there is a lot of open-source solution or SaaS solution (à la Toggl). We decided to bootstrap this project with only one objective : we want to learn.
We want to learn to design a product, to do workshop and brainstorming. We also want to demonstrate what kind of work we’re able to offer to our client. Have a look at the various workshops we did in Chessy. 
Our main purpose is to build a cool playground where we can learn, experiment and build a great tool. 
Toggl is a good tool. We’ve been using it since 2016. However there is some requirements that are not and will never be cover by this tool.

Thus, Timekeeper

Enjoy, have fun and contribute ! 

# Documentation 

All documentation are hold on Confluence pages [here](https://lunatech.atlassian.net/wiki/spaces/INTRANET/pages/1609695253/Timekeeper)

# Agile Dashboard

Timekeeper dashboard is [here](https://lunatechfr.myjetbrains.com/youtrack/issues/TK)

# Technical stack 

All ADRs are on Confluence [here](https://lunatech.atlassian.net/wiki/spaces/INTRANET/pages/1686077447/Technical+architecture#Architecture-decision-records)

# Developer guide

Read [How to setup your development environment](https://lunatech.atlassian.net/wiki/spaces/INTRANET/pages/1879343105/How-to+setup+your+development+environment) 

## Backend 

- Quarkus 1.3
- PostgresSQL 12.2
- Hibernate-panache
- JsonB
- Docker

## FrontEnd

- React

# Getting started

## 1 - Database

The postgreSQL server runs on 5435 with Docker. Username and password for dev are defined in docker-compose.yml and application.properties.

Run docker-compose as :

    docker-compose up 
    
If you want to run docker in background : 

    docker-compose up -d    
    
2 main services will be started :

- PostgreSQL dedicated to TimeKeeper app on port 5435 
- Keycloak (+ one postgres dedicated to Keycloak) on http://localhost:8081

## 2 - Start TimeKeeper

    mvn quarkus:dev
    
We are using Flyway extension. Database's model will be created at the first run of the app.

## 3 - FrontEnd   

Go to the `frontend` folder

Install npm dependencies :

    npm install
    
Start the React application in development mode

    npm run start    

## 4 - Keycloak setup

Keycloak runs on port http://localhost:8082 use admin/admin (configured in docker-compose.yml).

docker compose imports `keycloak/realm-export.json`. This JSON file will create a "Quarkus" Realm and 2 clients : 
- backend-service for Quarkus
- react-ff for the React Frontend

Some users are also automatically imported (alice/alice).

# Git branch and naming convention

Git naming conventions are key. They allow you to understand the context of somebody’s work in a matter of seconds and can help you filter through your team’s work when well executed.

To enforce branch names, each developer should execute this command from its Timekeeper folder 

    git config core.hooksPath .githooks

We use GitFlow convention 

  - Feature branches (feature/*): takes the form of a user story, or a feature that will be merged later on. 
  - Release branches (release/*): support the preparation of new product release, say a future rebranding of your website and will be eventually merged when ready
  - Hotfix branches (hotfix/*): your typical bug fix. You could, for example, branch off in order to fix a bug in production

Other branches:
- `master` is for official release on production, with a Tag
- `develop` is "last-stable" version of the project
- `feature/TK-*' are ephemeral branches for WIP and PR

Try to reuse YouTrack ID as part of branch : 

    feature/TK-84-home-page-design
    feature/TK-42-add-list-all-companies

⚠️ TK must be uppercase ! 

This command configures pre-commit hook and validation.

