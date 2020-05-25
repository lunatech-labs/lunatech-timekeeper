const path = require('path')
const express = require('express')


const redirectionFilter = function (req, res, next) {
    const currentDate = new Date();
    const receivedUrl = `${req.protocol}:\/\/${req.hostname}:${port}${req.url}`;

    if (req.get('X-Forwarded-Proto') === 'http') {
        const redirectTo = `https:\/\/${req.hostname}${req.url}`;
        console.log(`${currentDate} Redirecting ${receivedUrl} --> ${redirectTo}`);
        res.redirect(301, redirectTo);
    } else {
        next();
    }
};

const app = express(),
    DIST_DIR = __dirname + "/build/",
    HTML_FILE = path.join(DIST_DIR, 'index.html')
    app.get('*',redirectionFilter)
    app.use(express.static(DIST_DIR))
    app.get('*', (req, res) => {
        res.sendFile(HTML_FILE)
    })
const PORT = process.env.PORT || 8080
app.listen(PORT, () => {
    console.log(`TimeKeeper App started`)
})