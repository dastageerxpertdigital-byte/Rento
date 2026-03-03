/* eslint-disable @typescript-eslint/no-explicit-any */
"use client";

import { useState, useMemo } from 'react';
import { useCollection } from '../../lib/hooks';
import { StatusBadge, InputReasonDialog } from '../ui/index';
import { Slash, CheckCircle, Search, Filter, ChevronDown } from 'lucide-react';
import { doc, updateDoc } from 'firebase/firestore';
import { db } from '../../lib/firebase';
import { TableSkeleton } from '../ui/PageLoader';

export default function ReportsView() {
    const { data: items, isLoading } = useCollection("reports");
    const [showBlockDialog, setShowBlockDialog] = useState<any>(null);

    // Search and Filter State
    const [searchQuery, setSearchQuery] = useState('');
    const [statusFilter, setStatusFilter] = useState('All');

    const filteredItems = useMemo(() => {
        return items.filter((r: any) => {
            const searchStr = `${r.reason || ''} ${r.reportedUserId || ''} ${r.reportedListingId || ''} ${r.reporterId || ''}`.toLowerCase();
            const matchesSearch = searchStr.includes(searchQuery.toLowerCase());
            const matchesStatus = statusFilter === 'All' || (r.status || 'Pending') === statusFilter;
            return matchesSearch && matchesStatus;
        });
    }, [items, searchQuery, statusFilter]);

    const handleBlockUser = async (reason: string) => {
        if (!showBlockDialog) return;
        try {
            await updateDoc(doc(db, "users", showBlockDialog.reportedUserId), {
                status: 'Blocked', blockReason: reason
            });
            await updateDoc(doc(db, "reports", showBlockDialog.id), { status: 'Resolved' });
            setShowBlockDialog(null);
        } catch { alert("Failed to block user and resolve report"); }
    };

    if (isLoading) return <TableSkeleton rows={8} cols={5} />;

    return (
        <div className="h-full flex flex-col relative animate-in fade-in duration-500 flex-1">
            {/* Header / Toolbar */}
            <div className="flex flex-col md:flex-row md:items-center justify-between mb-6 gap-4">
                <div className="flex flex-col sm:flex-row items-center gap-3 w-full md:w-auto">
                    <div className="relative w-full sm:w-auto">
                        <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-[var(--t2)]" size={16} />
                        <input
                            type="text"
                            placeholder="Search reports..."
                            value={searchQuery}
                            onChange={(e) => setSearchQuery(e.target.value)}
                            className="w-full sm:w-64 pl-9 pr-4 py-2 border border-[var(--bd)] rounded-full text-sm bg-[var(--bg0)] focus:outline-none focus:border-[var(--pri)] text-[var(--t0)] transition-colors"
                        />
                    </div>
                    <div className="relative w-full sm:w-auto">
                        <Filter className="absolute left-3 top-1/2 -translate-y-1/2 text-[var(--t2)]" size={16} />
                        <select
                            value={statusFilter}
                            onChange={(e) => setStatusFilter(e.target.value)}
                            className="w-full sm:w-40 appearance-none pl-9 pr-8 py-2 border border-[var(--bd)] rounded-full text-sm bg-[var(--bg0)] focus:outline-none focus:border-[var(--pri)] text-[var(--t0)] cursor-pointer transition-colors"
                        >
                            <option value="All">All Status</option>
                            <option value="Pending">Pending</option>
                            <option value="Resolved">Resolved</option>
                        </select>
                        <ChevronDown className="absolute right-3 top-1/2 -translate-y-1/2 text-[var(--t2)] pointer-events-none" size={16} />
                    </div>
                </div>
                <div className="text-[13px] font-bold text-[var(--t1)] bg-[var(--bg1)] px-4 py-2 rounded-full border border-[var(--bd)] self-start md:self-auto shadow-sm">
                    Total: {filteredItems.length}
                </div>
            </div>

            <div className="flex-1 overflow-y-auto pb-4">
                {items.length === 0 ? (
                    <div className="py-12 flex flex-col items-center justify-center text-center border border-[var(--bd)] rounded-2xl bg-[var(--bg0)]">
                        <div className="w-16 h-16 bg-[var(--bg3)] text-[var(--t2)] rounded-2xl flex items-center justify-center mb-4 font-fraunces font-bold text-xl">0</div>
                        <h3 className="text-sm font-bold text-[var(--t0)]">No reports found.</h3>
                    </div>
                ) : (
                    <div className="overflow-x-auto border border-[var(--bd)] rounded-2xl bg-[var(--bg0)]">
                        <table className="w-full text-left border-collapse">
                            <thead>
                                <tr className="border-b border-[var(--bd)] bg-[var(--bg1)]">
                                    <th className="py-3 px-4 text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider">Reason</th>
                                    <th className="py-3 px-4 text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider">Reported User/Listing</th>
                                    <th className="py-3 px-4 text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider">Reported By</th>
                                    <th className="py-3 px-4 text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider">Status</th>
                                    <th className="py-3 px-4 text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider text-right">Actions</th>
                                </tr>
                            </thead>
                            <tbody className="text-sm">
                                {filteredItems.map((row: any, index: number) => (
                                    <tr key={row.id} style={{ animationDelay: `${(index % 20) * 30}ms`, animationFillMode: 'both' }} className="border-b last:border-0 border-[var(--bd)] hover:bg-[var(--bg3)] transition-colors group animate-in fade-in slide-in-from-bottom-2 duration-300">
                                        <td className="py-4 px-4 text-[13px] text-[var(--t0)] font-medium max-w-[200px] truncate group-hover:text-[var(--pri)] transition-colors" title={row.reason}>{row.reason || 'No reason'}</td>
                                        <td className="py-4 px-4 text-[13px] text-[var(--t1)] font-mono">{row.reportedUserId || row.reportedListingId || 'Unknown'}</td>
                                        <td className="py-4 px-4 text-[13px] text-[var(--t2)] font-mono">{row.reporterId}</td>
                                        <td className="py-4 px-4"><StatusBadge status={row.status || 'Pending'} /></td>
                                        <td className="py-4 px-4 flex justify-end gap-2 text-right">
                                            {row.reportedUserId && row.status !== 'Resolved' && (
                                                <button onClick={() => setShowBlockDialog(row)} className="px-3 py-1.5 bg-[var(--bg1)] border border-[var(--bd)] text-[var(--acc)] font-bold text-[11px] rounded-full hover:bg-[rgba(240,201,74,0.15)] transition-colors inline-flex items-center gap-1 shadow-sm">
                                                    <Slash size={12} /> Block User
                                                </button>
                                            )}
                                            {row.status !== 'Resolved' && (
                                                <button onClick={async () => await updateDoc(doc(db, "reports", row.id), { status: 'Resolved' })} className="px-3 py-1.5 bg-[var(--bg1)] border border-[var(--bd)] text-green-500 font-bold text-[11px] rounded-full hover:bg-[rgba(34,197,94,0.15)] transition-colors inline-flex items-center gap-1 shadow-sm">
                                                    <CheckCircle size={12} /> Resolve
                                                </button>
                                            )}
                                        </td>
                                    </tr>
                                ))}
                                {filteredItems.length === 0 && (
                                    <tr><td colSpan={5} className="py-12 text-center text-[var(--t2)] text-sm font-bold">No reports match your filters.</td></tr>
                                )}
                            </tbody>
                        </table>
                    </div>
                )}
            </div>

            {showBlockDialog && (
                <InputReasonDialog
                    title="Block Mentioned User"
                    message="Provide a reason for blocking the reported user."
                    onConfirm={handleBlockUser}
                    onCancel={() => setShowBlockDialog(null)}
                />
            )}
        </div>
    );
}
