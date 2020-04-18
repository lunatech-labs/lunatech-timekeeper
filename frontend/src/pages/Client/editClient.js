import React, {useEffect} from 'react'
import {useParams} from "react-router-dom";
import {useAxios} from "../../utils/hooks";
import ClientForm from "../../components/Clients/EditClientForm";
import MainPage from "../MainPage/MainPage";

const EditClientPage = () => {
    const apiEndpoint = useAxios('http://localhost:8080');

    let { id } = useParams();

    useEffect(() => {
        if (!apiEndpoint) {
            return;
        }
    }, [apiEndpoint]);

    return (
        <MainPage title={`Edit client`}>

            <p>Edit Client {id}</p>

            <ClientForm client={id} axiosInstance={apiEndpoint} isNew={false}/>
        </MainPage>
    )
};

export default EditClientPage;