import React, {useEffect, useState} from 'react';
import {Alert, Button, Form, Input, message, Spin, Radio, DatePicker, Col, Row} from 'antd';
import {Link, Redirect, useRouteMatch} from 'react-router-dom';
import {useTimeKeeperAPIPut} from '../../utils/services';
import Space from 'antd/lib/space';
import '../../components/Button/BtnGeneral.less';
import moment from 'moment';

const EditTimeSheetForm = ({timesheet}) => {

    const [timeSheetUpdated, setTimeSheetUpdated] = useState(false);

    const timeKeeperAPIPut = useTimeKeeperAPIPut('/api/time-sheets/' + timesheet.id, (form => form), setTimeSheetUpdated);

    useEffect(() => {
        if (!timeSheetUpdated) {
            return;
        }
        message.success('Time Sheet was updated');
    }, [timeSheetUpdated]);

    const [form] = Form.useForm();

    if (timeSheetUpdated) {
        return (
            <React.Fragment>
                <Redirect to={{pathname:'/projects/'+timesheet.project.id, state: { modal: true }}}/>
            </React.Fragment>
        );
    }

    if (timeKeeperAPIPut.error) {
        return (
            <React.Fragment>
                <Alert title='Server error'
                       message='Failed to save the edited time sheet'
                       type='error'
                />
            </React.Fragment>
        );
    }

    // The method to handle initial values of the form
    const initialValues = (timesheet) => {
        return {
            "timeUnit": timesheet.timeUnit,
            "defaultIsBillable": timesheet.defaultIsBillable,
            "expirationDate": moment(timesheet.expirationDate, 'yyyy-MM-dd').utc(),
            "maxDuration": timesheet.maxDuration,
            "durationUnit": timesheet.durationUnit
        }
    }


    return (
        <React.Fragment>
            <Form id="tk_Form" layout="vertical" initialValues={initialValues(timesheet)} onFinish={timeKeeperAPIPut.run} form={form}>

                <Form.Item name="timeUnit" label="Time unit" rules={[{required: true}]}>
                    <Radio.Group>
                        <Radio value="DAY">Day</Radio>
                        <Radio value="HALFDAY" >Half-day</Radio>
                        <Radio value="HOURLY" >Hours</Radio>
                    </Radio.Group>
                </Form.Item>

                <Form.Item label="End date" name="expirationDate" rules={[{required: false,},]}>
                    <DatePicker className="tk_InputDate" />
                </Form.Item>

                <Row gutter={32}>
                    <Col className="gutter-row" span={12}>
                        <Form.Item label="Number of days" name="maxDuration" rules={[{required: false},]}>
                            <Input placeholder="TimeSheet's max duration"/>
                        </Form.Item>
                    </Col>

                    <Col className="gutter-row" span={12}>
                        <Form.Item label="Billable" name="defaultIsBillable" rules={[{required: true}]}>
                            <Radio.Group>
                                <Radio value={true}>Yes</Radio>
                                <Radio value={false}>No</Radio>
                            </Radio.Group>
                        </Form.Item>
                    </Col>
                </Row>

                <Form.Item name='durationUnit' noStyle={true} />

                <Form.Item>
                    <Space className="tk_JcFe" size="middle" align="center">
                        <Link id="tk_Btn" className="tk_BtnSecondary" key="cancelLink" to={{state: { modal: true }}}>Cancel</Link>
                        <Button id="tk_Btn" className="tk_BtnPrimary" htmlType="submit">Submit</Button>
                    </Space>
                </Form.Item>
            </Form>
        </React.Fragment>
    );
};

export default EditTimeSheetForm;