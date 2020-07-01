import React from 'react';

import MainPage from '../MainPage/MainPage';
import NewEventTemplateForm from "./NewEventTemplateForm";

const NewEventTemplatePage = () => {
    return (
        <MainPage title="Create new event">
            <NewEventTemplateForm/>
        </MainPage>
    );
};

export default NewEventTemplatePage;