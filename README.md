# Timekeeper project

Why another Toggl you might ask… 

That’s a good question.

We know that there is a lot of open-source solution or SaaS solution (à la Toggl). We decided to bootstrap this project with only one objective : we want to learn.
We want to learn to design a product, to do workshop and brainstorming. We also want to demonstrate what kind of work we’re able to offer to our customer. Have a look at the various workshops we did in Chessy. 
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

Java 11 is required. 

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

Go to the `frontend` folder and check the README.md

