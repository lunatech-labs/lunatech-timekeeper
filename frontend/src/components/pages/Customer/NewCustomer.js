import React from 'react'
import {Button, Form, Input, Select } from 'antd'

const { TextArea } = Input;

const NewCustomer = ({ }) => (
    <Form
        labelCol={{span: 4}}
        wrapperCol={{span: 14}}
        layout="horizontal"
    >
        <Form.Item label="Name">
            <Input placeholder="New customer's name"/>
        </Form.Item>
        <Form.Item label="Description">
                <TextArea
                    rows={4}
                    placeholder="New customer's description"
                />
        </Form.Item>
        <Button className="btn save">Save</Button>
    </Form>
);

export default NewCustomer;