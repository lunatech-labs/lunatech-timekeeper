import React from 'react'
import {Button, Form, Input } from 'antd'
import {useAxios} from "../../../utils/hooks";

const { TextArea } = Input;

const NewCustomer = ({ }) => {
    const axiosInstance = useAxios('http://localhost:8080');

    const postUser = values => {
        axiosInstance.post('/api/customers', {
            'name': values.name,
            'description': values.description
        })
            .then(res => {
                switch(res.status){
                    case 201:
                        console.log("201: Created");
                        break;
                    case 400:
                        console.log("400: Bad Request");
                        break;
                    default:
                        console.log("Default");
                }
            })
    };

    return (
        <Form
            labelCol={{span: 4}}
            wrapperCol={{span: 14}}
            layout="horizontal"
            onFinish={postUser}
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
                <Input placeholder="New customer's name"/>
            </Form.Item>
            <Form.Item
                label="Description"
                name="description"
            >
                <TextArea
                    rows={4}
                    placeholder="New customer's description"
                />
            </Form.Item>
            <Form.Item >
                <Button className="btn save" htmlType="submit">
                    Submit
                </Button>
            </Form.Item>
        </Form>
    )
};

export default NewCustomer;