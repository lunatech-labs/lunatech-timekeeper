import React from 'react';
import MainPage from "../MainPage/MainPage";
import ShowProject from "../../components/Projects/ShowProject";
import {useRouteMatch} from "react-router";
import {useTimeKeeperAPI} from "../../utils/services";
import {Alert} from "antd";

const DetailProjectPage = () => {

  const projectIdSlug = useRouteMatch({
    path: '/projects/:id',
    strict: true,
    sensitive: true
  });

  const { data, error, loading } = useTimeKeeperAPI('/api/projects/'+projectIdSlug.params.id);

  if (error) {
    let errorReason = "Message: " + error ;
    return (
      <React.Fragment>
        <Alert title='Server error'
               message='Failed to load user from Quarkus backend server'
               type='error'
               description={errorReason}
        />
      </React.Fragment>
    );
  }
  if (loading) {
    return (
      <div>loading...</div>
    );
  }

  return (
    <MainPage title={'Project details'}>
      <ShowProject project={data} />
    </MainPage>
  );
};
export default DetailProjectPage;