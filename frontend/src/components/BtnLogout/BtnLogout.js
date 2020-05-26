import React  from 'react';
import { Button } from 'antd';
import './BtnGeneral.less';
import {useKeycloak} from '@react-keycloak/web';
import {LogoutOutlined} from '@ant-design/icons';

const BtnLogout = ( ) => {
  const { keycloak } = useKeycloak();

  return (
    <React.Fragment>
      {!!keycloak.authenticated && (
        <Button onClick={() => keycloak.logout()} id="tk_Btn" className="tk_BtnPrimary"><LogoutOutlined />Logout</Button>
      )}
    </React.Fragment>
  );
};

export default BtnLogout;