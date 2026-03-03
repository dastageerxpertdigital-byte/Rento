/* eslint-disable @typescript-eslint/no-explicit-any */
"use client";

import { useState } from 'react';
import { useCollection } from '../../lib/hooks';
import { Send, XCircle, ChevronDown, ChevronLeft, ChevronRight, Check } from 'lucide-react';
import { httpsCallable } from 'firebase/functions';
import { functions, db } from '../../lib/firebase';
import { TableSkeleton } from '../ui/PageLoader';
import { getMessaging, getToken } from 'firebase/messaging';
import { doc, getDoc } from 'firebase/firestore';

export default function NotificationsView() {
    const { data: items, isLoading } = useCollection("notifications");
    const { data: usersData } = useCollection("users");
    const [showSendModal, setShowSendModal] = useState(false);

    // Form state
    const [userId, setUserId] = useState('');
    const [selectedUserName, setSelectedUserName] = useState('');
    const [title, setTitle] = useState('');
    const [body, setBody] = useState('');
    const [broadcastTarget, setBroadcastTarget] = useState<'all' | 'free_only' | 'paid_only'>('all');
    const [sending, setSending] = useState(false);

    // Dropdown state
    const [isDropdownOpen, setIsDropdownOpen] = useState(false);
    const [userSearch, setUserSearch] = useState('');
    const [userPage, setUserPage] = useState(1);
    const USERS_PER_PAGE = 5;

    const filteredUsers = (usersData || []).filter((u: any) =>
        (u.name || u.displayName || u.userName || 'Unknown').toLowerCase().includes(userSearch.toLowerCase()) ||
        (u.email || '').toLowerCase().includes(userSearch.toLowerCase())
    );
    const paginatedUsers = filteredUsers.slice((userPage - 1) * USERS_PER_PAGE, userPage * USERS_PER_PAGE);
    const totalPages = Math.ceil(filteredUsers.length / USERS_PER_PAGE);

    const handleSend = async (e: React.FormEvent) => {
        e.preventDefault();
        setSending(true);
        try {
            if (userId) {
                // Fetch user's FCM token from Firestore
                const userDoc = await getDoc(doc(db, 'users', userId));
                const fcmToken = userDoc.data()?.fcmToken;

                if (!fcmToken) {
                    throw new Error("Target user does not have a registered push token.");
                }

                const sendNotification = httpsCallable(functions, 'sendFCMNotification');
                await sendNotification({
                    token: fcmToken,
                    title,
                    body,
                    userId
                });
            } else {
                const broadcastToAll = httpsCallable(functions, 'broadcastToAll');
                await broadcastToAll({
                    title,
                    body,
                    target: broadcastTarget
                });
            }
            setShowSendModal(false);
            setUserId('');
            setSelectedUserName('');
            setTitle('');
            setBody('');
            alert('Notification sent!');
        } catch (e: any) {
            alert('Failed to send notification: ' + e.message);
        }
        setSending(false);
    };

    if (isLoading) return <TableSkeleton rows={6} cols={5} />;

    const columns = items.length > 0 ? Object.keys(items[0]).filter(k => k !== 'id' && (typeof items[0][k] === 'string' || typeof items[0][k] === 'number')).slice(0, 5) : [];

    return (
        <div className="h-full flex flex-col relative animate-in fade-in duration-500 flex-1">
            <div className="flex items-center justify-between mb-6">
                <h2 className="text-xl font-fraunces font-bold text-[var(--t0)]">Push Notifications Log</h2>
                <button
                    onClick={() => setShowSendModal(true)}
                    className="flex items-center gap-2 card-gradient px-5 py-2 rounded-full text-sm font-bold text-white shadow-lg shadow-[var(--pri-transparent)] hover:opacity-90 transition-opacity"
                >
                    <Send size={16} /> Broadcast Message
                </button>
            </div>

            <div className="flex-1 overflow-y-auto">
                {items.length === 0 ? (
                    <div className="py-12 flex flex-col items-center justify-center text-center border border-[var(--bd)] rounded-2xl bg-[var(--bg0)]">
                        <div className="w-16 h-16 bg-[var(--bg3)] text-[var(--t2)] rounded-2xl flex items-center justify-center mb-4 font-fraunces font-bold text-xl">0</div>
                        <h3 className="text-sm font-bold text-[var(--t0)]">No notifications found.</h3>
                    </div>
                ) : (
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
                )}
            </div>

            {/* Send Modal */}
            {showSendModal && (
                <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50 backdrop-blur-sm">
                    <div className="bg-[var(--bg1)] border border-[var(--bd)] rounded-[24px] p-6 w-full max-w-md shadow-2xl relative animate-in zoom-in-95 duration-200">
                        <button onClick={() => setShowSendModal(false)} className="absolute top-4 right-4 text-[var(--t2)] hover:text-[var(--t0)]">
                            <XCircle size={24} />
                        </button>

                        <div className="flex flex-col items-center gap-3 mb-6">
                            <div className="w-12 h-12 rounded-2xl bg-[rgba(34,197,94,0.1)] text-green-500 flex items-center justify-center"><Send size={24} /></div>
                            <h2 className="text-xl font-fraunces font-bold text-[var(--t0)]">Send Push Notification</h2>
                            <p className="text-sm text-[var(--t2)] text-center">Send a manual push notification to a specific user or globally.</p>
                        </div>

                        <form onSubmit={handleSend} className="space-y-4">
                            <div className="relative">
                                <label className="block text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider mb-2">Target User (Optional)</label>
                                <div
                                    className="w-full bg-[var(--bg2)] border border-[var(--bd)] text-[var(--t0)] rounded-xl py-3 px-4 text-sm flex items-center justify-between cursor-pointer focus:border-[var(--pri)]"
                                    onClick={() => setIsDropdownOpen(!isDropdownOpen)}
                                >
                                    <span className={userId ? "text-[var(--t0)]" : "text-[var(--t2)] truncate mr-2"}>
                                        {userId ? selectedUserName : broadcastTarget === 'all' ? "All Users" : broadcastTarget === 'free_only' ? "Non-Paid Users" : "Premium Subscribers"}
                                    </span>
                                    <ChevronDown size={16} className={`transition-transform flex-shrink-0 text-[var(--t2)] ${isDropdownOpen ? 'rotate-180' : ''}`} />
                                </div>
                                {isDropdownOpen && (
                                    <div className="absolute top-full left-0 right-0 mt-2 bg-[var(--bg1)] border border-[var(--bd)] rounded-xl shadow-xl z-50 overflow-hidden flex flex-col">
                                        <div className="p-2 border-b border-[var(--bd)]">
                                            <input
                                                type="text"
                                                placeholder="Search users..."
                                                value={userSearch}
                                                onChange={(e) => { setUserSearch(e.target.value); setUserPage(1); }}
                                                className="w-full bg-[var(--bg2)] border border-[var(--bd)] text-[var(--t0)] rounded-lg py-2 px-3 text-sm focus:outline-none focus:border-[var(--pri)]"
                                                onClick={(e) => e.stopPropagation()}
                                            />
                                        </div>
                                        <div className="max-h-48 overflow-y-auto p-1">
                                            <div
                                                onClick={() => { setUserId(''); setBroadcastTarget('all'); setIsDropdownOpen(false); }}
                                                className={`px-3 py-2 rounded-lg text-sm cursor-pointer hover:bg-[var(--bg3)] flex items-center justify-between ${!userId && broadcastTarget === 'all' ? 'bg-[var(--bg3)] font-bold text-[var(--pri)]' : 'text-[var(--t0)]'}`}
                                            >
                                                <span>All Users</span>
                                                {!userId && broadcastTarget === 'all' && <Check size={16} />}
                                            </div>
                                            <div
                                                onClick={() => { setUserId(''); setBroadcastTarget('free_only'); setIsDropdownOpen(false); }}
                                                className={`px-3 py-2 rounded-lg text-sm cursor-pointer hover:bg-[var(--bg3)] flex items-center justify-between ${!userId && broadcastTarget === 'free_only' ? 'bg-[var(--bg3)] font-bold text-[var(--pri)]' : 'text-[var(--t0)]'}`}
                                            >
                                                <span>Non-Paid Users</span>
                                                {!userId && broadcastTarget === 'free_only' && <Check size={16} />}
                                            </div>
                                            <div
                                                onClick={() => { setUserId(''); setBroadcastTarget('paid_only'); setIsDropdownOpen(false); }}
                                                className={`px-3 py-2 rounded-lg text-sm cursor-pointer hover:bg-[var(--bg3)] flex items-center justify-between ${!userId && broadcastTarget === 'paid_only' ? 'bg-[var(--bg3)] font-bold text-[var(--pri)]' : 'text-[var(--t0)]'}`}
                                            >
                                                <span>Premium Subscribers</span>
                                                {!userId && broadcastTarget === 'paid_only' && <Check size={16} />}
                                            </div>
                                            {paginatedUsers.map((u: any) => {
                                                const uName = u.name || u.displayName || u.userName || 'Unknown';
                                                return (
                                                    <div
                                                        key={u.id}
                                                        onClick={() => { setUserId(u.id); setSelectedUserName(`${uName} (${u.email || ''})`); setIsDropdownOpen(false); }}
                                                        className={`px-3 py-2 rounded-lg text-sm cursor-pointer hover:bg-[var(--bg3)] flex items-center justify-between ${userId === u.id ? 'bg-[var(--bg3)] font-bold text-[var(--pri)]' : 'text-[var(--t0)]'}`}
                                                    >
                                                        <div className="flex flex-col truncate pr-2">
                                                            <span className="truncate">{uName}</span>
                                                            <span className="text-[10px] text-[var(--t2)] truncate">{u.email}</span>
                                                        </div>
                                                        {userId === u.id && <Check size={16} className="flex-shrink-0" />}
                                                    </div>
                                                );
                                            })}
                                            {filteredUsers.length === 0 && (
                                                <div className="p-3 text-center text-sm text-[var(--t2)]">No users found</div>
                                            )}
                                        </div>
                                        {totalPages > 1 && (
                                            <div className="p-2 border-t border-[var(--bd)] flex items-center justify-between bg-[var(--bg2)] mt-auto">
                                                <button
                                                    type="button"
                                                    disabled={userPage === 1}
                                                    onClick={(e) => { e.stopPropagation(); setUserPage(p => p - 1); }}
                                                    className="p-1 rounded-md text-[var(--t2)] hover:bg-[var(--bg3)] hover:text-[var(--t0)] disabled:opacity-50 transition-colors"
                                                >
                                                    <ChevronLeft size={16} />
                                                </button>
                                                <span className="text-xs font-medium text-[var(--t2)]">Page {userPage} of {totalPages}</span>
                                                <button
                                                    type="button"
                                                    disabled={userPage === totalPages}
                                                    onClick={(e) => { e.stopPropagation(); setUserPage(p => p + 1); }}
                                                    className="p-1 rounded-md text-[var(--t2)] hover:bg-[var(--bg3)] hover:text-[var(--t0)] disabled:opacity-50 transition-colors"
                                                >
                                                    <ChevronRight size={16} />
                                                </button>
                                            </div>
                                        )}
                                    </div>
                                )}
                            </div>
                            <div>
                                <label className="block text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider mb-2">Title</label>
                                <input
                                    type="text"
                                    required
                                    value={title}
                                    onChange={e => setTitle(e.target.value)}
                                    className="w-full bg-[var(--bg2)] border border-[var(--bd)] text-[var(--t0)] rounded-xl py-3 px-4 text-sm focus:outline-none focus:border-[var(--pri)] transition-colors"
                                />
                            </div>
                            <div>
                                <label className="block text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider mb-2">Message Body</label>
                                <textarea
                                    required
                                    rows={3}
                                    value={body}
                                    onChange={e => setBody(e.target.value)}
                                    className="w-full bg-[var(--bg2)] border border-[var(--bd)] text-[var(--t0)] rounded-xl py-3 px-4 text-sm focus:outline-none focus:border-[var(--pri)] resize-none transition-colors"
                                />
                            </div>

                            <div className="flex gap-3 pt-2">
                                <button type="button" onClick={() => setShowSendModal(false)} className="flex-1 px-4 py-3 rounded-full text-sm font-bold text-[var(--t1)] bg-[var(--bg2)] border border-[var(--bd)] hover:bg-[var(--bg3)] transition-colors">
                                    Cancel
                                </button>
                                <button type="submit" disabled={sending} className="flex-1 px-4 py-3 rounded-full text-sm font-bold text-white card-gradient shadow-lg shadow-[var(--pri-transparent)] transition-opacity hover:opacity-90 disabled:opacity-50">
                                    {sending ? 'Sending...' : 'Send Message'}
                                </button>
                            </div>
                        </form>
                    </div>
                </div>
            )}
        </div>
    );
}
