import moment from 'moment';

// Render a range of date with the year
// same month, same year => 22 - 25 May 2020
// different months, same year => 22 Jan - 01 Feb 2020
export const renderRangeWithYear = (start, end) => {
  const panelFormatWithYear = 'DD MMM YYYY';
  const panelFormat = 'DD MMM';
  if ((start.isSame(end, 'month') && start.isSame(end, 'year'))) {
    // same month, same year => 22 - 25 May 2020
    return `${start.format('DD')} - ${end.format(panelFormatWithYear)}`;
  } else if ((!start.isSame(end, 'month') && start.isSame(end, 'year'))) {
    // different months, same year => 22 Jan - 01 Feb 2020
    return `${start.format(panelFormat)} - ${end.format(panelFormatWithYear)}`;
  } else {
    return `${start.format(panelFormatWithYear)} - ${end.format(panelFormatWithYear)}`;
  }
};

// Render a range of date without the year
export const renderRange = (start, end) => {
  const panelFormat = 'DD MMM';
  if (start.isSame(end, 'month')) {
    return `${start.format('DD')} - ${end.format(panelFormat)}`;
  } else {
    return `${start.format(panelFormat)} - ${end.format(panelFormat)}`;
  }
};

// Compute the week from the first day (or the start of the current week)
export const weekRangeOfDate = (firstDay, numberOfWeek) => {
  const startOfCurrentWeek = firstDay || moment().utc().startOf('week');
  return [...Array(numberOfWeek).keys()].map(i => {
    const toAdd = i - 7;
    const start = startOfCurrentWeek.clone().add(toAdd, 'week');
    const end = start.clone().endOf('week');
    return {
      id: start.isoWeek(),
      start: start,
      end: end
    };
  });
};

export const isToday = (day) => {
  return moment.utc().isSame(day, 'day');
};

// Returns true if the date is saturday or sunday
export const isWeekEnd = (date) => date.isoWeekday() === 6 || date.isoWeekday() === 7;

// Returns the number of hours between two dates
export const computeNumberOfHours = (start, end) => {
  return moment.duration(end.diff(start)).asHours();
};