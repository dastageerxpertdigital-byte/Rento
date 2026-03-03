"use client";

import { Edit, Trash2 } from 'lucide-react';
import { StatusBadge } from '../ui/index';
import { useCollection } from '../../lib/hooks';

export default function StandardTablePage({ title }: { title: string }) {
    const collectionName = title.toLowerCase();
    const { data, isLoading, error } = useCollection(collectionName);

    if (isLoading) return <div className="p-8 text-center text-[var(--t2)] font-medium">Loading {title}...</div>;
    if (error) return <div className="p-8 text-center text-[var(--red)] font-medium">Error loading {title}: {error}</div>;
    if (!data || data.length === 0) return <div className="p-8 text-center text-[var(--t2)] font-medium">No results found for {title}.</div>;

    // Exclude ID from columns, we only display string/number primitive values for generic tables
    const columns = Object.keys(data[0]).filter(k => k !== 'id' && (typeof data[0][k] === 'string' || typeof data[0][k] === 'number')).slice(0, 8);

    return (
        <div className="bg-[var(--bg2)] border border-[var(--bd)] rounded-[24px] p-6 h-full flex flex-col">
            <div className="overflow-x-auto flex-1">
                <table className="w-full text-left border-collapse">
                    <thead>
                        <tr className="border-b border-[var(--bd)]">
                            {columns.map(col => (
                                <th key={col} className="pb-4 pt-2 text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider capitalize px-4">
                                    {col}
                                </th>
                            ))}
                            <th className="pb-4 pt-2 text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider text-right px-4">Actions</th>
                        </tr>
                    </thead>
                    <tbody className="text-sm">
                        {data.map((row, i) => (
                            <tr key={i} className="border-b border-[var(--bd)] hover:bg-[var(--bg3)] transition-colors group">
                                {columns.map(col => (
                                    <td key={col} className="py-4 px-4 text-[13px] text-[var(--t0)] font-medium">
                                        {col === 'status' ? (
                                            <StatusBadge status={row[col] as string} />
                                        ) : (
                                            row[col] as React.ReactNode
                                        )}
                                    </td>
                                ))}
                                <td className="py-4 px-4 text-right flex justify-end gap-2">
                                    <button className="p-2 bg-[var(--bg0)] rounded-lg text-[var(--pri)] hover:bg-[var(--pri-transparent)] transition-colors">
                                        <Edit size={16} />
                                    </button>
                                    <button className="p-2 bg-[var(--bg0)] rounded-lg text-[var(--red)] hover:bg-[rgba(224,96,96,0.15)] transition-colors">
                                        <Trash2 size={16} />
                                    </button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
}
