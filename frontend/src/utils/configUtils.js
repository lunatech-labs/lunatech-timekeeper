// If exists return the number of hours per day defined in .env files, or set default at 8hrs
export const getMaximumHoursPerDay = () => {
  return process.env.REACT_APP_GLOBAL_VARIABLE_MAX_HOUR_PER_DAY ? process.env.REACT_APP_GLOBAL_VARIABLE_MAX_HOUR_PER_DAY : 8;
};

// If exists return the halfday duration defined in .env files, or set default at 4hrs
export const getHalfDayDuration = () => {
  return process.env.REACT_APP_GLOBAL_VARIABLE_HALFDAY_DURATION ? process.env.REACT_APP_GLOBAL_VARIABLE_HALFDAY_DURATION : 4;
};