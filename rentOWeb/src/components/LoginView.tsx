"use client";

import { useState, useEffect } from 'react';
import { signInWithEmailAndPassword, createUserWithEmailAndPassword } from "firebase/auth";
import { doc, setDoc } from "firebase/firestore";
import { Users, Eye, Sun, Moon } from 'lucide-react';
import { auth, db } from '../lib/firebase';
import { useAuth } from '../context/AuthContext';

export default function LoginView() {
    const [email, setEmail] = useState('admin@rento.pk');
    const [password, setPassword] = useState('');
    const [isRegister, setIsRegister] = useState(false);
    const [loading, setLoading] = useState(false);
    const { error, setError } = useAuth();
    const [isDark, setIsDark] = useState(true);

    useEffect(() => {
        if (isDark) document.documentElement.classList.add('dark');
        else document.documentElement.classList.remove('dark');
    }, [isDark]);

    const handleSubmit = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        setError(null);
        try {
            if (isRegister) {
                const userCredential = await createUserWithEmailAndPassword(auth, email, password);
                // Create the user document with 'admin' type to pass the AuthContext check
                await setDoc(doc(db, "users", userCredential.user.uid), {
                    email: userCredential.user.email,
                    userType: 'admin',
                    createdAt: new Date().toISOString()
                });
                // Note: AuthContext might trigger before setDoc finishes and may call signOut if it doesn't see the doc in time.
                // If you get signed out immediately upon registering, just sign in again now that the doc is created.
            } else {
                await signInWithEmailAndPassword(auth, email, password);
            }
        } catch (err: unknown) {
            setError(err instanceof Error ? err.message : String(err));
            setLoading(false);
        }
    };

    return (
        <div className="flex h-screen items-center justify-center bg-[var(--bg0)] transition-colors duration-300">
            <div className="bg-[var(--bg1)] border border-[var(--bd)] p-8 rounded-[24px] w-full max-w-sm shadow-2xl relative">
                <button
                    type="button"
                    className="absolute top-4 right-4 p-2 text-[var(--t2)] hover:text-[var(--t0)] transition-colors"
                    onClick={() => setIsDark(!isDark)}
                >
                    {isDark ? <Sun size={18} /> : <Moon size={18} />}
                </button>

                <div className="flex flex-col items-center gap-3 mb-8 justify-center">
                    <div className="w-12 h-12 rounded-2xl card-gradient flex items-center justify-center text-white font-bold text-2xl shadow-lg shadow-[var(--pri-transparent)]">R</div>
                    <h1 className="font-fraunces text-2xl font-bold text-[var(--t0)]">
                        {isRegister ? 'Register Admin' : 'RentO Admin'}
                    </h1>
                </div>

                <form onSubmit={handleSubmit} className="flex flex-col gap-4">
                    <div>
                        <label htmlFor="email" className="block text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider mb-2">Email Address</label>
                        <div className="relative">
                            <input
                                id="email"
                                type="email"
                                value={email}
                                onChange={e => setEmail(e.target.value)}
                                className="w-full bg-[var(--bg2)] border border-[var(--bd)] text-[var(--t0)] rounded-xl py-3 pl-4 pr-10 text-sm focus:outline-none focus:border-[var(--pri)] transition-colors"
                                required
                            />
                            <div className="absolute right-3 top-1/2 -translate-y-1/2 text-[var(--t2)]"><Users size={16} /></div>
                        </div>
                    </div>
                    <div>
                        <label htmlFor="password" className="block text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider mb-2">Password</label>
                        <div className="relative">
                            <input
                                id="password"
                                type="password"
                                value={password}
                                onChange={e => setPassword(e.target.value)}
                                className="w-full bg-[var(--bg2)] border border-[var(--bd)] text-[var(--t0)] rounded-xl py-3 pl-4 pr-10 text-sm focus:outline-none focus:border-[var(--pri)] transition-colors"
                                required
                            />
                            <div className="absolute right-3 top-1/2 -translate-y-1/2 text-[var(--t2)]"><Eye size={16} /></div>
                        </div>
                    </div>

                    {error && (
                        <div className="bg-[rgba(224,96,96,0.15)] border border-[var(--red)] p-3 rounded-xl mt-1">
                            <p className="text-[12px] font-bold text-[var(--red)] text-center">{error}</p>
                        </div>
                    )}

                    <button
                        type="submit"
                        disabled={loading}
                        className="mt-4 card-gradient rounded-full w-full py-3.5 text-sm font-bold text-white shadow-lg shadow-[var(--pri-transparent)] hover:opacity-90 transition-opacity disabled:opacity-50"
                    >
                        {loading ? (isRegister ? 'Registering...' : 'Signing in...') : (isRegister ? 'Register' : 'Sign In')}
                    </button>

                    <button
                        type="button"
                        onClick={() => setIsRegister(!isRegister)}
                        className="mt-2 text-sm text-[var(--t2)] hover:text-[var(--t0)] transition-colors"
                    >
                        {isRegister ? 'Already have an account? Sign In' : 'Need an admin account? Register'}
                    </button>
                </form>
            </div>
        </div>
    );
}
