export const range = (start, end) => {
  const result = [];
  [...Array(end-start).keys()].map(i => {
    result.push(i+start);
  });
  return result;
};