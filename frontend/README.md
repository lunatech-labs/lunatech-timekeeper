# Timekeeper React Frontend

# Pre-requisites 

`npm` must be installed

    npm 6.9.0 installed with brew install npm
    node v10.15.3 installed with brew install node

You can also use `yarn`. 

Do not commit package-lock.json to Git, or yarn.lock.

If you encounter errors with node on Mac OS X : [check this stackoverflow question](https://stackoverflow.com/questions/44363066/error-cannot-find-module-lib-utils-unsupported-js-while-using-ionic)

## Environment Prepare

Install `node_modules`:

```bash
npm install
```

or

```bash
yarn install
```

## Development 

```bash
npm start
```
or

```bash
yarn start
```

This starts a local node server and the TimeKeeper React app should be on http://localhost:3000/
    
## Available Scripts

In the project directory, you can run:

### `npm start`

Runs the app in the development mode.<br />
Open [http://localhost:3000](http://localhost:3000) to view it in the browser.

The page will reload if you make edits.<br />
You will also see any lint errors in the console.

### `npm test`

Launches the test runner in the interactive watch mode.<br />
See the section about [running tests](https://facebook.github.io/create-react-app/docs/running-tests) for more information.

### `npm run lint`

Launches the ESLint code check

### `npm run lint-fix`

Launches the ESLint code check and fix simple typos, tabs, semi-colon, etc.


### `npm run build`

Builds the app for production to the `build` folder.<br />
It correctly bundles React in production mode and optimizes the build for the best performance.

The build is minified and the filenames include the hashes.<br />
Your app is ready to be deployed!

See the section about [deployment](https://facebook.github.io/create-react-app/docs/deployment) for more information.

## Learn More

You can learn more in the [Create React App documentation](https://facebook.github.io/create-react-app/docs/getting-started).

To learn React, check out the [React documentation](https://reactjs.org/).

# Memo

https://www.npmjs.com/package/@react-keycloak/web
https://github.com/panz3r/react-keycloak#readme


# Cypress 

[Cypress](https://www.cypress.io/) is an end-to-end test framework

We use Cypress for non-regression testing of the user interface. 
We also use [db-migrate](https://db-migrate.readthedocs.io/en/latest/) to clean-up and reset the local database to a well know configuration before each test.

## How can I install Cypress?

First, check that the latest node packages are installed 

    cd frontend
    yarn install
    
# How can I execute Cypress tests suite?

## Pre-requisites

- Quarkus backend is started
- the Frontend application is up and running
- user "Alice" exists and is configured on Keycloak 

All tests run with a set of demo users. As a prerequisite, the following user should be created on your local Keycloak server :

Username : alice
Password : alice
Roles    : user, admin

Attributes : create an `organization` key with a value set to `lunatech.fr`

## How can I execute the tests?

You can execute all tests from the `frontend` sub-folder 

    cd frontend
    yarn cypress
    
All tests are available under frontend/cypress/integration sub folder.
      
## How can I write a test?

Check [cypress/integration](./cypress/integration) sub folder

## Resources

[Cypress assertions](https://docs.cypress.io/guides/references/assertions.html)
[DB-migrate](https://db-migrate.readthedocs.io/en/latest/)
[How to truncate pgSQL tables with Cascade](https://www.postgresqltutorial.com/postgresql-truncate-table/#:~:text=To%20remove%20all%20data%20from%20a%20table%2C%20you%20use%20the,faster%20than%20the%20DELETE%20statement.)

       

