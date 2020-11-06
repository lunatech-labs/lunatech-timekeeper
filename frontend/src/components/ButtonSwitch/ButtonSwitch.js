import React, { ButtonHTMLAttributes } from "react";
import './ButtonSwitch.less';

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  onClickMethod : (args: any) => void;
}


const ButtonSwitch: React.FC<ButtonProps> = ({ disabled = false, children, onClickMethod, ...rest }) => {
  return (
    <button disabled={disabled} className='primary-btn' {...rest} onClick={onClickMethod}>
      {children}
    </button>
  );
};

export default ButtonSwitch;