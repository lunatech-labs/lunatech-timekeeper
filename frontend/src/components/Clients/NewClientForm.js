import React, {useState, useCallback} from 'react'
import {useAxios} from "../../utils/hooks";
import {Button, Form, Input, PageHeader, message} from 'antd'
import {Redirect} from "react-router-dom";

const {TextArea} = Input;

const tailLayout = {
    wrapperCol: {
        offset: 4,
        span: 16,
    },
};

const ClientForm = () => {
    const apiEndpoint = useAxios('http://localhost:8080');

    const [clientCreated, setClientCreated] = useState(false);

    const postForm = useCallback( values => {

        apiEndpoint.post('/api/clients', {
            'name': values.name,
            'description': values.description
        })
            .then(res => {
                switch (res.status) {
                    case 201:
                        setClientCreated(true);
                        message.success('Client created');
                        break;
                    default:
                }
            })
    }, [apiEndpoint]);


    if (clientCreated) {
        return (
            <React.Fragment>
                <PageHeader title="Clients" subTitle="Create a new Timekeeper client"/>
                <div>
                   <Redirect to="/clients"/>
                </div>
            </React.Fragment>
        )
    }

    return (
        <React.Fragment>
            <PageHeader title="Clients" subTitle="Create a new Timekeeper client"/>
            <Form
                labelCol={{span: 4}}
                wrapperCol={{span: 14}}
                layout="horizontal"
                onFinish={postForm}
            >
                <Form.Item
                    label="Name"
                    name="name"
                    rules={[
                        {
                            required: true,
                        },
                    ]}
                >
                    <Input
                        placeholder="Client's name"
                    />
                </Form.Item>
                <Form.Item
                    label="Description"
                    name="description"
                >
                    <TextArea
                        rows={4}
                        placeholder="A short description about this client"
                    />
                </Form.Item>
                <Form.Item {...tailLayout}>
                    <Button type="primary" htmlType="submit">
                        Submit
                    </Button>
                </Form.Item>
            </Form>
        </React.Fragment>
    )
};

export default ClientForm;