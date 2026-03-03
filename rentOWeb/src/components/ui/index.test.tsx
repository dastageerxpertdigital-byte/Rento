import React from 'react';
import { render, screen } from '@testing-library/react';
import { StatusBadge } from './index';
import '@testing-library/jest-dom';

describe('StatusBadge Component', () => {
    it('renders a success badge correctly', () => {
        render(<StatusBadge status="Active" />);
        const badge = screen.getByText('Active');
        expect(badge).toBeInTheDocument();
        expect(badge.className).toContain('text-[var(--pri)]');
    });

    it('renders a warning badge correctly', () => {
        render(<StatusBadge status="Pending" />);
        const badge = screen.getByText('Pending');
        expect(badge).toBeInTheDocument();
        expect(badge.className).toContain('text-[var(--acc)]');
    });

    it('renders an error badge correctly', () => {
        render(<StatusBadge status="Blocked" />);
        const badge = screen.getByText('Blocked');
        expect(badge).toBeInTheDocument();
        expect(badge.className).toContain('text-[var(--red)]');
    });

    it('renders an info badge correctly for default statuses', () => {
        render(<StatusBadge status="Unknown" />);
        const badge = screen.getByText('Unknown');
        expect(badge).toBeInTheDocument();
        expect(badge.className).toContain('text-[var(--t1)]');
    });
});
