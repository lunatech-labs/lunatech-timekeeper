import React, {useState, useEffect} from 'react'
import {Breadcrumb, Divider, PageHeader} from "antd";
import ShowUser from "../../components/ShowUser/ShowUser";
import './index.less';
import {useAxios} from "../../utils/hooks";

const ShowUserPage = () => {
    const axiosInstance = useAxios('http://localhost:8080');

    const [loadedUsers, setData] = useState({ users: [] });

    useEffect( () => {
        if(axiosInstance==null){
            // When the page starts but Axios is not ready yet, we cannot fetch data
            // I guess there is a better pattern that we should use
            return;
        }
        const fetchData = async () => {
            // Endpoint cote Quarkus qui prend le jeton JWT et retourne l'utilisateur authentifié
            const result = await axiosInstance.get('/api/users/me');
            setData(result.data);
        };
        fetchData();
    }, [axiosInstance]);

    return (

        <React.Fragment>
            <Breadcrumb style={{margin: '16px 0'}}>
                <Breadcrumb.Item>Home</Breadcrumb.Item>
                <Breadcrumb.Item>Welcome</Breadcrumb.Item>
            </Breadcrumb>

            <PageHeader
                className="site-page-header"
                title="Welcome to TimeKeeper"
                subTitle="A smart time entry application by Lunatech"
            />

            <Divider/>
            <ShowUser user={loadedUsers}/>

        </React.Fragment>
    )
};

export default ShowUserPage