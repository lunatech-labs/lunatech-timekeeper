import React  from 'react';
import {ChangeTheme} from '../ChangeTheme/ChangeTheme';

function ButtonSwitch() {
  // The Theme Toggler Button receives not only the theme
  // but also a toggleTheme function from the context
  return (
    <ChangeTheme.Consumer>
      {({theme, toggleTheme}) => (
        <button onClick={toggleTheme}>
          Toggle Theme
        </button>
      )}
    </ChangeTheme.Consumer>
  );
}

export default ButtonSwitch;