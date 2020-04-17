import React from 'react'
import {Button, Form, Input, PageHeader} from 'antd'

const {TextArea} = Input;

const tailLayout = {
    wrapperCol: {
        offset: 4,
        span: 16,
    },
};

function getPageTitle(isNewClient) {
    if (isNewClient) {
        return "Create a new Client";
    }
    return "Update an existing Client";
}

const ClientForm = ({client, axiosInstance, isNew}) => {
    const postForm = values => {
        axiosInstance.post('/api/clients', {
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
    };

    return (
        <React.Fragment>
            <PageHeader title="Clients" subTitle={getPageTitle(isNew)} />
            <Form
                labelCol={{span: 4}}
                wrapperCol={{span: 14}}
                layout="horizontal"
                onFinish={(isNew && postForm)}
                initialValues={client}
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