import React from 'react';
import PropTypes from 'prop-types';
import Space from 'antd/lib/space';
import {Collapse, List} from 'antd';
import FolderFilled from '@ant-design/icons/lib/icons/FolderFilled';
import {CalendarOutlined, ClockCircleOutlined} from '@ant-design/icons';
import HistoryOutlined from '@ant-design/icons/lib/icons/HistoryOutlined';
import './UserTimeSheetList.less';
import ProjectClientHeader from "../Projects/ProjectClientHeader";

const {Panel} = Collapse;
const formatDate = (s) => s.replace('-', '/').replace('-', '/');
const formatEmpty = (stringToCheck) => {
  if (stringToCheck)
    return stringToCheck;
  return 'Unlimited';
};

const defineClassNameDayLeft = (daysLeft) => {
  if (!daysLeft) {
    return 'tk_UnlimitedField';
  } else if (daysLeft < 9) {
    return 'tk_WarnDayLeft';
  } else {
    return '';
  }
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
                    <ProjectClientHeader project={timeSheet.project}/>
                    <div>
                      <p>
                        <ClockCircleOutlined/> TimeUnit: {timeSheet.timeUnit}</p>
                      <p className={timeSheet.maxDuration ? '':'tk_UnlimitedField'}>
                        <CalendarOutlined/> Number of days: {formatEmpty(timeSheet.maxDuration)}
                      </p>
                      <p className={timeSheet.expirationDate ? '':'tk_UnlimitedField'}>
                        <CalendarOutlined/> End date: {timeSheet.expirationDate ? formatDate(timeSheet.expirationDate) : 'Unlimited'}
                      </p>
                      <p className={defineClassNameDayLeft(timeSheet.leftOver)}>
                        <HistoryOutlined/>Days left : {formatEmpty(timeSheet.leftOver)}
                      </p>
                    </div>
                  </List.Item>
                );
            }}/>
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