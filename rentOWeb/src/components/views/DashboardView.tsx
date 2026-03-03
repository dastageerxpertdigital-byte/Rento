import { useMemo } from 'react';
import { Users, Building, Search, Package } from 'lucide-react';
import { KPICard, StatusBar, DashboardTableRow } from '../ui/index';
import { useCollectionCount, useCollection } from '../../lib/hooks';
import { where, orderBy, limit } from 'firebase/firestore';

export default function DashboardView() {
    const totalUsers = useCollectionCount("users");
    const totalListings = useCollectionCount("listings");
    const totalRequests = useCollectionCount("requests");
    const totalPackages = useCollectionCount("packages");
    const pendingRequests = useCollectionCount("requests", [where("status", "==", "Pending")]);
    const activeSubs = useCollectionCount("subscriptions", [where("status", "==", "Active")]);

    const publishedListings = useCollectionCount("listings", [where("status", "==", "Published")]);
    const pendingListings = useCollectionCount("listings", [where("status", "==", "Pending")]);
    const draftListings = useCollectionCount("listings", [where("status", "==", "Draft")]);
    const blockedListings = useCollectionCount("listings", [where("status", "==", "Blocked")]);

    // Safety fallback for heights
    const maxListings = Math.max(1, publishedListings, pendingListings, draftListings, blockedListings);
    const getHeight = (val: number) => `${Math.max(5, (val / maxListings) * 100)}%`;

    const approvalRate = totalListings > 0 ? Math.round((publishedListings / totalListings) * 100) : 0;
    const mrr = activeSubs * 2500; // Mock calculation, in real app would sum actual active subscription amounts

    // Calculate distributions for mini charts
    const freeUsers = Math.max(0, totalUsers - activeSubs);
    const conversionRate = totalUsers > 0 ? Math.round((activeSubs / totalUsers) * 100) : 0;

    const { data: listings } = useCollection("listings");
    const avgPrice = useMemo(() => {
        if (!listings || listings.length === 0) return 0;
        const total = listings.reduce((acc: number, curr: any) => acc + (curr.pricePerDay || 0), 0);
        return Math.round(total / listings.length);
    }, [listings]);

    const { data: expiringSubs } = useCollection("subscriptions", [where("status", "==", "Active"), orderBy("expiryDate", "asc"), limit(5)]);
    const { data: latestUsersData } = useCollection("users", [orderBy("createdAt", "desc"), limit(4)]);

    return (
        <div className="grid grid-cols-12 gap-6 relative">
            {/* TOP KPI CARDS */}
            <div className="col-span-12 grid grid-cols-2 md:grid-cols-4 gap-6">
                <KPICard
                    title="Total Users"
                    value={totalUsers.toLocaleString()}
                    icon={<Users size={20} className="text-[var(--blue)]" />}
                    sparkline={[20, 40, 30, 50, 40, 60, 80]}
                />
                <KPICard
                    title="Total Listings"
                    value={totalListings.toLocaleString()}
                    icon={<Building size={20} className="text-[var(--pri)]" />}
                />
                <KPICard
                    title="Pending Requests"
                    value={pendingRequests.toLocaleString()}
                    icon={<Search size={20} className="text-[var(--red)]" />}
                />
                <KPICard
                    title="Active Subs"
                    value={activeSubs.toLocaleString()}
                    trend={conversionRate > 0 ? `${conversionRate}% conv.` : undefined}
                    icon={<Package size={20} className="text-[var(--acc)]" />}
                />
            </div>

            {/* MIDDLE ROW */}
            <div className="col-span-12 lg:col-span-8">
                <div className="h-full flex flex-col relative">
                    <div className="flex justify-between items-start mb-4 relative z-10">
                        <div>
                            <h3 className="text-sm font-bold text-[var(--t2)] uppercase tracking-wider mb-1">Monthly Recurring Revenue</h3>
                            <div className="flex items-end gap-3">
                                <h2 className="text-3xl font-fraunces font-bold text-[var(--t0)]">PKR {mrr.toLocaleString()}</h2>
                                <span className="flex items-center gap-1 text-xs font-bold text-[var(--t2)] bg-[var(--bg3)] px-2 py-1 rounded-md mb-1">
                                    Est.
                                </span>
                            </div>
                        </div>
                        <span className="text-[11px] font-bold text-[var(--t2)] bg-[var(--bg3)] px-3 py-1 rounded-full">This Month</span>
                    </div>

                    {/* Custom SVG Line Chart */}
                    <div className="flex-1 mt-4 relative w-full h-48">
                        <svg viewBox="0 0 800 200" className="w-full h-full overflow-visible preserve-3d">
                            <defs>
                                <linearGradient id="lineGrad" x1="0" y1="0" x2="0" y2="1">
                                    <stop offset="0%" stopColor="var(--pri)" stopOpacity="0.4" />
                                    <stop offset="100%" stopColor="var(--pri)" stopOpacity="0.0" />
                                </linearGradient>
                            </defs>
                            {/* Grid Lines */}
                            <path d="M0,150 L800,150" stroke="var(--bd)" strokeWidth="1" strokeDasharray="4 4" />
                            <path d="M0,100 L800,100" stroke="var(--bd)" strokeWidth="1" strokeDasharray="4 4" />
                            <path d="M0,50 L800,50" stroke="var(--bd)" strokeWidth="1" strokeDasharray="4 4" />

                            {mrr > 0 ? (
                                <>
                                    {/* Mock Prediction Line */}
                                    <path d="M0,120 Q 100,140 200,110 T 400,130 T 600,90 T 800,140" fill="none" stroke="var(--bd2)" strokeWidth="2" strokeDasharray="6 6" />
                                    {/* Area Fill */}
                                    <path d="M0,100 Q 100,120 200,70 T 400,80 T 500,20 T 600,50 T 800,40 L800,200 L0,200 Z" fill="url(#lineGrad)" />
                                    {/* Main Line */}
                                    <path d="M0,100 Q 100,120 200,70 T 400,80 T 500,20 T 600,50 T 800,40" fill="none" stroke="var(--pri)" strokeWidth="3" strokeLinecap="round" />

                                    <circle cx="500" cy="20" r="5" fill="var(--bg1)" stroke="var(--pri)" strokeWidth="3" />
                                    <line x1="500" y1="20" x2="500" y2="200" stroke="var(--pri)" strokeWidth="1.5" strokeDasharray="4 4" />
                                </>
                            ) : (
                                /* Flat Baseline when no data */
                                <path d="M0,180 L800,180" stroke="var(--pri)" strokeWidth="2" strokeOpacity="0.3" strokeLinecap="round" />
                            )}
                        </svg>

                        {mrr === 0 ? (
                            <div className="absolute inset-0 flex flex-col items-center justify-center text-center pb-8">
                                <p className="text-[10px] font-bold text-[var(--t2)] uppercase tracking-[0.2em] opacity-60">No revenue data yet</p>
                                <p className="text-[9px] text-[var(--t2)] max-w-[180px] mt-1 opacity-40">Tracking will begin after first subscription.</p>
                            </div>
                        ) : (
                            <div className="absolute top-[-10px] left-[520px] bg-[var(--bg1)] border border-[var(--bd)] shadow-xl p-3 rounded-xl z-20">
                                <p className="text-[10px] font-bold text-[var(--t2)] mb-1">Today</p>
                                <p className="text-sm font-bold text-[var(--pri)] flex items-center gap-2">
                                    <span className="w-2 h-0.5 bg-[var(--pri)] rounded"></span> {mrr.toLocaleString()} <span className="font-normal text-[var(--t1)] text-xs">est.</span>
                                </p>
                            </div>
                        )}

                        <div className="absolute -bottom-6 left-0 right-0 flex justify-between text-[11px] font-medium text-[var(--t2)] px-2">
                            <span>Wk 1</span><span>Wk 2</span><span>Wk 3</span><span>Wk 4</span><span>Current</span>
                        </div>
                    </div>

                    <div className="mt-8 flex gap-6 pt-4 border-t border-[var(--bd)]">
                        <div className="flex items-center gap-3">
                            <div className="p-2 bg-[var(--pri-transparent)] rounded-lg text-[var(--pri)]"><Building size={16} /></div>
                            <div>
                                <p className="text-lg font-bold text-[var(--t0)]">{totalListings.toLocaleString()}</p>
                                <p className="text-[11px] font-medium text-[var(--t2)]">Listings</p>
                            </div>
                        </div>
                        <div className="w-px h-10 bg-[var(--bd)]"></div>
                        <div className="flex items-center gap-3">
                            <div className="p-2 bg-[rgba(90,159,212,0.15)] rounded-lg text-[var(--blue)]"><Search size={16} /></div>
                            <div>
                                <p className="text-lg font-bold text-[var(--t0)]">{totalRequests.toLocaleString()}</p>
                                <p className="text-[11px] font-medium text-[var(--t2)]">Requests</p>
                            </div>
                        </div>
                        <div className="w-px h-10 bg-[var(--bd)]"></div>
                        <div className="flex items-center gap-3">
                            <div className="p-2 bg-[rgba(240,201,74,0.15)] rounded-lg text-[var(--acc)]"><Package size={16} /></div>
                            <div>
                                <p className="text-lg font-bold text-[var(--t0)]">{totalPackages.toLocaleString()}</p>
                                <p className="text-[11px] font-medium text-[var(--t2)]">Packages</p>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            <div className="col-span-12 lg:col-span-4 flex flex-col gap-6">
                <div className="flex-1 flex flex-col bg-[var(--bg1)] border border-[var(--bd)] rounded-[24px] p-6">
                    <div className="flex justify-between items-start mb-6">
                        <h3 className="text-lg font-fraunces font-semibold text-[var(--t0)]">System Activity</h3>
                        <span className="text-[10px] font-bold text-green-500 bg-green-500/10 px-2 py-1 rounded-md flex items-center gap-1">
                            <span className="w-1.5 h-1.5 rounded-full bg-green-500 animate-pulse"></span> Optimal
                        </span>
                    </div>

                    <div className="space-y-6">
                        <div>
                            <div className="flex justify-between text-[11px] font-bold text-[var(--t2)] uppercase mb-2">
                                <span>Published Listings</span>
                                <span>{publishedListings}</span>
                            </div>
                            <div className="h-2 bg-[var(--bg3)] rounded-full overflow-hidden">
                                <div className="h-full bg-[var(--pri)] rounded-full transition-all duration-1000" style={{ width: `${(publishedListings / Math.max(1, totalListings)) * 100}%` }}></div>
                            </div>
                        </div>
                        <div>
                            <div className="flex justify-between text-[11px] font-bold text-[var(--t2)] uppercase mb-2">
                                <span>Pending Approval</span>
                                <span>{pendingListings}</span>
                            </div>
                            <div className="h-2 bg-[var(--bg3)] rounded-full overflow-hidden">
                                <div className="h-full bg-[var(--acc)] rounded-full transition-all duration-1000" style={{ width: `${(pendingListings / Math.max(1, totalListings)) * 100}%` }}></div>
                            </div>
                        </div>
                        <div>
                            <div className="flex justify-between text-[11px] font-bold text-[var(--t2)] uppercase mb-2">
                                <span>Active Paid Subs</span>
                                <span>{activeSubs}</span>
                            </div>
                            <div className="h-2 bg-[var(--bg3)] rounded-full overflow-hidden">
                                <div className="h-full bg-[var(--blue)] rounded-full transition-all duration-1000" style={{ width: `${(activeSubs / Math.max(1, totalUsers)) * 100}%` }}></div>
                            </div>
                        </div>
                    </div>

                    <div className="mt-8 pt-6 border-t border-[var(--bd)]">
                        <p className="text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider mb-4">Package Conversion</p>
                        <div className="flex items-center gap-4">
                            <div className="flex-1">
                                <div className="flex justify-between text-xs mb-1">
                                    <span className="text-[var(--t1)]">Premium</span>
                                    <span className="text-[var(--t0)] font-bold">{conversionRate}%</span>
                                </div>
                                <div className="h-1 bg-[var(--bg3)] rounded-full">
                                    <div className="h-full bg-[var(--pri)] rounded-full" style={{ width: `${conversionRate}%` }}></div>
                                </div>
                            </div>
                            <div className="flex-1">
                                <div className="flex justify-between text-xs mb-1">
                                    <span className="text-[var(--t1)]">Free</span>
                                    <span className="text-[var(--t0)] font-bold">{100 - conversionRate}%</span>
                                </div>
                                <div className="h-1 bg-[var(--bg3)] rounded-full">
                                    <div className="h-full bg-[var(--t2)] rounded-full" style={{ width: `${100 - conversionRate}%` }}></div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>

            {/* BOTTOM ROW */}
            <div className="col-span-12 lg:col-span-8">
                <div className="bg-[var(--bg1)] border border-[var(--bd)] rounded-[24px] p-6">
                    <div className="flex justify-between items-center mb-6">
                        <h3 className="text-lg font-fraunces font-semibold text-[var(--t0)]">Expiring Subscriptions</h3>
                        <span className="text-[11px] font-bold text-[var(--t2)] bg-[var(--bg3)] px-3 py-1 rounded-full">Next 30 days</span>
                    </div>
                    <div className="overflow-x-auto">
                        <table className="w-full text-left border-collapse">
                            <thead>
                                <tr className="border-b border-[var(--bd)]">
                                    <th className="pb-3 text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider px-2">ID</th>
                                    <th className="pb-3 text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider px-2">User</th>
                                    <th className="pb-3 text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider text-right">Package</th>
                                    <th className="pb-3 text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider text-right">Expires In</th>
                                    <th className="pb-3 text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider text-right px-2">Action</th>
                                </tr>
                            </thead>
                            <tbody className="text-sm">
                                {expiringSubs && expiringSubs.length > 0 ? expiringSubs.map((sub: any) => (
                                    <DashboardTableRow
                                        key={sub.id}
                                        id={sub.id.substring(0, 6)}
                                        name={sub.userName || "Unknown"}
                                        icon={sub.userName ? sub.userName[0].toUpperCase() : "U"}
                                        pkg={sub.packageName || "N/A"}
                                        expiry={sub.expiryDate ? `${Math.ceil((new Date(sub.expiryDate.toDate ? sub.expiryDate.toDate() : sub.expiryDate).getTime() - Date.now()) / (1000 * 3600 * 24))} Days` : "N/A"}
                                    />
                                )) : (
                                    <tr>
                                        <td colSpan={5} className="py-8 text-center text-[var(--t2)]">No subs expiring soon</td>
                                    </tr>
                                )}
                            </tbody>
                        </table>
                    </div>
                </div>
            </div>

            <div className="col-span-12 lg:col-span-4 flex flex-col gap-6">
                <div className="flex-1 flex flex-col bg-[var(--bg1)] border border-[var(--bd)] rounded-[24px] p-6">
                    <div className="mt-6 p-4 rounded-3xl bg-[var(--bg0)] border border-[var(--bd)] border-dashed border-2 flex flex-col items-center justify-center text-center">
                        <p className="text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider mb-2">Avg. Listing Price</p>
                        <h2 className="text-3xl font-fraunces font-bold text-[var(--pri)] mb-1">PKR {avgPrice.toLocaleString()}</h2>
                        <p className="text-[10px] text-[var(--t2)] italic text-center">per day average across <span className="text-[var(--t1)] font-bold">{totalListings}</span> listings</p>
                    </div>

                    <div className="mt-8">
                        <p className="text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider mb-4 px-1">Latest Signups</p>
                        <div className="space-y-3">
                            {latestUsersData && latestUsersData.map((u: any) => (
                                <div key={u.id} className="flex items-center justify-between p-2 rounded-xl bg-[var(--bg0)] border border-[var(--bd)] hover:bg-[var(--bg3)] transition-colors">
                                    <div className="flex items-center gap-3">
                                        <div className="w-8 h-8 rounded-full bg-[var(--bg4)] flex items-center justify-center font-bold text-[var(--t2)] text-xs font-mono">
                                            {u.displayName ? u.displayName[0].toUpperCase() : (u.name ? u.name[0] : (u.userName ? u.userName[0] : 'U'))}
                                        </div>
                                        <div className="flex flex-col">
                                            <span className="text-xs font-bold text-[var(--t0)] truncate max-w-[120px]">
                                                {u.displayName || u.name || u.userName || 'Member'}
                                            </span>
                                            <span className="text-[9px] text-[var(--t2)] line-clamp-1">{u.email || 'N/A'}</span>
                                        </div>
                                    </div>
                                    <div className="text-[9px] text-[var(--t1)] font-medium bg-[var(--bg4)] px-2 py-1 rounded-lg shrink-0 ml-1">Recently</div>
                                </div>
                            ))}
                        </div>
                    </div>
                </div>
            </div>
        </div>
    );
}
