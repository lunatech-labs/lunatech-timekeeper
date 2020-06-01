const momentUtil = () => {
  const moment = require('moment');
  const globalLocale = require('moment/locale/fr');
  return {
    moment: moment,
    globalLocale: globalLocale
  };
};
export default momentUtil;
