/* eslint-disable @typescript-eslint/no-explicit-any */
/* eslint-disable @typescript-eslint/no-unused-vars */
import { renderHook, act } from '@testing-library/react';
import { AuthProvider, useAuth } from './AuthContext';
import { onAuthStateChanged, signOut } from 'firebase/auth';
import { getDoc } from 'firebase/firestore';

jest.mock('firebase/auth', () => ({
    onAuthStateChanged: jest.fn(),
    signOut: jest.fn(),
}));

jest.mock('firebase/firestore', () => ({
    doc: jest.fn(),
    getDoc: jest.fn(),
}));

jest.mock('../lib/firebase', () => ({
    auth: {},
    db: {},
}));

describe('AuthContext Setup & Auth Guards', () => {
    beforeEach(() => {
        jest.clearAllMocks();
    });

    const wrapper = ({ children }: { children: React.ReactNode }) => (
        <AuthProvider>{children}</AuthProvider>
    );

    it('P18-D: sets user to null when no user is logged in (Auth Guard test)', async () => {
        let callback: any;
        (onAuthStateChanged as jest.Mock).mockImplementation((auth, cb) => {
            callback = cb;
            return jest.fn(); // unsubscribe fn
        });

        const { result } = renderHook(() => useAuth(), { wrapper });

        // It initially starts loading
        expect(result.current.loading).toBe(true);

        await act(async () => {
            callback(null);
        });

        expect(result.current.user).toBeNull();
        expect(result.current.loading).toBe(false);
    });

    it('P18-E: signs out and sets error if user is not admin (Admin check test)', async () => {
        let callback: any;
        (onAuthStateChanged as jest.Mock).mockImplementation((auth, cb) => {
            callback = cb;
            return jest.fn();
        });

        (getDoc as jest.Mock).mockResolvedValue({
            exists: () => true,
            data: () => ({ userType: 'user' }) // Regular user
        });

        const { result } = renderHook(() => useAuth(), { wrapper });

        await act(async () => {
            await callback({ uid: '123' });
        });

        expect(signOut).toHaveBeenCalled();
        expect(result.current.error).toBe("Access denied. Admin accounts only.");
        expect(result.current.user).toBeNull();
        expect(result.current.loading).toBe(false);
    });

    it('Allows login if user is strictly admin (Admin check test)', async () => {
        let callback: any;
        (onAuthStateChanged as jest.Mock).mockImplementation((auth, cb) => {
            callback = cb;
            return jest.fn();
        });

        (getDoc as jest.Mock).mockResolvedValue({
            exists: () => true,
            data: () => ({ userType: 'admin', extraDetail: 'testAdmin' }) // Admin user
        });

        const { result } = renderHook(() => useAuth(), { wrapper });

        await act(async () => {
            await callback({ uid: '456' });
        });

        expect(signOut).not.toHaveBeenCalled();
        expect(result.current.error).toBeNull();
        expect(result.current.user).toEqual({ uid: '456', userType: 'admin', extraDetail: 'testAdmin' });
        expect(result.current.loading).toBe(false);
    });
});
