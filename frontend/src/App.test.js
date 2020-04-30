import React from 'react';
import { render } from '@testing-library/react';
import App from './App';
// eslint-disable-next-line
test('renders the timekeeper home', () => {
  const { getByText } = render(<App />);
  const linkElement = getByText(/Loading.../i);
  // eslint-disable-next-line
  expect(linkElement).toBeInTheDocument();
});
