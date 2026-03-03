import React from 'react';
import { render, screen } from '@testing-library/react';
import PageLoader, { TableSkeleton } from './PageLoader';
import '@testing-library/jest-dom';

describe('PageLoader Component', () => {
    it('renders with the default message', () => {
        render(<PageLoader />);
        expect(screen.getByText('Loading...')).toBeInTheDocument();
    });

    it('renders with a custom message', () => {
        render(<PageLoader message="Fetching data..." />);
        expect(screen.getByText('Fetching data...')).toBeInTheDocument();
    });
});

describe('TableSkeleton Component', () => {
    it('renders the correct number of rows and columns', () => {
        const { container } = render(<TableSkeleton rows={3} cols={2} />);

        // 1 header row with 2 columns
        const headerCols = container.querySelectorAll('.flex.gap-4.mb-4 > .skeleton');
        expect(headerCols.length).toBe(2);

        // 3 content rows
        const contentRows = container.querySelectorAll('.flex.gap-4.items-center');
        expect(contentRows.length).toBe(3);

        // Total content columns = 3 * 2 = 6
        const allContentCols = container.querySelectorAll('.flex.gap-4.items-center > .skeleton');
        expect(allContentCols.length).toBe(6);
    });
});
