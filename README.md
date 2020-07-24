# Timekeeper project

![Quarkus on develop](https://github.com/lunatech-labs/lunatech-timekeeper/workflows/Java%20CI%20with%20Maven/badge.svg?branch=develop)

![Frontend CI](https://github.com/lunatech-labs/lunatech-timekeeper/workflows/Frontend%20CI/badge.svg?branch=develop)

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=lunatech-labs_lunatech-timekeeper&metric=alert_status&token=002c82801d0eb45ccc3a82067c18799929110e67)](https://sonarcloud.io/dashboard?id=lunatech-labs_lunatech-timekeeper)

# Intro

Welcome to the TimeKeeper project page.
This open-source project, distributed under the Apache 2.0 license, was created by Lunatech. It allows to show you a complete example of a time tracking application.
We built this software first for Lunatech employees. The tool allows us to track the time spent by project and by customer. We use this tool every month, in order to edit time reports and invoices.

We decided to make the code open-source. We also plan to create a service for companies interested in this project, but who may not be able to install the software.

If you wish to contribute or propose corrections, please read our contribution rules. 

You can contact Lunatech by email: timekeeper@lunatech.com

# Documentation 

All documentation are hold on Confluence pages [here](https://lunatech.atlassian.net/wiki/spaces/INTRANET/pages/1609695253/Timekeeper)

# Agile Dashboard

Timekeeper dashboard is [here](https://lunatech.atlassian.net/jira/software/projects/TK/boards/8)

# Technical stack 

All ADR (Architecture Decision Records) are on Confluence [here](https://lunatech.atlassian.net/wiki/spaces/INTRANET/pages/1686077447/Technical+architecture#Architecture-decision-records)

# Developer guide

Read [How to setup your development environment](https://github.com/lunatech-labs/lunatech-timekeeper/blob/develop/DEV_ENVIRONMENT.md) 
Feel free to contribute for other platforms (Linux, Windows...) if you wish. PR for documentations are welcome. 

## Backend 

- Quarkus 1.3 
- Java 11
- Graal VM 19.3.1
- PostgresSQL 12.2
- Hibernate-panache
- Jackson
- Docker
- Keycloak 10.x

## FrontEnd

- React 0.16
- AntD library
- Less

The UX/UI design was made by Geoffroy Bouet, Lunatech.

# Getting started

## 1 - Database

The postgreSQL server runs on 5435 with Docker. Username and password for dev are defined in infrastructure/docker-compose.yml and application.properties.

Run docker-compose as :

    docker-compose up 
    
If you want to run docker in background : 

    docker-compose up -d    
    
The following services should start :

- PostgreSQL dedicated to TimeKeeper app on port 5435 
- Keycloak (+ one postgres dedicated to Keycloak) on http://localhost:8081
- Mailhog (a mail service)

## 2 - Start TimeKeeper

    mvn quarkus:dev
    
This project relies on Flyway for database schema migration. Database's model will be created at the first run of the app.

## 2.1 - Start TimeKeeper on Debug (with IntelliJ)

Go to Run/Debug Configuration and add the new configuration by using the `+` button and select `Remote`. 
Use the default configuration (Port 5005).

![DebugConfiguration](https://user-images.githubusercontent.com/45755667/80485053-078d0b00-8959-11ea-9028-e223ef7859f9.png)

By running the `mvn quarkus:dev` command, you will see : `Listening for transport dt_socket at address: 5005`. 
Then start the debug. You can now use the debug mode.

## 2.2 - Run test

    ./mvnw compile test


## 3 - Front End   

Go to the `frontend` folder and check the [README](https://github.com/lunatech-labs/lunatech-timekeeper/blob/develop/frontend/README.md) for more details

## 4 - Keycloak setup

Keycloak runs on port http://localhost:8082 use admin/admin (configured in docker-compose.yml).

docker compose imports `keycloak/realm-export.json`. This JSON file will create a "Quarkus" Realm and 2 clients : 
- backend-service for Quarkus
- react-ff for the React Frontend

For Cypress end-to-end tests, a specific test user should be manually created. See [README](https://github.com/lunatech-labs/lunatech-timekeeper/blob/develop/frontend/README.md) for more details

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

