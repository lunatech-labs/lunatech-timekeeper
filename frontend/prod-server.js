/*
 * Copyright 2020 Lunatech S.A.S
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

const path = require('path');
const express = require('express');
const app = express(),
    DIST_DIR = __dirname + "/build/",
    HTML_FILE = path.join(DIST_DIR, 'index.html');
    app.use(express.static(DIST_DIR));
    app.get('*', (req, res) => {
        res.sendFile(HTML_FILE)
    });
const PORT = process.env.PORT || 8080;
app.listen(PORT, () => {
    console.log(`TimeKeeper App started`)
});