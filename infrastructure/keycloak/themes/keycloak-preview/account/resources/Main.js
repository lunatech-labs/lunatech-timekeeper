"use strict";
/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
var __extends = (this && this.__extends) || (function () {
    var extendStatics = function (d, b) {
        extendStatics = Object.setPrototypeOf ||
            ({ __proto__: [] } instanceof Array && function (d, b) { d.__proto__ = b; }) ||
            function (d, b) { for (var p in b) if (b.hasOwnProperty(p)) d[p] = b[p]; };
        return extendStatics(d, b);
    };
    return function (d, b) {
        extendStatics(d, b);
        function __() { this.constructor = d; }
        d.prototype = b === null ? Object.create(b) : (__.prototype = b.prototype, new __());
    };
})();
Object.defineProperty(exports, "__esModule", { value: true });
var React = require("react");
var ReactDOM = require("react-dom");
var react_router_dom_1 = require("react-router-dom");
var App_1 = require("./app/App");
var ContentPages_1 = require("./app/ContentPages");
var Main = /** @class */ (function (_super) {
    __extends(Main, _super);
    function Main(props) {
        return _super.call(this, props) || this;
    }
    Main.prototype.componentDidMount = function () {
        isReactLoading = false;
        toggleReact();
    };
    Main.prototype.render = function () {
        return (React.createElement(react_router_dom_1.HashRouter, null,
            React.createElement(App_1.App, null)));
    };
    return Main;
}(React.Component));
exports.Main = Main;
;
var e = React.createElement;
function removeHidden(items) {
    var visible = [];
    for (var _i = 0, items_1 = items; _i < items_1.length; _i++) {
        var item = items_1[_i];
        if (item.hidden)
            continue;
        if (ContentPages_1.isExpansion(item)) {
            visible.push(item);
            item.content = removeHidden(item.content);
            if (item.content.length === 0) {
                visible.pop(); // remove empty expansion
            }
        }
        else {
            visible.push(item);
        }
    }
    return visible;
}
content = removeHidden(content);
ContentPages_1.initGroupAndItemIds();
function loadModule(modulePage) {
    return new Promise(function (resolve, reject) {
        System.import(resourceUrl + modulePage.modulePath).then(function (module) {
            modulePage.module = module;
            resolve(modulePage);
        }).catch(function (error) {
            console.warn('Unable to load ' + modulePage.label + ' because ' + error.message);
            reject(modulePage);
        });
    });
}
;
var moduleLoaders = [];
ContentPages_1.flattenContent(content).forEach(function (item) {
    if (ContentPages_1.isModulePageDef(item)) {
        moduleLoaders.push(loadModule(item));
    }
});
// load content modules and start
Promise.all(moduleLoaders).then(function () {
    var domContainer = document.querySelector('#main_react_container');
    ReactDOM.render(e(Main), domContainer);
});
//# sourceMappingURL=Main.js.map