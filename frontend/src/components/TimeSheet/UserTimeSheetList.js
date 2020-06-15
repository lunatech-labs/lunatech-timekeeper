import React from 'react';
import PropTypes from 'prop-types';
import Space from 'antd/lib/space';
import {Avatar, Collapse, List} from 'antd';
import FolderFilled from '@ant-design/icons/lib/icons/FolderFilled';
import logo from '../../img/logo_timekeeper_homepage.png';
import TagProjectClient from '../Tag/TagProjectClient';
import {CalendarOutlined, ClockCircleOutlined} from '@ant-design/icons';
import moment from 'moment';
import HistoryOutlined from '@ant-design/icons/lib/icons/HistoryOutlined';
import './UserTimeSheetList.less';


const {Panel} = Collapse;
const format = (s) => moment(s, 'YYYY-MM-DD').format('YYYY/MM/DD');
const formatEmpty = (stringTocheck) => {
  if (stringTocheck)
    return stringTocheck;
  return 'Unlimited';
};

const defineClassName = (elementToCheck) => {
  if (elementToCheck)
    return '';
  return 'ant-card-meta-description';
};


const UserTimeSheetList = ({timeSheets}) => {
  return (
    <div className="tk_CardCollapse">
      <Collapse bordered={false} expandIconPosition={'right'} key="timeSheets">
        <Panel header={<Space size="small"><FolderFilled/>{'Time sheets list'}</Space>} key="1">
          <List
            className={'tk_TimeSheetList'}
            dataSource={timeSheets}
            renderItem={timeSheet => {
              if (!timeSheet.project.publicAccess)
                return (
                  <List.Item>
                    <div>
                      <Avatar src={logo} shape={'square'} size="large"/>
                      <p>{timeSheet.project.name}</p>
                      <TagProjectClient client={timeSheet.project.client}/>
                    </div>
                    <div>
                      <p className={defineClassName(timeSheet.timeUnit)}><ClockCircleOutlined/> TimeUnit: {timeSheet.timeUnit}</p>
                      <p className={defineClassName(timeSheet.maxDuration)}><CalendarOutlined/> Number of days: {formatEmpty(timeSheet.maxDuration)}</p>
                      <p className={defineClassName(timeSheet.expirationDate)}><CalendarOutlined/> End date: {timeSheet.expirationDate ? format(timeSheet.expirationDate) : 'Unlimited'}</p>
                      <p className={defineClassName(timeSheet.leftOver)}><HistoryOutlined/>Days left : {formatEmpty(timeSheet.leftOver)}</p>
                    </div>
                  </List.Item>
                );
            }}
          />
        </Panel>
      </Collapse>
    </div>
  );
};

UserTimeSheetList.propTypes = {
  timeSheets: PropTypes.arrayOf(
    PropTypes.shape({
      id: PropTypes.number.isRequired,
      project: PropTypes.shape({
        name: PropTypes.string,
        publicAccess: PropTypes.bool,
        client: PropTypes.object
      }),
      defaultIsBillable: PropTypes.bool.isRequired,
      expirationDate: PropTypes.string,
      maxDuration: PropTypes.number,
      durationUnit: PropTypes.string,
      leftOver: PropTypes.number
    })).isRequired
};

export default UserTimeSheetList;