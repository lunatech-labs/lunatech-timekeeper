import React, {useEffect, useState} from 'react';
import {Alert, Button, Form, Input, message, Spin, Radio, DatePicker} from 'antd';
import {Link, Redirect, useRouteMatch} from 'react-router-dom';
import {useTimeKeeperAPIPut} from '../../utils/services';
import Space from 'antd/lib/space';
import '../../components/Button/BtnGeneral.less';
import moment from 'moment';

const EditTimeSheetForm = ({timesheet}) => {

    const [timeSheetUpdated, setTimeSheetUpdated] = useState(false);

    const timeKeeperAPIPut = useTimeKeeperAPIPut('/api/time-sheets/' + timesheet.id, (form => form), setTimeSheetUpdated);

    // I dunno but it looks like I need it to prompt success update
    useEffect(() => {
        if (!timeSheetUpdated) {
            return;
        }
        message.success('Time Sheet was updated');
    }, [timeSheetUpdated]);

    // I dunno but it was in EditProjectForm
    const [form] = Form.useForm();

    if (timeSheetUpdated) {
        return (
            <React.Fragment>
                <Redirect to="/projects"/>
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
            "projectId": timesheet.projectId,
            "ownerID": timesheet.ownerID,
            "timeUnit": timesheet.timeUnit,
            "defaultIsBillable": timesheet.defaultIsBillable,
            "expirationDate": moment(timesheet.expirationDate, 'yyyy-MM-dd'),
            "maxDuration": timesheet.maxDuration,
            "durationUnit": timesheet.durationUnit
        }
    }


    return (
        <React.Fragment>
            <Form
                id="tk_Form"
                layout="vertical"
                initialValues={initialValues(timesheet)}
                onFinish={timeKeeperAPIPut.run}
                form={form}
            >
                <div className="tk_CardLg">

                    {/*
                        THE Time unit Picker ( 3 values enum)
                    */}

                    <Form.Item name="timeUnit" label="Time unit" rules={[{required: true}]}>
                        <Radio.Group>
                            <Radio.Button value="HOURLY" >Hours</Radio.Button>
                            <Radio.Button value="HALFDAY" >Half-day</Radio.Button>
                            <Radio.Button value="DAY">Day</Radio.Button>
                        </Radio.Group>
                    </Form.Item>

                    {/*
                        THE End date Picker
                    */}
                    <Form.Item
                        label="End date"
                        name="expirationDate"
                        rules={[
                            {
                                required: false,
                            },
                        ]}
                    >
                        <DatePicker />
                    </Form.Item>

                    {/*
                        THE Number of days
                    */}
                    <Form.Item
                        label="Number of days"
                        name="maxDuration"
                        rules={[
                            {
                                required: true
                            },
                        ]}
                    >
                        <Input
                            placeholder="TimeSheet's max duration"
                        />
                    </Form.Item>

                    {/*
                        THE isBillable radio
                    */}
                    <Form.Item
                        label="Billable"
                        name="defaultIsBillable"
                        rules={[{required: true}]}
                    >
                        <Radio.Group>
                            <Radio value={true}>Yes</Radio>
                            <Radio value={false}>No</Radio>
                        </Radio.Group>
                    </Form.Item>
                    {/* additionnal values : must be defined to be sent */}
                    <Form.Item name='durationUnit' noStyle={true} />
                </div>
                <Form.Item>
                    <Space className="tk_JcFe" size="middle" align="center">
                        <Link id="tk_Btn" className="tk_BtnSecondary" key="cancelLink" to={'/projects'}>Cancel</Link>
                        <Button id="tk_Btn" className="tk_BtnPrimary" htmlType="submit">Submit</Button>
                    </Space>
                </Form.Item>
            </Form>
        </React.Fragment>
    );


};

export default EditTimeSheetForm;