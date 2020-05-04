import React from 'react';
import MainPage from "../MainPage/MainPage";
import ProjectDetail from "../../components/Projects/ProjectDetail";

const DetailProjectPage = () => {
  return (
    <MainPage title={'List of projects'}>
      <ProjectDetail/>
    </MainPage>
  );
};
export default DetailProjectPage;