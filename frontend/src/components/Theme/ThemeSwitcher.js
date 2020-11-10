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

import React  from 'react';
import {ContextTheme} from './ContextTheme';
import { Switch } from 'antd';
import './ThemeSwitcher.less';

function onChange(checked) {
  console.log(`switch to ${checked}`);
}

function ThemeSwitcher() {
  return (
    <ContextTheme.Consumer>
      {({theme, toggleTheme}) => (
        <div className="tk_ThemeSwitcher">
          <p>Light</p>
          <Switch checked={theme === 'dark-theme'} onClick={toggleTheme} onChange={onChange} />
          <p>Dark</p>
        </div>
      )}
    </ContextTheme.Consumer>
  );
}

export default ThemeSwitcher;