import React from "react";
import PropTypes from 'prop-types';
import {Button} from "antd";
import {Link} from "react-router-dom";


const LinkButton = ({ children, to, ...buttonProps }) => (
    <Link to={to}>
        <Button {...buttonProps}>
            {children}
        </Button>
    </Link>
);

LinkButton.propTypes = {
    children: PropTypes.oneOfType([PropTypes.func, PropTypes.node]),
    to: PropTypes.string
};

export default LinkButton;