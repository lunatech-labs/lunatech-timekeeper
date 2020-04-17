import React, {useEffect, useState} from 'react'
import {useParams} from "react-router-dom";
import {Button, Form, Input} from 'antd'
import {useAxios} from "../../utils/hooks";

const {TextArea} = Input;

const getCustomer = (axios, setState, id) => {
    console.log("test")
    const fetchData = async () => {
        const result = await axios.get(`/api/customers/${id}`);
        setState(result.data);
    };
    fetchData();
};

const CustomerForm = ({ axiosInstance, isNew}) => {
    const params = useParams();
    const [customer, setCustomer] = useState();
    console.log(customer)
    useEffect(() => {
        if(!axiosInstance) {
            return;
        }
        if(!isNew && params.id) {
            getCustomer(axiosInstance, setCustomer, params.id)
        }
    }, [axiosInstance, params])
    const postForm = values => {
        axiosInstance.post('/api/customers', {
            'name': values.name,
            'description': values.description
        })
            .then(res => {
                switch (res.status) {
                    case 201:
                        //TODO Action in case of 201
                        break;
                    default:
                }
            })
    }
    const putForm = values => {
        axiosInstance.put(`/api/customers/${customer.id}`, {
            'name': values.name,
            'description': values.description
        })
            .then(res => {
                switch (res.status) {
                    case 204:
                        //TODO Action is case of 204
                        break;
                }
            })
    }

    return (
        <Form
            labelCol={{span: 4}}
            wrapperCol={{span: 14}}
            layout="horizontal"
            onFinish={(isNew ? postForm : putForm)}
            initialValues={customer}
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
                    placeholder="New customer's name"
                />
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
            <Form.Item>
                <Button className="btn save" htmlType="submit">
                    Submit
                </Button>
            </Form.Item>
        </Form>
    )
};

export default CustomerForm;