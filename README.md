# Timekeeper project

![Quarkus on develop](https://github.com/lunatech-labs/lunatech-timekeeper/workflows/Java%20CI%20with%20Maven/badge.svg?branch=develop)

![Frontend CI](https://github.com/lunatech-labs/lunatech-timekeeper/workflows/Frontend%20CI/badge.svg?branch=develop)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=lunatech-labs_lunatech-timekeeper&metric=alert_status&token=002c82801d0eb45ccc3a82067c18799929110e67)](https://sonarcloud.io/dashboard?id=lunatech-labs_lunatech-timekeeper)

# Intro

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

Timekeeper dashboard is [here](https://lunatech.atlassian.net/jira/software/projects/TK/boards/8)

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

The postgreSQL server runs on 5435 with Docker. Username and password for dev are defined in infrastructure/docker-compose.yml and application.properties.

Run docker-compose as :

    docker-compose up 
    
If you want to run docker in background : 

    docker-compose up -d    
    
2 main services will be started :

- PostgreSQL dedicated to TimeKeeper app on port 5435 
- Keycloak (+ one postgres dedicated to Keycloak) on http://localhost:8081
- Mailhog (a mail service)

## 2 - Start TimeKeeper

    mvn quarkus:dev
    
We are using Flyway extension. Database's model will be created at the first run of the app.

## 2.1 - Start TimeKeeper on Debug (with IntelliJ)

Go to Run/Debug Configuration and add the new configuration by using the `+` button and select `Remote`. 
Use the default configuration (Port 5005).

![DebugConfiguration](https://user-images.githubusercontent.com/45755667/80485053-078d0b00-8959-11ea-9028-e223ef7859f9.png)

By running the `mvn quarkus:dev` command, you will see : `Listening for transport dt_socket at address: 5005`. 
Then start the debug. You can now use the debug mode.

## 2.2 - Run test

    ./mvnw compile test

⚠️ Don't run your tests directly with IntelliJ ! 

There are some issues, probably with maven, IntelliJ will not compile your current code before running.
You must use the command first, then run IntelliJ for more readability if the tests failed.

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

Try to reuse Jira ID as part of branch : 

    feature/TK-84-home-page-design
    feature/TK-42-add-list-all-companies

⚠️ TK must be uppercase ! 

This command configures pre-commit hook and validation.

# Sonarsource

Use mvn with profile "sonar"

    ./mvnw -P sonar clean verify sonar:sonar

Tests coverage are generated as XML file, as described [on sonarqube-scanner-maven](https://github.com/SonarSource/sonar-scanning-examples/tree/master/sonarqube-scanner-maven/maven-basic) sample project
    
# Fast tests : how to execute only H2 Test

The integration suite is a bit slow with Docker + Keycloak. When you want to execute simple tests, please set ENV=fast-test-only

    export ENV=fast-test-only
    ./mvnw test
    
You can annotate a test with @DisabledIfEnvironmentVariable if your test is slow

    @DisabledIfEnvironmentVariable(named = "ENV", matches = "fast-test-only")        

