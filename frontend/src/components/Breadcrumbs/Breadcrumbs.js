import React, {Component} from 'react';
import {Link, withRouter} from 'react-router-dom';
import {Breadcrumb} from 'antd';
import PropTypes from 'prop-types';


const breadcrumbNameMap = {
  '/clients': 'Clients',
  '/clients/new': 'New client',
  '/clients/:id/edit': 'Edit client',
  '/users': 'Users',
  '/projects': 'Projects',
  '/projects/new': 'New project',
  '/projects/:id': 'Project details',
  '/projects/:id/edit': 'Edit project'
};

class Breadcrumbs extends Component {
  render() {
    const {location, entity} = this.props;

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
  location: PropTypes.object.isRequired,
  entity: PropTypes.string
};


export default withRouter(Breadcrumbs);