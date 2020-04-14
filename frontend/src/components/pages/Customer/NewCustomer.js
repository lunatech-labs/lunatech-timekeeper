import React from 'react'
import {Button, Form, Input, Select } from 'antd'

const NewCustomer = ({ }) => (
    <Form
        labelCol={{span: 4}}
        wrapperCol={{span: 14}}
        layout="horizontal"
    >
        <Form.Item label="Name">
            <Input placeholder="New customer name"/>
        </Form.Item>
        <Form.Item label="Project">
            <Select>
                <Select.Option value="demo">DARVA - Agira</Select.Option>
                <Select.Option value="demo">DARVA - Sinapps</Select.Option>
                <Select.Option value="demo">Disney - Guest profile</Select.Option>
            </Select>
        </Form.Item>
        <Button className="btn save">Save</Button>
        <Button className="btn cancel">Cancel</Button>
    </Form>
);

export default NewCustomer;