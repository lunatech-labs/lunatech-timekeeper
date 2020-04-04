import React, { useState, useEffect } from 'react';
import axios from 'axios';
import logo from './logo_timekeeper_homepage.png';
import './App.css';

function App() {

    const [data, setData] = useState({ users: [] });
    useEffect(() => {
        const fetchData = async () => {
            const result = await axios(
                'http://127.0.0.1:8080/api/users/me', { headers: { "Authorization": "Bearer " + localStorage.getItem("react-token") } }
            );
            setData(result.data);
        };
        fetchData();
    }, []);


    return (
        <div className="App">
            <header className="App-header">
                <h1>TimeKeeper</h1>
                <div>
                    <img src={logo} className="App-logo" alt="logo" />
                </div>
                <div>

                    <h2>Response from Quarkus API: /api/users/me </h2>

                    <p>Name: {data.name}</p>
                    <p>First name: {data.givenName}</p>
                    <p>Last name: {data.familyName}</p>
                    <p>Email:{data.email}</p>

                </div>
            </header>
        </div>
    );
}

export default App;
