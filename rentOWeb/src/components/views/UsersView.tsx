/* eslint-disable @typescript-eslint/no-explicit-any */
/* eslint-disable @typescript-eslint/no-unused-vars */
import { useState, useMemo } from 'react';
import { Eye, Slash, Trash2, ArrowUpCircle, XCircle, Search, Filter, ChevronDown } from 'lucide-react';
import { StatusBadge, ConfirmDialog, InputReasonDialog } from '../ui/index';
import { useCollection } from '../../lib/hooks';
import { updateDoc, doc } from 'firebase/firestore';
import { db } from '../../lib/firebase';
import PageLoader, { TableSkeleton } from '../ui/PageLoader';
import { deleteUserAccount } from '../../lib/cloudFunctions';

export default function UsersView() {
    const { data: users, isLoading } = useCollection("users");

    // Pagination & Filter state
    const [page, setPage] = useState(1);
    const ITEMS_PER_PAGE = 20;
    const [searchQuery, setSearchQuery] = useState('');
    const [statusFilter, setStatusFilter] = useState('All');

    // Actions state
    const [selectedUser, setSelectedUser] = useState<any>(null);
    const [showDetailPanel, setShowDetailPanel] = useState(false);
    const [showBlockDialog, setShowBlockDialog] = useState(false);
    const [showDeleteDialog, setShowDeleteDialog] = useState(false);
    const [isDeleting, setIsDeleting] = useState(false);

    const filteredUsers = useMemo(() => {
        return users.filter((u: any) => {
            const searchStr = `${u.name || ''} ${u.displayName || ''} ${u.userName || ''} ${u.email || ''}`.toLowerCase();
            const matchesSearch = searchStr.includes(searchQuery.toLowerCase());
            const matchesStatus = statusFilter === 'All' || (u.status || 'Active') === statusFilter;
            return matchesSearch && matchesStatus;
        });
    }, [users, searchQuery, statusFilter]);

    if (isLoading) return <TableSkeleton rows={8} cols={5} />;

    const paginatedUsers = filteredUsers.slice(0, page * ITEMS_PER_PAGE);

    const handleBlock = async (reason: string) => {
        if (!selectedUser) return;
        try {
            const newStatus = selectedUser.status === 'Blocked' ? 'Active' : 'Blocked';
            await updateDoc(doc(db, "users", selectedUser.id), {
                status: newStatus,
                blockReason: newStatus === 'Blocked' ? reason : null
            });
            setShowBlockDialog(false);
        } catch (e) {
            console.error(e);
            alert("Failed to change user status");
        }
    };

    const handleDelete = async () => {
        if (!selectedUser) return;
        setIsDeleting(true);
        try {
            await deleteUserAccount(selectedUser.id);
            alert("User deleted successfully.");
            setShowDeleteDialog(false);
            setShowDetailPanel(false);
        } catch (e) {
            console.error(e);
            alert("Failed to delete user account");
        } finally {
            setIsDeleting(false);
        }
    };

    const handleUpgrade = async (userId: string) => {
        try {
            await updateDoc(doc(db, "users", userId), { packageId: 'premium', subscriptionStatus: 'Active' });
            alert("User upgraded to Premium");
        } catch (e) {
            console.error(e);
            alert("Failed to upgrade user");
        }
    };

    return (
        <div className="h-full flex flex-col relative animate-in fade-in duration-500">
            {/* Header / Toolbar */}
            <div className="flex flex-col md:flex-row md:items-center justify-between mb-6 gap-4 border-b border-[var(--bd)] pb-6">
                <div>
                    <h2 className="text-xl font-fraunces font-bold text-[var(--t0)]">Registered Users</h2>
                    <p className="text-[12px] text-[var(--t2)] mt-1">Manage users, their subscriptions, and system access.</p>
                </div>
                <div className="flex flex-col sm:flex-row items-center gap-3">
                    <div className="relative w-full sm:w-auto">
                        <Search className="absolute left-3 top-1/2 -translate-y-1/2 text-[var(--t2)]" size={16} />
                        <input
                            type="text"
                            placeholder="Search users..."
                            value={searchQuery}
                            onChange={(e) => { setSearchQuery(e.target.value); setPage(1); }}
                            className="w-full sm:w-56 pl-9 pr-4 py-2 border border-[var(--bd)] rounded-full text-sm bg-[var(--bg0)] focus:outline-none focus:border-[var(--pri)] text-[var(--t0)] transition-colors"
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
                            <option value="Active">Active</option>
                            <option value="Blocked">Blocked</option>
                        </select>
                        <ChevronDown className="absolute right-3 top-1/2 -translate-y-1/2 text-[var(--t2)] pointer-events-none" size={16} />
                    </div>
                </div>
            </div>

            {/* Table */}
            <div className="overflow-x-auto flex-1 pb-4">
                <table className="w-full text-left border-collapse">
                    <thead>
                        <tr className="border-b border-[var(--bd)]">
                            <th className="pb-4 pt-2 text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider px-4">User</th>
                            <th className="pb-4 pt-2 text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider px-4">Package</th>
                            <th className="pb-4 pt-2 text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider px-4">Status</th>
                            <th className="pb-4 pt-2 text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider text-right px-4">Actions</th>
                        </tr>
                    </thead>
                    <tbody className="text-sm">
                        {paginatedUsers.map((row: any, index: number) => {
                            const displayName = row.userName || row.name || row.displayName || 'Anonymous User';
                            const initial = displayName !== 'Anonymous User' ? displayName.substring(0, 2).toUpperCase() : 'U';
                            return (
                                <tr
                                    key={row.id}
                                    style={{ animationDelay: `${(index % ITEMS_PER_PAGE) * 30}ms`, animationFillMode: 'both' }}
                                    className="border-b border-[var(--bd)] hover:bg-[var(--bg3)] transition-colors group animate-in fade-in slide-in-from-bottom-2 duration-300"
                                >
                                    <td className="py-4 px-4">
                                        <div className="flex items-center gap-3">
                                            <div className="w-8 h-8 rounded-full bg-[var(--bg4)] flex items-center justify-center font-bold text-xs overflow-hidden border border-[var(--bd)]">
                                                {row.photoURL ? (
                                                    /* eslint-disable-next-line @next/next/no-img-element */
                                                    <img src={row.photoURL} alt="Avatar" className="w-full h-full object-cover" />
                                                ) : (
                                                    <span className="text-[var(--t1)]">{initial}</span>
                                                )}
                                            </div>
                                            <div>
                                                <div className="text-[14px] font-bold text-[var(--t0)] group-hover:text-[var(--pri)] transition-colors">{displayName}</div>
                                                <div className="text-[11px] font-medium text-[var(--t2)]">{row.email || 'No email provided'}</div>
                                            </div>
                                        </div>
                                    </td>
                                    <td className="py-4 px-4 text-[13px] font-medium text-[var(--t1)] capitalize">{row.packageId || 'Free'}</td>
                                    <td className="py-4 px-4"><StatusBadge status={row.status || 'Active'} /></td>
                                    <td className="py-4 px-4 flex justify-end gap-2">
                                        <button onClick={() => handleUpgrade(row.id)} className="p-2 bg-[var(--bg1)] border border-[var(--bd)] rounded-xl text-[var(--pri2)] hover:bg-[var(--pri-transparent)] transition-colors tooltip relative shadow-sm">
                                            <ArrowUpCircle size={16} />
                                        </button>
                                        <button onClick={() => { setSelectedUser(row); setShowDetailPanel(true); }} className="p-2 bg-[var(--bg1)] border border-[var(--bd)] rounded-xl text-[var(--pri)] hover:bg-[var(--pri-transparent)] transition-colors tooltip relative shadow-sm">
                                            <Eye size={16} />
                                        </button>
                                        <button onClick={() => { setSelectedUser(row); setShowBlockDialog(true); }} className="p-2 bg-[var(--bg1)] border border-[var(--bd)] rounded-xl text-[var(--acc)] hover:bg-[rgba(240,201,74,0.15)] transition-colors shadow-sm">
                                            <Slash size={16} />
                                        </button>
                                        <button onClick={() => { setSelectedUser(row); setShowDeleteDialog(true); }} className="p-2 bg-[var(--bg1)] border border-[var(--bd)] rounded-xl text-[var(--red)] hover:bg-[rgba(224,96,96,0.15)] transition-colors shadow-sm">
                                            <Trash2 size={16} />
                                        </button>
                                    </td>
                                </tr>
                            );
                        })}
                        {paginatedUsers.length === 0 && (
                            <tr><td colSpan={4} className="py-12 text-center text-[var(--t2)] text-sm font-bold">No users match your criteria.</td></tr>
                        )}
                    </tbody>
                </table>
                {paginatedUsers.length < filteredUsers.length && (
                    <div className="text-center py-6">
                        <button onClick={() => setPage(p => p + 1)} className="px-8 py-2.5 border border-[var(--bd)] rounded-full text-sm font-bold text-[var(--t1)] bg-[var(--bg1)] hover:bg-[var(--bg3)] transition-colors shadow-sm">
                            Load More Results
                        </button>
                    </div>
                )}
            </div>

            {/* Detail Slide-over Panel */}
            {showDetailPanel && selectedUser && (() => {
                const selectedDisplayName = selectedUser.userName || selectedUser.name || selectedUser.displayName || 'Anonymous User';
                const selectedInitial = selectedDisplayName !== 'Anonymous User' ? selectedDisplayName.substring(0, 2).toUpperCase() : 'U';
                return (
                    <div className="absolute top-0 right-0 w-full sm:w-[400px] h-full bg-[var(--bg1)] border-l border-[var(--bd)] shadow-2xl z-20 flex flex-col transform transition-transform animate-in slide-in-from-right duration-300">
                        <div className="p-6 border-b border-[var(--bd)] flex items-center justify-between bg-[var(--bg2)]">
                            <h2 className="text-xl font-fraunces font-bold text-[var(--t0)]">User Profile</h2>
                            <button onClick={() => setShowDetailPanel(false)} className="text-[var(--t2)] hover:text-[var(--t0)]"><XCircle size={24} className="lucide-react" /></button>
                        </div>
                        <div className="p-6 flex-1 overflow-y-auto space-y-6">
                            <div className="flex flex-col items-center">
                                <div className="w-24 h-24 rounded-full bg-[var(--bg4)] flex items-center justify-center overflow-hidden mb-4 border-4 border-[var(--bd)]">
                                    {selectedUser.photoURL ? (
                                        /* eslint-disable-next-line @next/next/no-img-element */
                                        <img src={selectedUser.photoURL} alt="Avatar" className="w-full h-full object-cover" />
                                    ) : (
                                        <span className="text-2xl font-bold text-[var(--t1)]">{selectedInitial}</span>
                                    )}
                                </div>
                                <p className="text-xl font-fraunces font-bold text-[var(--t0)]">{selectedDisplayName}</p>
                                <p className="text-sm text-[var(--t1)] font-mono">{selectedUser.email}</p>
                                <div className="mt-2"><StatusBadge status={selectedUser.status || "Active"} /></div>
                            </div>

                            <div className="grid grid-cols-2 gap-4">
                                <div className="bg-[var(--bg0)] p-4 rounded-xl border border-[var(--bd)] text-center">
                                    <p className="text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider mb-1">Package</p>
                                    <p className="text-lg font-bold text-[var(--t0)] capitalize">{selectedUser.packageId || 'Free'}</p>
                                </div>
                                <div className="bg-[var(--bg0)] p-4 rounded-xl border border-[var(--bd)] text-center">
                                    <p className="text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider mb-1">Listings</p>
                                    <p className="text-lg font-bold text-[var(--t0)]">{selectedUser.stats?.listingCount || 0}</p>
                                </div>
                            </div>

                            <div className="space-y-3">
                                <div>
                                    <p className="text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider mb-1">User ID</p>
                                    <p className="text-sm font-medium text-[var(--t0)] font-mono">{selectedUser.id}</p>
                                </div>
                                <div>
                                    <p className="text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider mb-1">Joined</p>
                                    <p className="text-sm font-medium text-[var(--t0)]">
                                        {selectedUser.createdAt ? new Date(selectedUser.createdAt.seconds * 1000).toLocaleDateString() : 'Unknown'}
                                    </p>
                                </div>
                            </div>

                        </div>
                        <div className="p-6 border-t border-[var(--bd)] bg-[var(--bg0)] flex justify-end gap-3">
                            <button onClick={() => setShowDetailPanel(false)} className="px-5 py-2 rounded-full text-sm font-bold text-[var(--t1)] hover:bg-[var(--bg3)]">Close</button>
                        </div>
                    </div>
                );
            })()}
            {/* Dialogs */}
            {showBlockDialog && (
                <InputReasonDialog
                    title={selectedUser?.status === 'Blocked' ? "Unblock User" : "Block User"}
                    message={selectedUser?.status === 'Blocked' ? "Confirm unblocking this user?" : "Please provide a valid reason for blocking this user."}
                    placeholder="Violation of terms..."
                    onConfirm={handleBlock}
                    onCancel={() => setShowBlockDialog(false)}
                />
            )}

            {showDeleteDialog && (
                <ConfirmDialog
                    title="Delete Account"
                    message="Are you sure you want to completely destroy this user account and all references? THIS CANNOT BE UNDONE."
                    isDestructive
                    confirmText={isDeleting ? "Deleting..." : "Destroy Account"}
                    onConfirm={handleDelete}
                    onCancel={() => !isDeleting && setShowDeleteDialog(false)}
                />
            )}
        </div>
    );
}
