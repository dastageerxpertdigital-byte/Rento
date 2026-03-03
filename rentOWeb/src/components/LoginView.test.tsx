import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import LoginView from './LoginView';
import { useAuth } from '../context/AuthContext';
import { signInWithEmailAndPassword } from 'firebase/auth';

jest.mock('../context/AuthContext', () => ({
    useAuth: jest.fn(),
}));

jest.mock('../lib/firebase', () => ({
    auth: {},
}));

jest.mock('firebase/auth', () => ({
    signInWithEmailAndPassword: jest.fn(),
}));

describe('LoginView', () => {
    const mockSetError = jest.fn();

    beforeEach(() => {
        jest.clearAllMocks();
        (useAuth as jest.Mock).mockReturnValue({
            error: null,
            setError: mockSetError,
        });
    });

    // Success Case
    it('calls signInWithEmailAndPassword with correct credentials on submit', async () => {
        (signInWithEmailAndPassword as jest.Mock).mockResolvedValueOnce({ user: { uid: '123' } });

        render(<LoginView />);

        const emailInput = screen.getByLabelText(/email address/i);
        const passwordInput = screen.getByLabelText(/password/i);
        const submitButton = screen.getByRole('button', { name: /sign in/i });

        fireEvent.change(emailInput, { target: { value: 'test@admin.com' } });
        fireEvent.change(passwordInput, { target: { value: 'password123' } });
        const form = submitButton.closest('form');
        fireEvent.submit(form!);

        expect(signInWithEmailAndPassword).toHaveBeenCalledWith(expect.anything(), 'test@admin.com', 'password123');
        expect(mockSetError).toHaveBeenCalledWith(null);
    });

    // Loading State
    it('displays loading state during submission', async () => {
        (signInWithEmailAndPassword as jest.Mock).mockImplementation(() => new Promise(() => { }));

        render(<LoginView />);

        const submitButton = screen.getByRole('button', { name: /sign in/i });
        const form = submitButton.closest('form');
        fireEvent.submit(form!);

        await waitFor(() => {
            expect(screen.getByRole('button', { name: /signing in.../i })).toBeInTheDocument();
            expect(submitButton).toBeDisabled();
        });
    });

    // Failure Case
    it('displays error message on failed login', async () => {
        const errorMsg = 'Invalid credentials';
        (useAuth as jest.Mock).mockReturnValue({
            error: errorMsg,
            setError: mockSetError,
        });

        render(<LoginView />);

        expect(screen.getByText(errorMsg)).toBeInTheDocument();
    });
});
