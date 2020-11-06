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