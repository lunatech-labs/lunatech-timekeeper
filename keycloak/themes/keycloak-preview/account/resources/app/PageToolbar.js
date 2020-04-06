"use strict";
/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
var react_core_1 = require("@patternfly/react-core");
var ReferrerDropdownItem_1 = require("./widgets/ReferrerDropdownItem");
var ReferrerLink_1 = require("./widgets/ReferrerLink");
var LocaleSelectors_1 = require("./widgets/LocaleSelectors");
var Logout_1 = require("./widgets/Logout");
var PageToolbar = /** @class */ (function (_super) {
    __extends(PageToolbar, _super);
    function PageToolbar(props) {
        var _this = _super.call(this, props) || this;
        _this.hasReferrer = typeof referrerName !== 'undefined';
        _this.onKebabDropdownToggle = function (isKebabDropdownOpen) {
            _this.setState({
                isKebabDropdownOpen: isKebabDropdownOpen
            });
        };
        _this.state = {
            isKebabDropdownOpen: false,
        };
        return _this;
    }
    PageToolbar.prototype.render = function () {
        var kebabDropdownItems = [];
        if (this.hasReferrer) {
            kebabDropdownItems.push(React.createElement(ReferrerDropdownItem_1.ReferrerDropdownItem, { key: 'referrerDropdownItem' }));
        }
        if (features.isInternationalizationEnabled) {
            kebabDropdownItems.push(React.createElement(LocaleSelectors_1.LocaleNav, { key: 'kebabLocaleNav' }));
        }
        kebabDropdownItems.push(React.createElement(Logout_1.LogoutDropdownItem, { key: 'LogoutDropdownItem' }));
        return (React.createElement(react_core_1.Toolbar, null,
            this.hasReferrer &&
                React.createElement(react_core_1.ToolbarGroup, { key: 'referrerGroup' },
                    React.createElement(react_core_1.ToolbarItem, { className: "pf-m-icons", key: 'referrer' },
                        React.createElement(ReferrerLink_1.ReferrerLink, null))),
            React.createElement(react_core_1.ToolbarGroup, { key: 'secondGroup' },
                features.isInternationalizationEnabled &&
                    React.createElement(react_core_1.ToolbarItem, { className: "pf-m-icons", key: 'locale' },
                        React.createElement(LocaleSelectors_1.LocaleDropdown, null)),
                React.createElement(react_core_1.ToolbarItem, { className: "pf-m-icons", key: 'logout' },
                    React.createElement(Logout_1.LogoutButton, null)),
                React.createElement(react_core_1.ToolbarItem, { key: 'kebab', className: "pf-m-mobile" },
                    React.createElement(react_core_1.Dropdown, { isPlain: true, position: "right", toggle: React.createElement(react_core_1.KebabToggle, { id: "mobileKebab", onToggle: this.onKebabDropdownToggle }), isOpen: this.state.isKebabDropdownOpen, dropdownItems: kebabDropdownItems })))));
    };
    return PageToolbar;
}(React.Component));
exports.PageToolbar = PageToolbar;
//# sourceMappingURL=PageToolbar.js.map