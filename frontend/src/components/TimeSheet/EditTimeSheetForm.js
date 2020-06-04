import React, {useEffect, useState} from 'react';
import {Alert, Button, Form, Input, message, Spin,} from 'antd';
import {Link, Redirect, useRouteMatch} from 'react-router-dom';
import useTimeKeeperAPIPut from '../../utils/services';
import Space from 'antd/lib/space';
import '../../components/Button/BtnGeneral.less';

const EditTimeSheetForm = ({timesheet}) => {

    const [timeSheetUpdated, setTimeSheetUpdated] = useState(false);

    const timeKeeperAPIPut = useTimeKeeperAPIPut('/api/time-sheets/' + timesheet.id, (form => form), setTimeSheetUpdated);

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

    return (
        <React.Fragment>
            <Form
                id="tk_Form"
                layout="vertical"
                initialValues={timesheet}
                onFinish={timeKeeperAPIPut.run}
            >
                <div className="tk_CardLg">
                    <Form.Item
                        label="Max Duration"
                        name="maxDuration"
                        rules={[
                            {
                                required: true,
                                type: 'number'
                            },
                        ]}
                    >
                        <Input
                            placeholder="TimeSheet's max duration"
                        />
                    </Form.Item>
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