/* eslint-disable @typescript-eslint/no-explicit-any */
/* eslint-disable @typescript-eslint/no-unused-vars */
import { useState, useMemo } from 'react';
import { Eye, CheckCircle, XCircle, Search, Filter, ChevronDown } from 'lucide-react';
import { StatusBadge, ConfirmDialog } from '../ui/index';
import { useCollection } from '../../lib/hooks';
import { updateDoc, doc } from 'firebase/firestore';
import { db } from '../../lib/firebase';
import PageLoader, { TableSkeleton } from '../ui/PageLoader';

export default function RequestsView() {
    const { data: requests, isLoading } = useCollection("requests");

    // Pagination & Filter state
    const [page, setPage] = useState(1);
    const ITEMS_PER_PAGE = 20;
    const [searchQuery, setSearchQuery] = useState('');
    const [statusFilter, setStatusFilter] = useState('All');

    // Actions state
    const [selectedRequest, setSelectedRequest] = useState<any>(null);
    const [showDetailPanel, setShowDetailPanel] = useState(false);
    const [showCloseDialog, setShowCloseDialog] = useState(false);

    const filteredRequests = useMemo(() => {
        return requests.filter((r: any) => {
            const searchStr = `${r.title || ''} ${r.id || ''}`.toLowerCase();
            const matchesSearch = searchStr.includes(searchQuery.toLowerCase());
            const matchesStatus = statusFilter === 'All' || (r.status || 'Pending') === statusFilter;
            return matchesSearch && matchesStatus;
        });
    }, [requests, searchQuery, statusFilter]);

    if (isLoading) return <TableSkeleton rows={8} cols={5} />;

    const paginatedRequests = filteredRequests.slice(0, page * ITEMS_PER_PAGE);

    const handleClose = async () => {
        if (!selectedRequest) return;
        try {
            await updateDoc(doc(db, "requests", selectedRequest.id), { status: 'Closed' });
            setShowCloseDialog(false);
        } catch (e) {
            console.error(e);
            alert("Failed to close request");
        }
    };

    return (
        <div className="h-full flex flex-col relative animate-in fade-in duration-500">
            {/* Header / Toolbar */}
            <div className="flex flex-col md:flex-row md:items-center justify-between mb-6 gap-4">
                <div className="flex flex-col sm:flex-row items-center gap-3 w-full md:w-auto">
                    <div className="relative w-full sm:w-auto">
                        <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-[var(--t2)]" size={16} />
                        <input
                            type="text"
                            placeholder="Search requests..."
                            value={searchQuery}
                            onChange={(e) => { setSearchQuery(e.target.value); setPage(1); }}
                            className="w-full sm:w-64 pl-9 pr-4 py-2 border border-[var(--bd)] rounded-full text-sm bg-[var(--bg0)] focus:outline-none focus:border-[var(--pri)] text-[var(--t0)] transition-colors"
                        />
                    </div>
                    <div className="relative w-full sm:w-auto">
                        <Filter className="absolute left-3 top-1/2 -translate-y-1/2 text-[var(--t2)]" size={16} />
                        <select
                            value={statusFilter}
                            onChange={(e) => { setStatusFilter(e.target.value); setPage(1); }}
                            className="w-full sm:w-40 appearance-none pl-9 pr-8 py-2 border border-[var(--bd)] rounded-full text-sm bg-[var(--bg0)] focus:outline-none focus:border-[var(--pri)] text-[var(--t0)] cursor-pointer transition-colors"
                        >
                            <option value="All">All Status</option>
                            <option value="Pending">Pending</option>
                            <option value="Fulfilled">Fulfilled</option>
                            <option value="Closed">Closed</option>
                        </select>
                        <ChevronDown className="absolute right-3 top-1/2 -translate-y-1/2 text-[var(--t2)] pointer-events-none" size={16} />
                    </div>
                </div>
                <div className="text-[13px] font-bold text-[var(--t1)] bg-[var(--bg1)] px-4 py-2 rounded-full border border-[var(--bd)] self-start md:self-auto shadow-sm">
                    Total: {filteredRequests.length}
                </div>
            </div>

            {/* Table */}
            <div className="overflow-x-auto flex-1 pb-4">
                <table className="w-full text-left border-collapse">
                    <thead>
                        <tr className="border-b border-[var(--bd)]">
                            <th className="pb-4 pt-2 text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider px-4">Title</th>
                            <th className="pb-4 pt-2 text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider px-4">Budget</th>
                            <th className="pb-4 pt-2 text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider px-4">Status</th>
                            <th className="pb-4 pt-2 text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider text-right px-4">Actions</th>
                        </tr>
                    </thead>
                    <tbody className="text-sm">
                        {paginatedRequests.map((row: any, index: number) => (
                            <tr key={row.id} style={{ animationDelay: `${(index % ITEMS_PER_PAGE) * 30}ms`, animationFillMode: 'both' }} className="border-b border-[var(--bd)] hover:bg-[var(--bg3)] transition-colors group animate-in fade-in slide-in-from-bottom-2 duration-300">
                                <td className="py-4 px-4 text-[14px] font-bold text-[var(--t0)]">
                                    <span className="group-hover:text-[var(--pri)] transition-colors">{row.title || 'Untitled Request'}</span>
                                    <div className="text-[11px] font-medium text-[var(--t2)] font-mono mt-0.5">{row.id}</div>
                                </td>
                                <td className="py-4 px-4 text-[13px] font-medium text-[var(--t1)]">PKR {row.budget || 0}</td>
                                <td className="py-4 px-4"><StatusBadge status={row.status || 'Pending'} /></td>
                                <td className="py-4 px-4 flex justify-end gap-2 text-right w-full sm:w-auto overflow-x-auto">
                                    <button onClick={() => { setSelectedRequest(row); setShowDetailPanel(true); }} className="p-2 bg-[var(--bg1)] border border-[var(--bd)] rounded-xl text-[var(--pri)] hover:bg-[var(--pri-transparent)] transition-colors tooltip relative shadow-sm">
                                        <Eye size={16} />
                                    </button>
                                    <button onClick={() => { setSelectedRequest(row); setShowCloseDialog(true); }} className="p-2 bg-[var(--bg1)] border border-[var(--bd)] rounded-xl text-[var(--red)] hover:bg-[rgba(224,96,96,0.15)] transition-colors shadow-sm">
                                        <XCircle size={16} />
                                    </button>
                                </td>
                            </tr>
                        ))}
                        {paginatedRequests.length === 0 && (
                            <tr><td colSpan={4} className="py-12 text-center text-[var(--t2)] text-sm font-bold">No requests found.</td></tr>
                        )}
                    </tbody>
                </table>
                {paginatedRequests.length < filteredRequests.length && (
                    <div className="text-center py-6">
                        <button onClick={() => setPage(p => p + 1)} className="px-8 py-2.5 border border-[var(--bd)] rounded-full text-sm font-bold text-[var(--t1)] bg-[var(--bg1)] hover:bg-[var(--bg3)] transition-colors shadow-sm">
                            Load More Results
                        </button>
                    </div>
                )}
            </div>

            {/* Detail Slide-over Panel */}
            {showDetailPanel && selectedRequest && (
                <div className="absolute top-0 right-0 w-full sm:w-[400px] h-full bg-[var(--bg1)] border-l border-[var(--bd)] shadow-2xl z-20 flex flex-col transform transition-transform animate-in slide-in-from-right duration-300">
                    <div className="p-6 border-b border-[var(--bd)] flex items-center justify-between bg-[var(--bg2)]">
                        <h2 className="text-xl font-fraunces font-bold text-[var(--t0)]">Request Details</h2>
                        <button onClick={() => setShowDetailPanel(false)} className="text-[var(--t2)] hover:text-[var(--t0)]"><XCircle size={24} /></button>
                    </div>
                    <div className="p-6 flex-1 overflow-y-auto space-y-6">
                        <div>
                            <p className="text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider mb-1">Title</p>
                            <p className="text-sm font-medium text-[var(--t0)]">{selectedRequest.title}</p>
                        </div>
                        <div>
                            <p className="text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider mb-1">Description</p>
                            <p className="text-sm text-[var(--t1)] whitespace-pre-wrap">{selectedRequest.description || "No description provided."}</p>
                        </div>
                        <div className="grid grid-cols-2 gap-4">
                            <div>
                                <p className="text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider mb-1">Budget</p>
                                <p className="text-sm font-medium text-[var(--t0)]">PKR {selectedRequest.budget}</p>
                            </div>
                            <div>
                                <p className="text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider mb-1">Status</p>
                                <StatusBadge status={selectedRequest.status || "Pending"} />
                            </div>
                        </div>
                        <div>
                            <p className="text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider mb-1">Requested By</p>
                            <p className="text-sm font-medium text-[var(--t0)] font-mono">{selectedRequest.requestedBy}</p>
                        </div>
                    </div>
                    <div className="p-6 border-t border-[var(--bd)] bg-[var(--bg0)] flex justify-end gap-3">
                        <button onClick={() => setShowDetailPanel(false)} className="px-5 py-2 rounded-full text-sm font-bold text-[var(--t1)] hover:bg-[var(--bg3)]">Close</button>
                    </div>
                </div>
            )}

            {/* Dialogs */}
            {showCloseDialog && (
                <ConfirmDialog
                    title="Close Request"
                    message="Are you sure you want to administratively close this request?"
                    confirmText="Close Request"
                    onConfirm={handleClose}
                    onCancel={() => setShowCloseDialog(false)}
                />
            )}
        </div>
    );
}
