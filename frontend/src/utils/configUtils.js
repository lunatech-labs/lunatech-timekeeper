// If exists return the number of hours per day defined in .env files, or set default at 8hrs
export const getMaximumHoursPerDay = () => {
  return process.env.REACT_APP_GLOBAL_VARIABLE_MAX_HOUR_PER_DAY ? process.env.REACT_APP_GLOBAL_VARIABLE_MAX_HOUR_PER_DAY : 8;
};