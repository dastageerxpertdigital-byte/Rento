"use client";

import React, { createContext, useContext, useState, useEffect } from 'react';
import { onAuthStateChanged, signOut, User as FirebaseUser } from "firebase/auth";
import { doc, getDoc } from "firebase/firestore";
import { auth, db } from '../lib/firebase';

type AuthUser = FirebaseUser & Record<string, unknown>;

interface AuthContextType {
    user: AuthUser | null;
    loading: boolean;
    error: string | null;
    setError: (error: string | null) => void;
}

const AuthContext = createContext<AuthContextType>({
    user: null,
    loading: true,
    error: null,
    setError: () => { }
});

export function AuthProvider({ children }: { children: React.ReactNode }) {
    const [user, setUser] = useState<AuthUser | null>(null);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        const unsubscribe = onAuthStateChanged(auth, async (firebaseUser) => {
            if (firebaseUser) {
                try {
                    const userDoc = await getDoc(doc(db, "users", firebaseUser.uid));
                    if (userDoc.exists() && userDoc.data().userType === 'admin') {
                        setUser({ ...firebaseUser, ...userDoc.data() });
                    } else {
                        await signOut(auth);
                        setError("Access denied. Admin accounts only.");
                        setUser(null);
                    }
                } catch (err: unknown) {
                    setError(err instanceof Error ? err.message : String(err));
                    setUser(null);
                }
            } else {
                setUser(null);
            }
            setLoading(false);
        });
        return unsubscribe;
    }, []);

    return (
        <AuthContext.Provider value={{ user, loading, error, setError }}>
            {children}
        </AuthContext.Provider>
    );
}

export const useAuth = () => useContext(AuthContext);
