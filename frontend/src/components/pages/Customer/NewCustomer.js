import React from 'react'
import {Button, Form, Input, Select } from 'antd'

const { TextArea } = Input;

const NewCustomer = ({ }) => (
    <Form
        labelCol={{span: 4}}
        wrapperCol={{span: 14}}
        layout="horizontal"
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
);

export default NewCustomer;