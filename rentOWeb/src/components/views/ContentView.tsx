/* eslint-disable @typescript-eslint/no-explicit-any */
"use client";

import { useState, useEffect } from 'react';
import { useCollection } from '../../lib/hooks';
import { ImageModal, StatusBadge, ConfirmDialog, InputReasonDialog } from '../ui/index';
import { Trash2, Plus, Eye, Slash, CheckCircle } from 'lucide-react';
import { deleteDoc, doc, updateDoc } from 'firebase/firestore';
import { db } from '../../lib/firebase';
import PageLoader from '../ui/PageLoader';

const tabs = [
    { id: 'feedback', label: 'Feedback & Support' },
    { id: 'sliders', label: 'App Sliders' },
    { id: 'reports', label: 'User Reports' },
    { id: 'notifications', label: 'Push Notifications' }
];

export default function ContentView() {
    const [activeTab, setActiveTab] = useState('feedback');

    useEffect(() => {
        const hash = window.location.hash.replace('#', '');
        if (tabs.some(t => t.id === hash)) {
            setActiveTab(hash);
        }
    }, []);

    const handleTabClick = (id: string) => {
        setActiveTab(id);
        window.location.hash = id;
    };

    return (
        <div className="h-full flex flex-col relative animate-in fade-in duration-500">
            {/* Header & Tabs */}
            <div className="bg-[var(--bg1)] border-b border-[var(--bd)] px-6 pt-6">
                <h2 className="text-xl font-fraunces font-bold text-[var(--t0)] mb-4">Content Management</h2>
                <div className="flex gap-6 overflow-x-auto no-scrollbar">
                    {tabs.map(tab => (
                        <button
                            key={tab.id}
                            onClick={() => handleTabClick(tab.id)}
                            className={`pb-3 text-sm font-bold tracking-wide transition-colors border-b-2 whitespace-nowrap ${activeTab === tab.id ? 'text-[var(--pri)] border-[var(--pri)]' : 'text-[var(--t2)] hover:text-[var(--t0)] border-transparent'}`}
                        >
                            {tab.label}
                        </button>
                    ))}
                </div>
            </div>

            {/* Tab Contents */}
            <div className="p-6 flex-1 overflow-y-auto">
                {activeTab === 'feedback' && <FeedbackTable />}
                {activeTab === 'sliders' && <SlidersTable />}
                {activeTab === 'reports' && <ReportsTable />}
                {activeTab === 'notifications' && <GenericTable tabName="notifications" collectionName="notifications" />}
            </div>
        </div>
    );
}

// -------------------------------------------------------------------------------------------------
// Generic Fallback Table
// -------------------------------------------------------------------------------------------------
function GenericTable({ tabName, collectionName }: { tabName: string, collectionName: string }) {
    const { data: items, isLoading } = useCollection(collectionName);

    if (isLoading) return <PageLoader />;

    if (items.length === 0) {
        return (
            <div className="py-12 flex flex-col items-center justify-center text-center">
                <div className="w-16 h-16 bg-[var(--bg3)] text-[var(--t2)] rounded-2xl flex items-center justify-center mb-4 font-fraunces font-bold text-xl">0</div>
                <h3 className="text-sm font-bold text-[var(--t0)]">No {tabName} found.</h3>
            </div>
        );
    }

    const columns = Object.keys(items[0]).filter(k => k !== 'id' && (typeof items[0][k] === 'string' || typeof items[0][k] === 'number')).slice(0, 5);

    return (
        <div className="overflow-x-auto border border-[var(--bd)] rounded-2xl bg-[var(--bg0)]">
            <table className="w-full text-left border-collapse">
                <thead>
                    <tr className="border-b border-[var(--bd)] bg-[var(--bg1)]">
                        {columns.map(col => (
                            <th key={col} className="py-3 px-4 text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider">{col}</th>
                        ))}
                    </tr>
                </thead>
                <tbody className="text-sm">
                    {items.map((row: any) => (
                        <tr key={row.id} className="border-b last:border-0 border-[var(--bd)] hover:bg-[var(--bg3)] transition-colors">
                            {columns.map(col => (
                                <td key={col} className="py-4 px-4 text-[13px] text-[var(--t0)] font-medium">
                                    {String(row[col])}
                                </td>
                            ))}
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    );
}

// -------------------------------------------------------------------------------------------------
// Sliders Table
// -------------------------------------------------------------------------------------------------
function SlidersTable() {
    const { data: items, isLoading } = useCollection("sliders");
    const [previewImage, setPreviewImage] = useState<string | null>(null);
    const [showDeleteDialog, setShowDeleteDialog] = useState<any>(null);

    const handleDelete = async () => {
        if (!showDeleteDialog) return;
        try {
            await deleteDoc(doc(db, "sliders", showDeleteDialog.id));
            setShowDeleteDialog(null);
        } catch { alert("Failed to delete slider"); }
    };

    if (isLoading) return <div className="text-[var(--t2)] text-sm animate-pulse">Loading sliders...</div>;

    return (
        <div>
            <div className="flex justify-end mb-4">
                <button className="px-4 py-2 card-gradient text-white rounded-full text-sm font-bold shadow-lg shadow-[var(--pri-transparent)] hover:opacity-90 transition-opacity flex items-center gap-2">
                    <Plus size={16} /> Add Slider
                </button>
            </div>

            {items.length === 0 ? (
                <div className="py-12 flex flex-col items-center justify-center text-center border border-[var(--bd)] rounded-2xl bg-[var(--bg0)]">
                    <div className="w-16 h-16 bg-[var(--bg3)] text-[var(--t2)] rounded-2xl flex items-center justify-center mb-4 font-fraunces font-bold text-xl">0</div>
                    <h3 className="text-sm font-bold text-[var(--t0)]">No sliders found.</h3>
                </div>
            ) : (
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    {items.map((slider: any) => (
                        <div key={slider.id} className="bg-[var(--bg0)] border border-[var(--bd)] rounded-[24px] overflow-hidden group">
                            <div className="relative h-48 bg-[var(--bg4)]">
                                {slider.imageUrl ? (
                                    /* eslint-disable-next-line @next/next/no-img-element */
                                    <img src={slider.imageUrl} alt="Slider" className="w-full h-full object-cover" />
                                ) : (
                                    <div className="w-full h-full flex items-center justify-center text-[var(--t2)]">No Image</div>
                                )}
                                <div className="absolute inset-0 bg-black/40 opacity-0 group-hover:opacity-100 transition-opacity flex items-center justify-center gap-3">
                                    <button onClick={() => setPreviewImage(slider.imageUrl)} className="w-10 h-10 rounded-full bg-white/20 backdrop-blur text-white flex items-center justify-center hover:bg-white/30 transition-colors">
                                        <Eye size={20} />
                                    </button>
                                    <button onClick={() => setShowDeleteDialog(slider)} className="w-10 h-10 rounded-full bg-[rgba(224,96,96,0.3)] backdrop-blur text-white flex items-center justify-center hover:bg-[rgba(224,96,96,0.5)] transition-colors">
                                        <Trash2 size={20} />
                                    </button>
                                </div>
                            </div>
                            <div className="p-4">
                                <h4 className="font-bold text-[var(--t0)] text-sm truncate">{slider.title || 'Untitled'}</h4>
                                <div className="flex justify-between items-center mt-2">
                                    <StatusBadge status={slider.active ? "Active" : "Draft"} />
                                    <span className="text-[11px] font-mono text-[var(--t2)]">Order: {slider.order || 0}</span>
                                </div>
                            </div>
                        </div>
                    ))}
                </div>
            )}

            {previewImage && <ImageModal imageUrl={previewImage} onClose={() => setPreviewImage(null)} />}
            {showDeleteDialog && (
                <ConfirmDialog
                    title="Delete Slider"
                    message="Are you sure you want to delete this slider? This action cannot be undone."
                    confirmText="Delete"
                    isDestructive
                    onConfirm={handleDelete}
                    onCancel={() => setShowDeleteDialog(null)}
                />
            )}
        </div>
    );
}

// -------------------------------------------------------------------------------------------------
// Reports Table
// -------------------------------------------------------------------------------------------------
function ReportsTable() {
    const { data: items, isLoading } = useCollection("reports");
    const [showBlockDialog, setShowBlockDialog] = useState<any>(null);

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

    if (isLoading) return <div className="text-[var(--t2)] text-sm animate-pulse">Loading reports...</div>;

    if (items.length === 0) {
        return (
            <div className="py-12 flex flex-col items-center justify-center text-center border border-[var(--bd)] rounded-2xl bg-[var(--bg0)]">
                <div className="w-16 h-16 bg-[var(--bg3)] text-[var(--t2)] rounded-2xl flex items-center justify-center mb-4 font-fraunces font-bold text-xl">0</div>
                <h3 className="text-sm font-bold text-[var(--t0)]">No reports found.</h3>
            </div>
        );
    }

    return (
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
                    {items.map((row: any) => (
                        <tr key={row.id} className="border-b last:border-0 border-[var(--bd)] hover:bg-[var(--bg3)] transition-colors">
                            <td className="py-4 px-4 text-[13px] text-[var(--t0)] font-medium max-w-[200px] truncate" title={row.reason}>{row.reason || 'No reason'}</td>
                            <td className="py-4 px-4 text-[13px] text-[var(--t1)] font-mono">{row.reportedUserId || row.reportedListingId || 'Unknown'}</td>
                            <td className="py-4 px-4 text-[13px] text-[var(--t2)] font-mono">{row.reporterId}</td>
                            <td className="py-4 px-4"><StatusBadge status={row.status || 'Pending'} /></td>
                            <td className="py-4 px-4 flex justify-end gap-2">
                                {row.reportedUserId && row.status !== 'Resolved' && (
                                    <button onClick={() => setShowBlockDialog(row)} className="px-3 py-1 bg-[rgba(240,201,74,0.1)] text-[var(--acc)] font-bold text-[11px] rounded-full hover:bg-[rgba(240,201,74,0.2)] transition-colors inline-flex items-center gap-1">
                                        <Slash size={12} /> Block User & Resolve
                                    </button>
                                )}
                                {row.status !== 'Resolved' && (
                                    <button onClick={async () => await updateDoc(doc(db, "reports", row.id), { status: 'Resolved' })} className="px-3 py-1 bg-[rgba(34,197,94,0.1)] text-green-500 font-bold text-[11px] rounded-full hover:bg-[rgba(34,197,94,0.2)] transition-colors inline-flex items-center gap-1">
                                        <CheckCircle size={12} /> Resolve Only
                                    </button>
                                )}
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>

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

// -------------------------------------------------------------------------------------------------
// Feedback Table
// -------------------------------------------------------------------------------------------------
function FeedbackTable() {
    const { data: items, isLoading } = useCollection("feedback");

    if (isLoading) return <div className="text-[var(--t2)] text-sm animate-pulse">Loading feedback...</div>;

    if (items.length === 0) {
        return (
            <div className="py-12 flex flex-col items-center justify-center text-center border border-[var(--bd)] rounded-2xl bg-[var(--bg0)]">
                <div className="w-16 h-16 bg-[var(--bg3)] text-[var(--t2)] rounded-2xl flex items-center justify-center mb-4 font-fraunces font-bold text-xl">0</div>
                <h3 className="text-sm font-bold text-[var(--t0)]">No feedback found.</h3>
            </div>
        );
    }

    return (
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
    );
}
