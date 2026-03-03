/* eslint-disable @typescript-eslint/no-explicit-any */
"use client";

import { useState, useEffect } from 'react';
import { FileTerminal, Search, X, Trash2, Key, Save } from 'lucide-react';
import { useCollection, useDocument } from '../../lib/hooks';
import { deleteDoc, doc, setDoc } from 'firebase/firestore';
import { db } from '../../lib/firebase';
import { ConfirmDialog } from '../ui/index';
import PageLoader from '../ui/PageLoader';

export default function GeminiView() {
    const { data: logs, isLoading: logsLoading } = useCollection("gemini_logs");
    const { data: config, isLoading: configLoading } = useDocument("config/admin");
    const [page, setPage] = useState(1);
    const ITEMS_PER_PAGE = 20;
    const [search, setSearch] = useState('');
    const [selectedLog, setSelectedLog] = useState<any>(null);
    const [showClearDialog, setShowClearDialog] = useState(false);
    const [isClearing, setIsClearing] = useState(false);
    const [apiKeys, setApiKeys] = useState<string[]>(['', '', '', '']);
    const [activeKeyIndex, setActiveKeyIndex] = useState(0);
    const [isSavingKey, setIsSavingKey] = useState(false);

    useEffect(() => {
        if (config) {
            setApiKeys([
                (config.geminiApiKey1 as string) || (config.geminiApiKey as string) || '',
                (config.geminiApiKey2 as string) || '',
                (config.geminiApiKey3 as string) || '',
                (config.geminiApiKey4 as string) || ''
            ]);
        }
    }, [config]);

    const handleKeyChange = (val: string) => {
        const newKeys = [...apiKeys];
        newKeys[activeKeyIndex] = val;
        setApiKeys(newKeys);
    };

    const handleSaveKey = async () => {
        setIsSavingKey(true);
        try {
            await setDoc(doc(db, "config", "admin"), {
                geminiApiKey1: apiKeys[0],
                geminiApiKey2: apiKeys[1],
                geminiApiKey3: apiKeys[2],
                geminiApiKey4: apiKeys[3],
                geminiApiKey: apiKeys[0] // Fallback
            }, { merge: true });
            alert("Gemini API Keys saved successfully.");
        } catch (e) {
            console.error(e);
            alert("Failed to save API Keys.");
        } finally {
            setIsSavingKey(false);
        }
    };

    const handleClearAll = async () => {
        setIsClearing(true);
        try {
            for (const log of logs) {
                await deleteDoc(doc(db, "gemini_logs", log.id as string));
            }
            setShowClearDialog(false);
            alert("All Gemini logs cleared successfully.");
        } catch (e) {
            console.error(e);
            alert("Failed to clear logs.");
        } finally {
            setIsClearing(false);
        }
    };

    if (logsLoading || configLoading) return <PageLoader message="Loading Gemini data..." />;

    const filteredLogs = logs.filter((log: any) => {
        return (log.id?.toLowerCase() || '').includes(search.toLowerCase()) ||
            (log.action?.toLowerCase() || '').includes(search.toLowerCase());
    });

    const paginatedLogs = filteredLogs.slice(0, page * ITEMS_PER_PAGE);

    return (
        <div className="h-full flex flex-col relative animate-in fade-in duration-500 flex-1">

            {/* Header / API Key Section */}
            <div className="p-6 border-b border-[var(--bd)] bg-[var(--bg1)]">
                <div className="flex items-center gap-3 mb-6">
                    <FileTerminal className="text-[var(--pri)]" size={28} />
                    <div>
                        <h2 className="text-xl font-fraunces font-bold text-[var(--t0)]">Gemini AI Management</h2>
                        <p className="text-[12px] text-[var(--t2)] mb-4">No Gemini configuration found in the database. Please ensure it's configured under Settings.</p>
                    </div>
                </div>

                <div className="bg-[var(--bg0)] border border-[var(--bd)] p-4 rounded-[20px] max-w-3xl flex flex-col gap-4">
                    <div className="flex items-center justify-between">
                        <label className="flex items-center gap-2 text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider">
                            <Key size={14} /> Gemini API Keys
                        </label>
                        <div className="flex bg-[var(--bg2)] border border-[var(--bd)] rounded-xl p-1 shadow-sm">
                            {[0, 1, 2, 3].map((idx) => (
                                <button
                                    key={idx}
                                    onClick={() => setActiveKeyIndex(idx)}
                                    className={`px-4 py-1.5 text-xs font-bold tracking-wide transition-all rounded-lg whitespace-nowrap ${activeKeyIndex === idx ? 'bg-[var(--bg1)] text-[var(--t0)] shadow-sm border border-[var(--bd)]' : 'text-[var(--t2)] hover:text-[var(--t0)] border border-transparent'}`}
                                >
                                    Key {idx + 1}
                                </button>
                            ))}
                        </div>
                    </div>
                    <div className="flex gap-3">
                        <input
                            type="password"
                            placeholder={`Enter Google Gemini API Key ${activeKeyIndex + 1}`}
                            value={apiKeys[activeKeyIndex]}
                            onChange={(e) => handleKeyChange(e.target.value)}
                            className="flex-1 bg-[var(--bg2)] border border-[var(--bd)] text-[var(--t0)] rounded-xl py-2.5 px-4 text-sm focus:outline-none focus:border-[var(--pri)] transition-colors"
                        />
                        <button
                            onClick={handleSaveKey}
                            disabled={isSavingKey}
                            className="flex items-center gap-2 card-gradient px-5 py-2.5 rounded-xl text-sm font-bold text-white shadow-lg shadow-[var(--pri-transparent)] hover:opacity-90 transition-opacity disabled:opacity-50"
                        >
                            <Save size={16} /> {isSavingKey ? "Saving..." : "Save All Keys"}
                        </button>
                    </div>
                    <p className="text-xs text-[var(--t2)]">Provide multiple keys to mitigate Google API's rate limits across your Cloud Functions.</p>
                </div>
            </div>

            {/* Logs Section */}
            <div className="p-6 flex items-center justify-between">
                <h3 className="text-lg font-fraunces font-bold text-[var(--t0)]">Audit Logs</h3>
                <div className="flex items-center gap-3">
                    <div className="relative">
                        <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-[var(--t2)] w-4 h-4" />
                        <input
                            type="text"
                            placeholder="Search logs..."
                            value={search}
                            onChange={e => setSearch(e.target.value)}
                            className="pl-9 pr-4 py-2 border border-[var(--bd)] rounded-full text-sm bg-[var(--bg0)] focus:outline-none focus:border-[var(--pri)] text-[var(--t0)] w-60"
                        />
                    </div>
                    {logs.length > 0 && (
                        <button
                            onClick={() => setShowClearDialog(true)}
                            className="px-4 py-2 bg-[rgba(224,96,96,0.1)] text-[var(--red)] border border-[rgba(224,96,96,0.2)] rounded-full text-sm font-bold hover:bg-[rgba(224,96,96,0.2)] transition-colors flex items-center gap-2"
                        >
                            <Trash2 size={16} /> Clear All
                        </button>
                    )}
                </div>
            </div>

            {/* Table */}
            <div className="overflow-x-auto flex-1 px-6 pb-6">
                <table className="w-full text-left border-collapse">
                    <thead>
                        <tr className="border-b border-[var(--bd)] bg-[var(--bg1)]">
                            <th className="pb-3 pt-3 text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider px-4">Log ID</th>
                            <th className="pb-3 pt-3 text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider px-4">Entity type</th>
                            <th className="pb-3 pt-3 text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider px-4">Timestamp</th>
                            <th className="pb-3 pt-3 text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider px-4">Action Taken</th>
                            <th className="pb-3 pt-3 text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider text-right px-4">Details</th>
                        </tr>
                    </thead>
                    <tbody className="text-sm">
                        {paginatedLogs.map((log: any) => (
                            <tr key={log.id} className="border-b border-[var(--bd)] hover:bg-[var(--bg3)] transition-colors">
                                <td className="py-4 px-4 text-[13px] font-mono text-[var(--t1)]">{log.id.substring(0, 10)}...</td>
                                <td className="py-4 px-4 text-[13px] font-bold text-[var(--t0)] capitalize">{log.entityType || 'Unknown'}</td>
                                <td className="py-4 px-4 text-[13px] text-[var(--t2)] font-mono">
                                    {log.timestamp ? new Date(log.timestamp.seconds * 1000).toLocaleString() : 'N/A'}
                                </td>
                                <td className="py-4 px-4">
                                    <span className={`px-2 py-1 text-[11px] font-bold rounded-md ${log.action === 'approved' ? 'bg-[rgba(34,197,94,0.1)] text-green-500' :
                                        log.action === 'rejected' ? 'bg-[rgba(239,68,68,0.1)] text-red-500' :
                                            'bg-[var(--bg3)] text-[var(--t1)]'
                                        }`}>
                                        {((log.action || 'Unknown') as string).toUpperCase()}
                                    </span>
                                </td>
                                <td className="py-4 px-4 flex justify-end">
                                    <button
                                        onClick={() => setSelectedLog(log)}
                                        className="text-[12px] font-bold text-[var(--pri)] hover:underline"
                                    >
                                        View Payload
                                    </button>
                                </td>
                            </tr>
                        ))}
                        {paginatedLogs.length === 0 && (
                            <tr><td colSpan={5} className="py-8 text-center text-[var(--t2)] text-sm">No logs found matching criteria.</td></tr>
                        )}
                    </tbody>
                </table>
                {paginatedLogs.length < filteredLogs.length && (
                    <div className="text-center py-6 border-t border-[var(--bd)]">
                        <button onClick={() => setPage(p => p + 1)} className="px-6 py-2 border border-[var(--bd)] rounded-full text-sm font-bold text-[var(--t1)] hover:bg-[var(--bg3)] transition-colors">
                            Load More
                        </button>
                    </div>
                )}
            </div>

            {/* Modal for Payload XML/JSON */}
            {selectedLog && (
                <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
                    <div className="absolute inset-0 bg-black/60 backdrop-blur-sm transition-opacity" onClick={() => setSelectedLog(null)}></div>
                    <div className="relative bg-[var(--bg1)] border border-[var(--bd)] flex flex-col rounded-[24px] max-w-2xl w-full h-[80vh] shadow-2xl z-10 animate-in fade-in zoom-in-95 duration-200">
                        <div className="flex items-center justify-between p-6 border-b border-[var(--bd)] bg-[var(--bg2)] rounded-t-[24px]">
                            <div>
                                <h3 className="text-xl font-fraunces font-bold text-[var(--t0)]">Payload Details</h3>
                                <p className="text-[11px] font-mono text-[var(--t2)] mt-1">{selectedLog.id}</p>
                            </div>
                            <button onClick={() => setSelectedLog(null)} className="text-[var(--t2)] hover:text-[var(--t0)]"><X size={24} /></button>
                        </div>
                        <div className="p-6 flex-1 overflow-auto bg-[#1e1e1e] text-[#d4d4d4] font-mono text-sm leading-relaxed rounded-b-[24px]">
                            <pre className="whitespace-pre-wrap">
                                {JSON.stringify(selectedLog, null, 2)}
                            </pre>
                        </div>
                    </div>
                </div>
            )}

            {showClearDialog && (
                <ConfirmDialog
                    title="Clear All Logs"
                    message="Are you sure you want to permanently delete ALL Gemini AI logs? This cannot be undone."
                    confirmText={isClearing ? "Clearing..." : "Yes, Clear All"}
                    isDestructive
                    onConfirm={handleClearAll}
                    onCancel={() => !isClearing && setShowClearDialog(false)}
                />
            )}
        </div>
    );
}
