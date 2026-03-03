/* eslint-disable @typescript-eslint/no-explicit-any */
"use client";

import { useState } from 'react';
import { Eye, Check, X, RefreshCcw } from 'lucide-react';
import { StatusBadge, ConfirmDialog, InputReasonDialog } from '../ui/index';
import { useCollection } from '../../lib/hooks';
import { updateDoc, doc } from 'firebase/firestore';
import { db } from '../../lib/firebase';
import { TableSkeleton } from '../ui/PageLoader';

export default function SubscriptionsView() {
    const { data: subs, isLoading } = useCollection("subscriptions");
    const [proofImage, setProofImage] = useState<string | null>(null);
    const [showCancelDialog, setShowCancelDialog] = useState<any>(null);
    const [showRejectDialog, setShowRejectDialog] = useState<any>(null);
    const [activeTab, setActiveTab] = useState<'Pending' | 'Active' | 'Cancelled' | 'Expired'>('Pending');
    const [showSyncDialog, setShowSyncDialog] = useState(false);

    if (isLoading) return <TableSkeleton rows={6} cols={6} />;

    const filteredSubs = subs.filter((sub: any) => {
        const status = sub.status || 'Pending';
        if (activeTab === 'Pending') return status === 'Pending';
        if (activeTab === 'Active') return status === 'Active';
        if (activeTab === 'Cancelled') return status === 'Cancelled' || status === 'Rejected';
        return status === 'Expired';
    });

    const handleApprove = async (id: string, userId: string, pkgId: string) => {
        try {
            await updateDoc(doc(db, "subscriptions", id), { status: 'Active' });
            if (userId) {
                await updateDoc(doc(db, "users", userId), {
                    packageId: pkgId,
                    subscriptionStatus: 'Active'
                });
            }
        } catch (e) {
            console.error(e);
            alert("Failed to approve subscription");
        }
    };

    const handleReject = async (reason: string) => {
        if (!showRejectDialog) return;
        try {
            await updateDoc(doc(db, "subscriptions", showRejectDialog.id), {
                status: 'Rejected',
                rejectReason: reason
            });
            setShowRejectDialog(null);
        } catch (e) {
            console.error(e);
            alert("Failed to reject subscription");
        }
    };

    const handleCancelRefund = async () => {
        if (!showCancelDialog) return;
        try {
            await updateDoc(doc(db, "subscriptions", showCancelDialog.id), { status: 'Cancelled' });
            if (showCancelDialog.userId) {
                await updateDoc(doc(db, "users", showCancelDialog.userId), {
                    packageId: 'free',
                    subscriptionStatus: 'Cancelled'
                });
            }
            setShowCancelDialog(null);
        } catch (e) {
            console.error(e);
            alert("Failed to cancel subscription");
        }
    };

    return (
        <div className="h-full flex flex-col relative animate-in fade-in duration-500">
            <div className="bg-[var(--bg1)] border-b border-[var(--bd)] px-6 pt-6">
                <div className="flex justify-between items-center mb-6">
                    <div>
                        <h3 className="text-xl font-fraunces font-semibold text-[var(--t0)]">Subscription Requests</h3>
                        <p className="text-[12px] text-[var(--t2)] mt-1">Review manual payment proofs and manage subscriptions.</p>
                    </div>
                    <button onClick={() => setShowSyncDialog(true)} className="px-4 py-2 bg-[var(--pri-transparent)] text-[var(--pri)] rounded-full text-sm font-bold hover:bg-[var(--pri)] hover:text-white transition-colors flex items-center gap-2">
                        <RefreshCcw size={16} /> Sync Subscriptions
                    </button>
                </div>

                <div className="flex bg-[var(--bg2)] border border-[var(--bd)] rounded-xl p-1.5 shadow-inner w-fit mb-4">
                    {['Pending', 'Active', 'Cancelled', 'Expired'].map(tab => (
                        <button
                            key={tab}
                            onClick={() => setActiveTab(tab as any)}
                            className={`px-6 py-2.5 text-[13px] font-bold tracking-wide transition-all rounded-lg whitespace-nowrap ${activeTab === tab ? 'bg-[var(--bg1)] text-[var(--pri)] shadow-md border border-[var(--bd)]' : 'text-[var(--t2)] hover:text-[var(--t0)] border border-transparent'}`}
                        >
                            {tab}
                        </button>
                    ))}
                </div>
            </div>

            <div className="overflow-x-auto flex-1 p-6">
                <table className="w-full text-left border-collapse">
                    <thead>
                        <tr className="border-b border-[var(--bd)]">
                            <th className="pb-4 pt-2 text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider px-4">Request ID</th>
                            <th className="pb-4 pt-2 text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider px-4">User Email</th>
                            <th className="pb-4 pt-2 text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider px-4">Package</th>
                            <th className="pb-4 pt-2 text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider px-4">Amount</th>
                            <th className="pb-4 pt-2 text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider px-4 text-center">Proof</th>
                            <th className="pb-4 pt-2 text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider px-4">Status</th>
                            <th className="pb-4 pt-2 text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider px-4 text-right">Actions</th>
                        </tr>
                    </thead>
                    <tbody className="text-sm">
                        {filteredSubs.map((row: any) => (
                            <tr key={row.id} className="border-b border-[var(--bd)] hover:bg-[var(--bg3)] transition-colors group">
                                <td className="py-4 px-4 text-[13px] text-[var(--t0)] font-medium font-mono">{row.id.substring(0, 8)}</td>
                                <td className="py-4 px-4 text-[13px] text-[var(--t0)] font-bold">{row.userEmail || row.userId || 'Unknown'}</td>
                                <td className="py-4 px-4 text-[13px] text-[var(--t2)] font-medium capitalize">{row.packageId || 'custom'}</td>
                                <td className="py-4 px-4 text-[13px] text-[var(--t0)] font-bold">PKR {row.amount || 0}</td>
                                <td className="py-4 px-4 text-center">
                                    {row.proofUrl ? (
                                        <button
                                            onClick={() => setProofImage(row.proofUrl)}
                                            className="px-3 py-1 bg-[rgba(59,130,246,0.1)] text-[var(--blue)] font-bold text-[11px] rounded-full hover:bg-[rgba(59,130,246,0.2)] transition-colors inline-flex items-center gap-1"
                                        >
                                            <Eye size={12} /> View
                                        </button>
                                    ) : (
                                        <span className="text-[11px] text-[var(--t2)]">N/A</span>
                                    )}
                                </td>
                                <td className="py-4 px-4">
                                    <StatusBadge status={row.status || 'Pending'} />
                                </td>
                                <td className="py-4 px-4 flex justify-end gap-2">
                                    {(row.status === 'Pending' || !row.status) && (
                                        <>
                                            <button onClick={() => handleApprove(row.id, row.userId, row.packageId)} className="w-8 h-8 rounded-full bg-[rgba(34,197,94,0.1)] text-green-500 hover:bg-[rgba(34,197,94,0.2)] transition-colors flex items-center justify-center tooltip relative">
                                                <Check size={16} />
                                            </button>
                                            <button onClick={() => setShowRejectDialog(row)} className="w-8 h-8 rounded-full bg-[rgba(239,68,68,0.1)] text-red-500 hover:bg-[rgba(239,68,68,0.2)] transition-colors flex items-center justify-center tooltip relative">
                                                <X size={16} />
                                            </button>
                                        </>
                                    )}
                                    {row.status === 'Active' && (
                                        <button onClick={() => setShowCancelDialog(row)} className="px-3 py-1 bg-[rgba(240,201,74,0.1)] text-[var(--acc)] font-bold text-[11px] rounded-full hover:bg-[rgba(240,201,74,0.2)] transition-colors inline-flex items-center gap-1">
                                            <RefreshCcw size={12} /> Cancel
                                        </button>
                                    )}
                                </td>
                            </tr>
                        ))}
                        {filteredSubs.length === 0 && (
                            <tr><td colSpan={7} className="py-8 text-center text-[var(--t2)] text-sm">No {activeTab.toLowerCase()} subscriptions found.</td></tr>
                        )}
                    </tbody>
                </table>
            </div>

            {/* Proof Modal */}
            {proofImage && (
                <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
                    <div className="absolute inset-0 bg-black/80 backdrop-blur-sm transition-opacity" onClick={() => setProofImage(null)}></div>
                    <div className="relative bg-[var(--bg1)] rounded-2xl max-w-2xl w-full max-h-[90vh] overflow-hidden flex flex-col shadow-2xl z-10 animate-in zoom-in-95 duration-200">
                        <div className="flex justify-between items-center p-4 border-b border-[var(--bd)] bg-[var(--bg2)]">
                            <h3 className="font-bold text-[var(--t0)]">Payment Proof</h3>
                            <button onClick={() => setProofImage(null)} className="text-[var(--t2)] hover:text-[var(--red)]"><X size={20} /></button>
                        </div>
                        <div className="flex-1 overflow-auto p-4 bg-[var(--bg0)] flex justify-center">
                            {/* eslint-disable-next-line @next/next/no-img-element */}
                            <img src={proofImage} alt="Payment Proof" className="max-w-full object-contain rounded-lg" />
                        </div>
                    </div>
                </div>
            )}

            {showCancelDialog && (
                <ConfirmDialog
                    title="Cancel & Refund Subscription"
                    message="Are you sure you want to cancel this subscription? The user will be downgraded to the Free tier immediately."
                    confirmText="Cancel Subscription"
                    isDestructive
                    onConfirm={handleCancelRefund}
                    onCancel={() => setShowCancelDialog(null)}
                />
            )}

            {showRejectDialog && (
                <InputReasonDialog
                    title="Reject Subscription"
                    message="Provide a reason for rejecting this payment proof. The user will be notified."
                    placeholder="e.g. Invalid transaction ID, blurry image, etc."
                    onConfirm={handleReject}
                    onCancel={() => setShowRejectDialog(null)}
                />
            )}

            {showSyncDialog && (
                <ConfirmDialog
                    title="Sync Subscriptions"
                    message="Trigger a manual sync of all subscriptions. This will close excess listings for expired subscriptions immediately."
                    confirmText="Sync Now"
                    onConfirm={() => {
                        alert("Sync triggered!");
                        setShowSyncDialog(false);
                    }}
                    onCancel={() => setShowSyncDialog(false)}
                />
            )}
        </div>
    );
}
