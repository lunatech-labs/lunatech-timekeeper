# How-to setup your development environment

This document is a guide to help you set-up and configure your local environment for Timekeeper.

# Pre-requisites 

For Mac users, install Homebrew

https://brew.sh/

Do an update on brew 

```
brew update
```

## Java

Quarkus 1.3 requires Java 11.

```
brew update
brew tap homebrew/cask-versions
brew cask install java11
```

To see the list of installed JDK : 

```
/usr/libexec/java_home -V
```

## JEnv 

Jenv is a command line tool to help you forget how to set the JAVA_HOME environment variable

[JEnv web site](https://www.jenv.be/)

Create a .jenv folder in your home directoty

```
cd $HOME
mkdir .jenv
```

Install JEnv using homebrew :

```console
brew install jenv
```

Add Java 11 as a new available Java version

```console
jenv add /Library/Java/JavaVirtualMachines/openjdk-11.0.2.jdk/Contents/Home    
```

Go to the timekeeper folder and use jenv to set Java 11 as the local Java version 

```console
cd lunatech-timekeeper
jenv local 11.0
```

Final check :

```console
nicolas:lunatech-timekeeper nmartignole$ java -version
openjdk version "11.0.2" 2019-01-15
OpenJDK Runtime Environment 18.9 (build 11.0.2+9)
OpenJDK 64-Bit Server VM 18.9 (build 11.0.2+9, mixed mode)
```

## node

install node with brew

```console
brew update
brew install node
```

Min. required version : node 13.12.0

## npm

```console
brew update
brew install npm

npm – version : 6.9.0
```

# Native packaging (optional)

The Timekeeper backend is based on Quarkus (see [this article](https://lunatech.fr/2020/01/25/quarkus-java-introduction/) on Lunatech blog). 

We describe how to build your application and to run-it as a native binary.

Download **GRAALVM 19.3.1** archive from here : [https://github.com/graalvm/graalvm-ce-builds/releases/tag/vm-19.3.1](https://github.com/graalvm/graalvm-ce-builds/releases/tag/vm-19.3.1)

For MacOs user, select following link :  graalvm-ce-java11-darwin-amd64-19.3.1.tar.gz

Uncompress archive

```console
tar zxf graalvm-ce-java11-darwin-amd64-19.3.1.tar.gz
cd graalvm-ce-java11-19.3.1/Contents/Home
```

Go to the `GRAALVM Home/bin` directory and run the following command :

```console
cd bin
./gu install native-image
```

Move the folder to the standard java folder on your Mac :

```console
cd /Library/Java/JavaVirtualMachines
sudo mv ~/Downloads/graalvm-ce-java11-19.3.1 .
```

Add the folder to jenv for later 

```console
jenv add /Library/Java/JavaVirtualMachines/graalvm-ce-java11-19.3.1/Contents/Home
```

You can also set the java version in the Timekeeper folder

```console
nic@mac > cd ~/Dev/lunatech-timekeeper
nic@mac > jenv version 11.0.6
11.0 (set by /Users/nmartignole/Dev/Lunatech/lunatech-timekeeper/.java-version)
```

Export following variables based on where you uncompress GRAALVM archive :

```console
export GRAALVM_HOME=/Library/Java/JavaVirtualMachines/graalvm-ce-java11-19.3.1/Contents/Home
export JAVA_HOME=/Library/Java/JavaVirtualMachines/graalvm-ce-java11-19.3.1/Contents/Home
```

# How to package Timekeeper and run-it in production mode

Now you are ready to build a native GraalVM package of Timekeep App :

```console
nic@mac > cd ~/Dev/lunatech-timekeeper
nic@mac > ./mvnw clean package -Pnative
```

Building time is (very) long... (more than 4mn)

/!\ docker-compose up -d must be ran before so that Keycloak and postgreSQL instances are started.

Keycloak must be started and running on port 8082 (started with docker). Check also the "Credentials" for
Clients > timekeeper-quarkus-backend on the Keycloak admin. Here the secret is set to `ae4c5ec2-7514-43e9-b026-dafc4eb52453`

When the process is finished you can start app by the following commands :

```console
 ./target/timekeeper-1.0.0-SNAPSHOT-runner \
 -Dquarkus.datasource.jdbc.url=jdbc:postgresql://localhost:5435/timekeeper \
 -Dquarkus.datasource.username=keycloak \
 -Dquarkus.datasource.password=admin \
 -Dquarkus.http.cors.origins=http://localhost:3000,http://127.0.0.1:3000,http://localhost:8090,http://127.0.0.1:8090 \
 -Dquarkus.oidc.auth-server-url=http://localhost:8082/auth/realms/Timekeeper \
 -Dquarkus.oidc.credentials.secret=ae4c5ec2-7514-43e9-b026-dafc4eb52453
 ```
 
 Please note also that the HTTP port in production mode is 8090, see [http://localhost:8090](http://localhost:8090)

Check the application health http://localhost:8090/api/health/ready
 
# Tips

Tips

MacOs user : If you get the following message : 

```console
fabrice@lunatech > output included error: xcrun: error: invalid active developer, 
```

use the following command to install missing package : 

```console
xcode-select --install
```console
```

# Troubleshooting

If you get this error with `./mvnw quarkus:dev`

```console
[ERROR] Failed to execute goal io.quarkus:quarkus-maven-plugin:1.3.1.Final:dev (default-cli) on project timekeeper: Fatal error compiling: invalid target release: 11 -> [Help 1]
```

- Check JAVA_HOME
- Check the value in $HOME/.mavenrc => it should contains Java 11. Or delete this file from your Home folder

```console
echo $JAVA_HOME
export JAVA_HOME=/Library/Java/JavaVirtualMachines/openjdk-11.0.2.jdk/Contents/Home
```

Make sure that Maven loads the correct Java version :

```console
nicolas:lunatech-timekeeper nmartignole$ ./mvnw --version

Apache Maven 3.6.3 (cecedd343002696d0abb50b32b541b8a6ba2883f)
Maven home: /Users/nmartignole/.m2/wrapper/dists/apache-maven-3.6.3-bin/1iopthnavndlasol9gbrbg6bf2/apache-maven-3.6.3
Java version: 11.0.2, vendor: Oracle Corporation, runtime: /Library/Java/JavaVirtualMachines/openjdk-11.0.2.jdk/Contents/Home
Default locale: fr_FR, platform encoding: UTF-8
OS name: "mac os x", version: "10.13.6", arch: "x86_64", family: "mac"
```

End of document
