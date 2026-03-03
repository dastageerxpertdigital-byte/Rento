"use client";

import { useState, useEffect } from 'react';
import { useRouter } from 'next/navigation';
import { createUserWithEmailAndPassword } from 'firebase/auth';
import { doc, setDoc } from 'firebase/firestore';
import { auth, db } from '../../lib/firebase';
import { Building2 } from 'lucide-react';

export default function RegisterAdminPage() {
    const router = useRouter();
    const [isLocalhost, setIsLocalhost] = useState(false);
    const [email, setEmail] = useState('');
    const [password, setPassword] = useState('');
    const [name, setName] = useState('');
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        if (typeof window !== 'undefined') {
            if (window.location.hostname === 'localhost' || window.location.hostname === '127.0.0.1') {
                setIsLocalhost(true);
            } else {
                router.replace('/');
            }
        }
    }, [router]);

    const handleRegister = async (e: React.FormEvent) => {
        e.preventDefault();
        setLoading(true);
        setError(null);

        try {
            const userCredential = await createUserWithEmailAndPassword(auth, email, password);
            const user = userCredential.user;

            await setDoc(doc(db, "users", user.uid), {
                email: user.email,
                name: name,
                userType: 'admin',
                createdAt: new Date()
            });

            router.replace('/');
        } catch (err: unknown) {
            setError(err instanceof Error ? err.message : String(err));
            setLoading(false);
        }
    };

    if (!isLocalhost) return null; // Wait for redirect if not localhost

    return (
        <div className="min-h-screen bg-[var(--bg0)] flex items-center justify-center p-4 selection:bg-[var(--pri-transparent)] selection:text-[var(--pri)]">
            <div className="w-full max-w-md">
                <div className="mb-10 text-center">
                    <div className="w-16 h-16 card-gradient rounded-3xl mx-auto flex items-center justify-center mb-6 shadow-2xl shadow-[var(--pri-transparent)]">
                        <Building2 className="text-white" size={32} />
                    </div>
                    <h1 className="text-3xl font-fraunces font-bold text-[var(--t0)] mb-2">Create Initial Admin</h1>
                    <p className="text-[var(--t2)] text-sm">This page is only accessible on localhost.</p>
                </div>

                <div className="bg-[var(--bg1)] border border-[var(--bd)] p-8 rounded-[32px] shadow-sm">
                    {error && (
                        <div className="mb-6 p-4 bg-[rgba(224,96,96,0.1)] border border-[rgba(224,96,96,0.2)] rounded-2xl text-[var(--red)] text-sm font-medium">
                            {error}
                        </div>
                    )}

                    <form onSubmit={handleRegister} className="space-y-5">
                        <div className="space-y-1.5">
                            <label className="text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider ml-1">Full Name</label>
                            <input
                                type="text"
                                required
                                value={name}
                                onChange={(e) => setName(e.target.value)}
                                className="w-full bg-[var(--bg0)] border border-[var(--bd)] text-[var(--t0)] rounded-2xl py-3.5 px-5 text-[15px] focus:outline-none focus:border-[var(--pri)] transition-colors placeholder-[var(--t3)]"
                                placeholder="Admin Name"
                            />
                        </div>

                        <div className="space-y-1.5">
                            <label className="text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider ml-1">Email Address</label>
                            <input
                                type="email"
                                required
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                className="w-full bg-[var(--bg0)] border border-[var(--bd)] text-[var(--t0)] rounded-2xl py-3.5 px-5 text-[15px] focus:outline-none focus:border-[var(--pri)] transition-colors placeholder-[var(--t3)]"
                                placeholder="admin@rento.pk"
                            />
                        </div>

                        <div className="space-y-1.5">
                            <label className="text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider ml-1">Password</label>
                            <input
                                type="password"
                                required
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                className="w-full bg-[var(--bg0)] border border-[var(--bd)] text-[var(--t0)] rounded-2xl py-3.5 px-5 text-[15px] focus:outline-none focus:border-[var(--pri)] transition-colors placeholder-[var(--t3)]"
                                placeholder="••••••••"
                            />
                        </div>

                        <button
                            type="submit"
                            disabled={loading}
                            className="w-full mt-4 card-gradient py-4 rounded-2xl text-white font-bold text-[15px] shadow-lg shadow-[var(--pri-transparent)] hover:shadow-xl hover:-translate-y-0.5 transition-all duration-300 disabled:opacity-70 disabled:hover:translate-y-0 disabled:hover:shadow-lg"
                        >
                            {loading ? 'Creating...' : 'Create Admin Account'}
                        </button>
                    </form>
                </div>
            </div>
        </div>
    );
}
