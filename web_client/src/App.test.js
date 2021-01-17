import { render, screen } from '@testing-library/react'
import App from './App'

test('renders login form', () => {
  render(<App />);
  const linkElement = screen.getByText(/Login or register to moderate your queues/i);
  expect(linkElement).toBeInTheDocument();
});
