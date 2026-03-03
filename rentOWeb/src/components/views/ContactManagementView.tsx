/* eslint-disable @typescript-eslint/no-explicit-any */
"use client";

import { useCollection } from '../../lib/hooks';
import { StatusBadge } from '../ui/index';
import PageLoader from '../ui/PageLoader';
import { CheckCircle } from 'lucide-react';
import { doc, updateDoc } from 'firebase/firestore';
import { db } from '../../lib/firebase';

export default function ContactManagementView() {
    const { data: items, isLoading } = useCollection("feedback");

    if (isLoading) return <PageLoader message="Loading feedback tickets..." />;

    return (
        <div className="h-full flex flex-col relative animate-in fade-in duration-500">
            <div className="flex items-center justify-between mb-6">
                <h2 className="text-xl font-fraunces font-bold text-[var(--t0)]">Contact Management (User Feedbacks)</h2>
            </div>

            <div className="flex-1 overflow-y-auto">
                {items.length === 0 ? (
                    <div className="py-12 flex flex-col items-center justify-center text-center border border-[var(--bd)] rounded-2xl bg-[var(--bg0)]">
                        <div className="w-16 h-16 bg-[var(--bg3)] text-[var(--t2)] rounded-2xl flex items-center justify-center mb-4 font-fraunces font-bold text-xl">0</div>
                        <h3 className="text-sm font-bold text-[var(--t0)]">No feedback tickets found.</h3>
                    </div>
                ) : (
                    <div className="overflow-x-auto border border-[var(--bd)] rounded-2xl bg-[var(--bg0)]">
                        <table className="w-full text-left border-collapse">
                            <thead>
                                <tr className="border-b border-[var(--bd)] bg-[var(--bg1)]">
                                    <th className="py-3 px-4 text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider">User</th>
                                    <th className="py-3 px-4 text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider">Message</th>
                                    <th className="py-3 px-4 text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider">Status</th>
                                    <th className="py-3 px-4 text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider text-right">Actions</th>
                                </tr>
                            </thead>
                            <tbody className="text-sm">
                                {items.map((row: any) => (
                                    <tr key={row.id} className="border-b last:border-0 border-[var(--bd)] hover:bg-[var(--bg3)] transition-colors">
                                        <td className="py-4 px-4 text-[13px] text-[var(--t0)] font-medium font-mono">{row.userId || 'Anonymous'}</td>
                                        <td className="py-4 px-4 text-[13px] text-[var(--t1)]">{row.message || 'No message'}</td>
                                        <td className="py-4 px-4"><StatusBadge status={row.status || 'Pending'} /></td>
                                        <td className="py-4 px-4 flex justify-end gap-2">
                                            {row.status !== 'Resolved' && (
                                                <button onClick={async () => await updateDoc(doc(db, "feedback", row.id), { status: 'Resolved' })} className="px-3 py-1 bg-[rgba(34,197,94,0.1)] text-green-500 font-bold text-[11px] rounded-full hover:bg-[rgba(34,197,94,0.2)] transition-colors inline-flex items-center gap-1">
                                                    <CheckCircle size={12} /> Resolve
                                                </button>
                                            )}
                                        </td>
                                    </tr>
                                ))}
                            </tbody>
                        </table>
                    </div>
                )}
            </div>
        </div>
    );
}
