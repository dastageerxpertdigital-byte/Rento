/* eslint-disable @typescript-eslint/no-explicit-any */
/* eslint-disable @typescript-eslint/no-unused-vars */
import { renderHook, waitFor } from '@testing-library/react';
import { useCollection, useDocument, useCollectionCount } from './hooks';
import { collection, doc, getDocs, getDoc, getCountFromServer, onSnapshot, query } from 'firebase/firestore';

jest.mock('firebase/firestore', () => ({
    collection: jest.fn(),
    doc: jest.fn(),
    getDocs: jest.fn(),
    getDoc: jest.fn(),
    getCountFromServer: jest.fn(),
    onSnapshot: jest.fn(),
    query: jest.fn()
}));

jest.mock('./firebase', () => ({
    db: {}
}));

describe('Firestore Hooks', () => {
    beforeEach(() => {
        jest.clearAllMocks();
    });

    describe('useCollection', () => {
        it('subscribes to collection and returns data on snapshot', () => {
            const mockUnsubscribe = jest.fn();
            const mockData = [{ id: '1', name: 'Item 1' }];

            (collection as jest.Mock).mockReturnValue({});
            (query as jest.Mock).mockReturnValue({});
            const mockedOnSnapshot = onSnapshot as jest.Mock;

            mockedOnSnapshot.mockImplementation((_, callback) => {
                const docs = mockData.map(d => ({
                    id: d.id,
                    data: () => ({ name: d.name })
                }));

                callback({
                    forEach: (cb: any) => docs.forEach(cb)
                });
                return mockUnsubscribe;
            });

            const { result, unmount } = renderHook(() => useCollection('test_collection'));

            expect(mockedOnSnapshot).toHaveBeenCalled();
            expect(result.current.data).toEqual(mockData);
            expect(result.current.isLoading).toBe(false);

            unmount();
            expect(mockUnsubscribe).toHaveBeenCalled();
        });
    });

    describe('useDocument', () => {
        it('subscribes to document and returns data on snapshot', () => {
            const mockUnsubscribe = jest.fn();
            const mockData = { id: 'doc1', title: 'Test Doc' };

            (doc as jest.Mock).mockReturnValue({});
            const mockedOnSnapshot = onSnapshot as jest.Mock;

            mockedOnSnapshot.mockImplementation((_, callback) => {
                callback({
                    id: mockData.id,
                    exists: () => true,
                    data: () => ({ title: mockData.title })
                });
                return mockUnsubscribe;
            });

            const { result, unmount } = renderHook(() => useDocument('test_collection/doc1'));

            expect(mockedOnSnapshot).toHaveBeenCalled();
            expect(result.current.data).toEqual(mockData);
            expect(result.current.isLoading).toBe(false);

            unmount();
            expect(mockUnsubscribe).toHaveBeenCalled();
        });
    });

    describe('useCollectionCount', () => {
        it('fetches count from server', async () => {
            (collection as jest.Mock).mockReturnValue({});
            (query as jest.Mock).mockReturnValue({});

            (getCountFromServer as jest.Mock).mockResolvedValue({
                data: () => ({ count: 42 })
            });

            const { result } = renderHook(() => useCollectionCount('test_collection'));

            await waitFor(() => {
                expect(result.current).toBe(42);
            });
            expect(getCountFromServer).toHaveBeenCalled();
        });
    });
});
