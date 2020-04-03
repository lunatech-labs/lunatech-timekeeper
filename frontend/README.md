# Frontend

React

## I am a backend developer and I do not want to install Node and npm

Easy : 

    mvn clean install

### Yes but what if I want to run the frontend?

Then npm is required with node. Use `brew install` 
   
You can then install npm `serve` with sudo (required only once) :

    sudo npm install -g serve   
   
You can then start the React application from the frontend folder : 
    
    serve -s build  
    
This starts the frontend on http://localhost:5000/    
    
## I am a frontend developer, should I use it? 

No.
Install node and npm then use 

    npm start

This starts a local node server and the TimeKeeper React app should be on http://localhost:3000/

On my Mac I relies on :

    npm 6.9.0 installed with brew install npm
    node v10.15.3 installed with brew install node

# The long version     

This project relies on `frontend-maven-plugin`(https://github.com/eirslett/frontend-maven-plugin)
The Maven plugin downloads/installs Node and NPM locally for your project, runs npm install, and then any combination of Bower, Grunt, Gulp, Jspm, Karma, or Webpack. It's supposed to work on Windows, OS X and Linux.

This is interesting to build the application to `frontend/build` folder.

#### What is this plugin meant to do?
- Let you keep your frontend and backend builds as separate as possible, by
reducing the amount of interaction between them to the bare minimum; using only 1 plugin.
- Let you use Node.js and its libraries in your build process without installing Node/NPM
globally for your build system
- Let you ensure that the version of Node and NPM being run is the same in every build environment

#### What is this plugin not meant to do?
- Not meant to replace the developer version of Node - frontend developers will still install Node on their
laptops, but backend developers can run a clean build without even installing Node on their computer.
- Not meant to install Node for production uses. The Node usage is intended as part of a frontend build,
running common javascript tasks such as minification, obfuscation, compression, packaging, testing etc.

**Notice:** _This plugin does not support already installed Node or npm versions. Use the `exec-maven-plugin` instead._

## Requirements

* _Maven 3_ 
* _Java 1.8_

## Development with React and NPM

I am a frontend developer, how should I develop?

This project was bootstrapped with [Create React App](https://github.com/facebook/create-react-app).

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

### `npm run build`

Builds the app for production to the `build` folder.<br />
It correctly bundles React in production mode and optimizes the build for the best performance.

The build is minified and the filenames include the hashes.<br />
Your app is ready to be deployed!

See the section about [deployment](https://facebook.github.io/create-react-app/docs/deployment) for more information.

### `npm run eject`

**Note: this is a one-way operation. Once you `eject`, you can’t go back!**

If you aren’t satisfied with the build tool and configuration choices, you can `eject` at any time. This command will remove the single build dependency from your project.

Instead, it will copy all the configuration files and the transitive dependencies (webpack, Babel, ESLint, etc) right into your project so you have full control over them. All of the commands except `eject` will still work, but they will point to the copied scripts so you can tweak them. At this point you’re on your own.

You don’t have to ever use `eject`. The curated feature set is suitable for small and middle deployments, and you shouldn’t feel obligated to use this feature. However we understand that this tool wouldn’t be useful if you couldn’t customize it when you are ready for it.

## Learn More

You can learn more in the [Create React App documentation](https://facebook.github.io/create-react-app/docs/getting-started).

To learn React, check out the [React documentation](https://reactjs.org/).

