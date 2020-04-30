// See https://ant.design/docs/react/use-with-create-react-app
const {override, addLessLoader} = require('customize-cra');

module.exports = override(
    addLessLoader({
        javascriptEnabled: true,
    }),
);