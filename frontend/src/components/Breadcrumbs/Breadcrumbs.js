import React, {Component} from 'react';
import {Link, withRouter} from 'react-router-dom';
import {Breadcrumb} from 'antd';
import PropTypes from 'prop-types';


const breadcrumbNameMap = {
  '/clients': 'Clients',
  '/users': 'Users',
  '/projects': 'Projects'
};

class Breadcrumbs extends Component {
  render() {
    const {location} = this.props;

    const pathSnippets = location.pathname.split('/').filter(i => i);
    const extraBreadcrumbItems = pathSnippets.map((_, index) => {
      const token = pathSnippets.slice(0, index + 1);
      const url = `/${token.join('/')}`;
      const breadcrumbName = breadcrumbNameMap[url];
      if (breadcrumbName !== undefined) {
        return (
          <Breadcrumb.Item key={url}>
            <Link to={url}>{breadcrumbNameMap[url] ? breadcrumbNameMap[url] : 'Page'}</Link>
          </Breadcrumb.Item>
        );
      } else {
        return '';
      }
    });
    const breadcrumbItems = [
      <Breadcrumb.Item key="home">
        <Link to="/">Home</Link>
      </Breadcrumb.Item>,
    ].concat(extraBreadcrumbItems);

    return <Breadcrumb>{breadcrumbItems}</Breadcrumb>;
  }
}


Breadcrumbs.propTypes = {
  location: PropTypes.object.isRequired
};


export default withRouter(Breadcrumbs);