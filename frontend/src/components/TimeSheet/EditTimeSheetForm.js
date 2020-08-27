/*
 * Copyright 2020 Lunatech S.A.S
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import React, {useEffect, useState} from 'react';
import {Alert, Button, Form, Input, message, Radio, DatePicker, Col, Row} from 'antd';
import {Link, Redirect} from 'react-router-dom';
import {useTimeKeeperAPIPut} from '../../utils/services';
import Space from 'antd/lib/space';
import '../../components/Button/BtnGeneral.less';
import moment from 'moment';
import PropTypes from 'prop-types';

const EditTimeSheetForm = ({timeSheet}) => {

  const [timeSheetUpdated, setTimeSheetUpdated] = useState(false);

  const timeKeeperAPIPut = useTimeKeeperAPIPut('/api/time-sheets/' + timeSheet.id, (form => form), setTimeSheetUpdated);

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
        <Redirect to={{pathname: '/projects/' + timeSheet.project.id, state: {modal: true}}}/>
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

  const initialValues = (timeSheet) => {
    return {
      'timeUnit': timeSheet.timeUnit,
      'defaultIsBillable': timeSheet.defaultIsBillable,
      'startDate': timeSheet.startDate ? moment(timeSheet.startDate, 'YYYY-MM-DD').utc(true) : null,
      'expirationDate': timeSheet.expirationDate ? moment(timeSheet.expirationDate, 'YYYY-MM-DD').utc(true) : null,
      'maxDuration': timeSheet.maxDuration,
      'durationUnit': timeSheet.durationUnit
    };
  };


  return (
    <React.Fragment>
      <Form id="tk_Form" layout="vertical" initialValues={initialValues(timeSheet)} onFinish={timeKeeperAPIPut.run} form={form}>
        <p className="tk_SectionTitle">Edit timeSheet</p>

        <Form.Item name="timeUnit" label="Time unit:" rules={[{required: true}]}>
          <Radio.Group>
            <Radio value="DAY">Day</Radio>
            <Radio value="HALFDAY">Half-day</Radio>
            <Radio value="HOURLY">Hours</Radio>
          </Radio.Group>
        </Form.Item>

        <Form.Item label="Start date" name='startDate' rules={[{required: true}]}>
          <DatePicker className="tk_InputDate"/>
        </Form.Item>

        <Form.Item label="End date" name='expirationDate' rules={[{required: false}]}>
          <DatePicker className="tk_InputDate"/>
        </Form.Item>

        <Row gutter={32}>
          <Col className="gutter-row" span={12}>
            <Form.Item label="Number of days" name="maxDuration" rules={[{required: false}]}>
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

        <Form.Item name='durationUnit' noStyle={true}/>

        <Form.Item>
          <Space className="tk_JcFe" size="middle" align="center">
            <Link id="btnCancelEditTimeSheet" className="tk_Btn tk_BtnSecondary" key="cancelLink" to={{state: {modal: true}}}>Cancel</Link>
            <Button id="btnSubmitEditTimeSheet" className="tk_Btn tk_BtnPrimary" htmlType="submit">Submit</Button>
          </Space>
        </Form.Item>
      </Form>
    </React.Fragment>
  );
};

EditTimeSheetForm.propTypes = {
  timeSheet: PropTypes.shape({
    id: PropTypes.number.isRequired,
    timeUnit: PropTypes.string,
    defaultIsBillable: PropTypes.bool.isRequired,
    expirationDate: PropTypes.string,
    maxDuration: PropTypes.number,
    durationUnit: PropTypes.string,
    project: PropTypes.shape({
      id: PropTypes.number.isRequired
    }),
    startDate: PropTypes.string
  })
};

export default EditTimeSheetForm;