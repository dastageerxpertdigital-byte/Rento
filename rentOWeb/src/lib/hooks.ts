"use client";

import { useState, useEffect } from 'react';
import { collection, doc, onSnapshot, query, getCountFromServer, QueryConstraint } from "firebase/firestore";
import { db } from './firebase';

export function useCollection(collectionPath: string, constraints: QueryConstraint[] = []) {
    const [data, setData] = useState<Record<string, unknown>[]>([]);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        if (!collectionPath) return;

        try {
            const q = query(collection(db, collectionPath), ...constraints);
            const unsubscribe = onSnapshot(q, (snapshot) => {
                const result: Record<string, unknown>[] = [];
                snapshot.forEach(d => result.push({ id: d.id, ...d.data() }));
                setData(result);
                setIsLoading(false);
            }, (err) => {
                setError(err.message);
                setIsLoading(false);
            });
            return unsubscribe;
        } catch (e: unknown) {
            setError(e instanceof Error ? e.message : String(e));
            setIsLoading(false);
        }
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [collectionPath, JSON.stringify(constraints)]);

    return { data, isLoading, error };
}

export function useDocument(path: string | null) {
    const [data, setData] = useState<Record<string, unknown> | null>(null);
    const [isLoading, setIsLoading] = useState(true);

    useEffect(() => {
        if (!path) {
            setIsLoading(false);
            return;
        }
        const unsubscribe = onSnapshot(doc(db, path), (docSnap) => {
            if (docSnap.exists()) {
                setData({ id: docSnap.id, ...docSnap.data() });
            } else {
                setData(null);
            }
            setIsLoading(false);
        });
        return unsubscribe;
    }, [path]);

    return { data, isLoading };
}

export function useCollectionCount(collectionPath: string, constraints: QueryConstraint[] = []) {
    const [count, setCount] = useState(0);

    useEffect(() => {
        let isMounted = true;
        async function fetchCount() {
            try {
                const q = query(collection(db, collectionPath), ...constraints);
                const snapshot = await getCountFromServer(q);
                if (isMounted) setCount(snapshot.data().count);
            } catch (err) {
                console.error("Error fetching count:", err);
            }
        }
        fetchCount();
        return () => {
            isMounted = false;
        };
        // eslint-disable-next-line react-hooks/exhaustive-deps
    }, [collectionPath, JSON.stringify(constraints)]);

    return count;
}
