/* eslint-disable @typescript-eslint/no-explicit-any */
"use client";

import { useState } from 'react';
import { useCollection } from '../../lib/hooks';
import { ImageModal, StatusBadge, ConfirmDialog } from '../ui/index';
import { Trash2, Eye } from 'lucide-react';
import { deleteDoc, doc } from 'firebase/firestore';
import { db } from '../../lib/firebase';
import PageLoader from '../ui/PageLoader';

export default function BannersView() {
    const { data: items, isLoading } = useCollection("sliders");
    const [previewImage, setPreviewImage] = useState<string | null>(null);
    const [showDeleteDialog, setShowDeleteDialog] = useState<any>(null);

    const handleDelete = async () => {
        if (!showDeleteDialog) return;
        try {
            await deleteDoc(doc(db, "sliders", showDeleteDialog.id));
            setShowDeleteDialog(null);
        } catch { alert("Failed to delete banner"); }
    };

    if (isLoading) return <PageLoader message="Loading banners..." />;

    return (
        <div className="h-full flex flex-col relative animate-in fade-in duration-500">
            <div className="flex items-center justify-between mb-6">
                <h2 className="text-xl font-fraunces font-bold text-[var(--t0)]">App Banners (Sliders)</h2>
            </div>

            <div className="flex-1 overflow-y-auto">
                {items.length === 0 ? (
                    <div className="py-12 flex flex-col items-center justify-center text-center border border-[var(--bd)] rounded-2xl bg-[var(--bg0)]">
                        <div className="w-16 h-16 bg-[var(--bg3)] text-[var(--t2)] rounded-2xl flex items-center justify-center mb-4 font-fraunces font-bold text-xl">0</div>
                        <h3 className="text-sm font-bold text-[var(--t0)]">No banners found.</h3>
                    </div>
                ) : (
                    <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                        {items.map((slider: any) => (
                            <div key={slider.id} className="bg-[var(--bg0)] border border-[var(--bd)] rounded-[24px] overflow-hidden group">
                                <div className="relative h-48 bg-[var(--bg4)]">
                                    {slider.imageUrl ? (
                                        /* eslint-disable-next-line @next/next/no-img-element */
                                        <img src={slider.imageUrl} alt="Banner" className="w-full h-full object-cover" />
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
                                    <h4 className="font-bold text-[var(--t0)] text-sm truncate">{slider.title || 'Untitled Banner'}</h4>
                                    <div className="flex justify-between items-center mt-2">
                                        <StatusBadge status={slider.active ? "Active" : "Draft"} />
                                        <span className="text-[11px] font-mono text-[var(--t2)]">Order: {slider.order || 0}</span>
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                )}
            </div>

            {previewImage && <ImageModal imageUrl={previewImage} onClose={() => setPreviewImage(null)} />}
            {showDeleteDialog && (
                <ConfirmDialog
                    title="Delete Banner"
                    message="Are you sure you want to delete this banner? This action cannot be undone."
                    confirmText="Delete"
                    isDestructive
                    onConfirm={handleDelete}
                    onCancel={() => setShowDeleteDialog(null)}
                />
            )}
        </div>
    );
}
