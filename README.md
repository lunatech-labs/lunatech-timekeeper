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
See this [How-To install JDK on MacOS with brew](https://medium.com/w-logs/installing-java-11-on-macos-with-homebrew-7f73c1e9fadf)

    brew update
    brew tap homebrew/cask-versions
    brew cask install java11

I recommend that you install `jenv` with brew. This utility let you manage various version of Java.

To see the full list of installed JDK : 

    /usr/libexec/java_home -V
    
To add Jdk11 to jenv : 

    jenv add /Library/Java/JavaVirtualMachines/openjdk-11.0.2.jdk/Contents/Home
    
You can then go to the Timekeeper folder and set the local Java version : 

    cd lunatech-timekeeper
    jenv local 11.0
                
## Fix Java 11 issues

If you get this error with `./mvnw quarkus:dev`
                
    [ERROR] Failed to execute goal io.quarkus:quarkus-maven-plugin:1.3.1.Final:dev (default-cli) on project timekeeper: Fatal error compiling: invalid target release: 11 -> [Help 1]

Check JAVA_HOME

    echo $JAVA_HOME
    export JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-11.0.2.jdk/Contents/Home

Check the value in $HOME/.mavenrc => it should contains Java 11. Or delete this file from your Home folder

Make sure that Maven loads the correct Java version :

    nicolas:lunatech-timekeeper nmartignole$ ./mvnw --version
    Apache Maven 3.6.3 (cecedd343002696d0abb50b32b541b8a6ba2883f)
    Maven home: /Users/nmartignole/.m2/wrapper/dists/apache-maven-3.6.3-bin/1iopthnavndlasol9gbrbg6bf2/apache-maven-3.6.3
    Java version: 11.0.2, vendor: Oracle Corporation, runtime: /Library/Java/JavaVirtualMachines/openjdk-11.0.2.jdk/Contents/Home
    Default locale: fr_FR, platform encoding: UTF-8
    OS name: "mac os x", version: "10.13.6", arch: "x86_64", family: "mac"

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

Go to the `frontend` folder and check the README.md

## 4 - Keycloak setup

Keycloak runs on port http://localhost:8082 use admin/admin (configured in docker-compose.yml).


