/* eslint-disable @typescript-eslint/no-explicit-any */
"use client";

import { useState, useEffect, useRef } from 'react';
import { useDocument } from '../../lib/hooks';
import { doc, setDoc } from 'firebase/firestore';
import { db } from '../../lib/firebase';
import { Save, Plus, Trash2, X, Edit3, Eye, ChevronLeft, ChevronRight, Crown, Sparkles, Zap } from 'lucide-react';
import { ToggleSwitch, ConfirmDialog } from '../ui/index';
import PageLoader from '../ui/PageLoader';

const PACKAGE_ICONS: Record<string, React.ReactNode> = {
    free: <Zap size={28} />,
    premium: <Crown size={28} />,
    business: <Sparkles size={28} />,
};

const PACKAGE_COLORS: Record<string, string> = {
    free: 'from-emerald-400 to-teal-500',
    premium: 'from-amber-400 to-orange-500',
    business: 'from-violet-400 to-purple-600',
};

export default function PackagesView() {
    const { data: config, isLoading } = useDocument('system_config/packages');
    const [saving, setSaving] = useState(false);
    const [showDeleteDialog, setShowDeleteDialog] = useState<string | null>(null);
    const [showAddForm, setShowAddForm] = useState(false);
    const [editMode, setEditMode] = useState(false);
    const [activeIndex, setActiveIndex] = useState(0);
    const scrollRef = useRef<HTMLDivElement>(null);

    // Add form state
    const [newPkgId, setNewPkgId] = useState('');
    const [newPkgPrice, setNewPkgPrice] = useState(0);
    const [newPkgDuration, setNewPkgDuration] = useState(30);
    const [newPkgMaxListings, setNewPkgMaxListings] = useState(5);

    const [packages, setPackages] = useState<Record<string, any>>({});

    useEffect(() => {
        if (config && Object.keys(config).length > 0) {
            // Filter out Firestore metadata fields
            const filtered: Record<string, any> = {};
            Object.keys(config).forEach(key => {
                if (typeof config[key] === 'object' && config[key] !== null && 'price' in config[key]) {
                    filtered[key] = config[key];
                }
            });
            if (Object.keys(filtered).length > 0) {
                setPackages(filtered);
            }
        }
    }, [config]);

    const packageKeys = Object.keys(packages);

    const handleChange = (pkgKey: string, field: string, value: any) => {
        setPackages(prev => ({
            ...prev,
            [pkgKey]: {
                ...prev[pkgKey],
                [field]: value
            }
        }));
    };

    const handleDelete = async () => {
        if (!showDeleteDialog) return;
        const updated = { ...packages };
        delete updated[showDeleteDialog];
        setPackages(updated);
        setShowDeleteDialog(null);
        // Auto-save to Firestore after delete
        try {
            await setDoc(doc(db, 'system_config/packages'), updated);
        } catch (e) {
            console.error(e);
            alert("Failed to delete package from database.");
        }
    };

    const handleAdd = async () => {
        const id = newPkgId.trim().toLowerCase().replace(/[^a-z0-9_]/g, '');
        if (!id) return alert('Invalid package ID');
        if (packages[id]) return alert('Package ID already exists');

        const updated = {
            ...packages,
            [id]: { price: newPkgPrice, durationDays: newPkgDuration, maxListings: newPkgMaxListings, active: true }
        };

        setPackages(updated);
        setNewPkgId('');
        setNewPkgPrice(0);
        setNewPkgDuration(30);
        setNewPkgMaxListings(5);
        setShowAddForm(false);

        // Auto-save to Firestore
        try {
            await setDoc(doc(db, 'system_config/packages'), updated);
        } catch (e) {
            console.error(e);
            alert("Failed to save new package to database.");
        }
    };

    const handleSave = async () => {
        setSaving(true);
        try {
            await setDoc(doc(db, 'system_config/packages'), packages);
            alert("Packages saved successfully.");
            setEditMode(false);
        } catch (e) {
            console.error(e);
            alert("Failed to save packages.");
        } finally {
            setSaving(false);
        }
    };

    const scrollToCard = (index: number) => {
        const clamped = Math.max(0, Math.min(index, packageKeys.length - 1));
        setActiveIndex(clamped);
        if (scrollRef.current) {
            const cards = scrollRef.current.children;
            if (cards[clamped]) {
                (cards[clamped] as HTMLElement).scrollIntoView({
                    behavior: 'smooth',
                    block: 'nearest',
                    inline: 'center'
                });
            }
        }
    };

    if (isLoading) return <PageLoader message="Loading packages..." />;

    return (
        <div className="h-full flex flex-col relative animate-in fade-in duration-500">
            {/* Header */}
            <div className="flex justify-end items-center mb-6">
                <div className="flex items-center gap-3">
                    <button
                        onClick={() => setEditMode(!editMode)}
                        className={`px-5 py-2.5 rounded-full text-sm font-bold transition-all flex items-center gap-2 border ${editMode
                            ? 'bg-[var(--pri-transparent)] text-[var(--pri)] border-[var(--pri)]'
                            : 'bg-[var(--bg2)] text-[var(--t1)] border-[var(--bd)] hover:bg-[var(--bg3)]'
                            }`}
                    >
                        {editMode ? <><Eye size={16} /> View Mode</> : <><Edit3 size={16} /> Edit Mode</>}
                    </button>
                    {editMode && (
                        <>
                            <button
                                onClick={() => setShowAddForm(true)}
                                className="px-5 py-2.5 rounded-full text-sm font-bold text-white card-gradient shadow-lg shadow-[var(--pri-transparent)] hover:opacity-90 transition-opacity flex items-center gap-2"
                            >
                                <Plus size={16} /> Add Plan
                            </button>
                            <button
                                disabled={saving}
                                onClick={handleSave}
                                className="px-5 py-2.5 rounded-full text-sm font-bold text-[var(--t1)] bg-[var(--bg2)] border border-[var(--bd)] hover:bg-[var(--bg3)] transition-colors disabled:opacity-50 flex items-center gap-2"
                            >
                                <Save size={16} /> {saving ? 'Saving...' : 'Save All'}
                            </button>
                        </>
                    )}
                </div>
            </div>

            {/* Cards Carousel */}
            <div className="flex-1 flex flex-col items-center justify-center overflow-hidden">
                {packageKeys.length === 0 ? (
                    <div className="flex flex-col items-center justify-center text-center py-16 px-8">
                        <div className="w-20 h-20 bg-[var(--bg3)] text-[var(--t2)] rounded-3xl flex items-center justify-center mb-6">
                            <Sparkles size={32} />
                        </div>
                        <h3 className="text-lg font-bold text-[var(--t0)] mb-2">No packages configured</h3>
                        <p className="text-sm text-[var(--t2)] mb-6 max-w-sm">Add your first subscription plan to get started. Switch to Edit Mode to create packages.</p>
                    </div>
                ) : (
                    <div
                        ref={scrollRef}
                        className="flex items-center gap-8 px-8 py-12 overflow-x-auto overflow-y-visible scroll-smooth snap-x snap-mandatory w-full justify-center"
                        style={{ scrollbarWidth: 'none' }}
                    >
                        {packageKeys.map((pkgKey, index) => {
                            const pkg = packages[pkgKey];
                            const isFocused = index === activeIndex;
                            const gradientClass = PACKAGE_COLORS[pkgKey] || 'from-cyan-400 to-blue-500';
                            const icon = PACKAGE_ICONS[pkgKey] || <Sparkles size={28} />;

                            return (
                                <div
                                    key={pkgKey}
                                    onClick={() => setActiveIndex(index)}
                                    className={`snap-center flex-shrink-0 transition-all duration-500 cursor-pointer ${isFocused
                                        ? 'w-[300px] scale-100 opacity-100'
                                        : 'w-[250px] scale-90 opacity-60 hover:opacity-80'
                                        }`}
                                >
                                    <div className={`bg-[var(--bg1)] border rounded-[28px] overflow-hidden transition-all duration-500 ${isFocused
                                        ? 'border-[var(--pri)] shadow-2xl shadow-[var(--pri-transparent)]'
                                        : 'border-[var(--bd)]'
                                        } ${!pkg.active && editMode ? 'grayscale' : ''}`}
                                    >
                                        {/* Card Header with Gradient */}
                                        <div className={`relative bg-gradient-to-br ${gradientClass} p-4 pb-4`}>
                                            <div className="flex items-center justify-between mb-4">
                                                <div className="w-12 h-12 rounded-2xl bg-white/20 backdrop-blur flex items-center justify-center text-white">
                                                    {icon}
                                                </div>
                                                {editMode && pkgKey !== 'free' && (
                                                    <button
                                                        onClick={(e) => { e.stopPropagation(); setShowDeleteDialog(pkgKey); }}
                                                        className="w-9 h-9 rounded-full bg-white/20 backdrop-blur text-white flex items-center justify-center hover:bg-white/30 transition-colors"
                                                    >
                                                        <Trash2 size={16} />
                                                    </button>
                                                )}
                                            </div>
                                            <h3 className="text-xl font-bold text-white capitalize tracking-wide">{pkgKey}</h3>
                                            <div className="flex items-baseline gap-1 mt-2">
                                                <span className="text-3xl font-fraunces font-bold text-white">
                                                    {pkg.price === 0 ? 'Free' : `PKR ${pkg.price?.toLocaleString()}`}
                                                </span>
                                                {pkg.price > 0 && (
                                                    <span className="text-white/70 text-sm font-medium">
                                                        / {pkg.durationDays || 30} days
                                                    </span>
                                                )}
                                            </div>
                                        </div>

                                        {/* Card Body */}
                                        <div className="p-4 space-y-3">
                                            {editMode ? (
                                                <>
                                                    <div className="flex items-center justify-between mb-2">
                                                        <span className="text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider">Active</span>
                                                        <ToggleSwitch checked={pkg.active ?? true} onChange={(v) => handleChange(pkgKey, 'active', v)} />
                                                    </div>
                                                    <div className="space-y-1.5">
                                                        <label className="text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider">Price (PKR)</label>
                                                        <input
                                                            type="number"
                                                            className="w-full bg-[var(--bg0)] border border-[var(--bd)] text-[var(--t0)] rounded-xl py-2 px-3 text-sm focus:outline-none focus:border-[var(--pri)] disabled:opacity-50 transition-colors"
                                                            value={pkg.price || 0}
                                                            onChange={e => handleChange(pkgKey, 'price', parseInt(e.target.value) || 0)}
                                                            disabled={pkgKey === 'free'}
                                                        />
                                                    </div>
                                                    <div className="space-y-1.5">
                                                        <label className="text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider">Duration (Days)</label>
                                                        <input
                                                            type="number"
                                                            className="w-full bg-[var(--bg0)] border border-[var(--bd)] text-[var(--t0)] rounded-xl py-2 px-3 text-sm focus:outline-none focus:border-[var(--pri)] disabled:opacity-50 transition-colors"
                                                            value={pkg.durationDays || 0}
                                                            onChange={e => handleChange(pkgKey, 'durationDays', parseInt(e.target.value) || 0)}
                                                            disabled={pkgKey === 'free'}
                                                        />
                                                    </div>
                                                    <div className="space-y-1.5">
                                                        <label className="text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider">Max Listings</label>
                                                        <input
                                                            type="number"
                                                            className="w-full bg-[var(--bg0)] border border-[var(--bd)] text-[var(--t0)] rounded-xl py-2 px-3 text-sm focus:outline-none focus:border-[var(--pri)] transition-colors"
                                                            value={pkg.maxListings || 0}
                                                            onChange={e => handleChange(pkgKey, 'maxListings', parseInt(e.target.value) || 0)}
                                                        />
                                                    </div>
                                                </>
                                            ) : (
                                                <div className="space-y-4">
                                                    <div className="flex items-center gap-3 py-3 border-b border-[var(--bd)]">
                                                        <div className="w-8 h-8 rounded-lg bg-[var(--pri-transparent)] text-[var(--pri)] flex items-center justify-center text-sm font-bold">
                                                            {pkg.maxListings || '∞'}
                                                        </div>
                                                        <div>
                                                            <p className="text-sm font-bold text-[var(--t0)]">Max Listings</p>
                                                            <p className="text-[11px] text-[var(--t2)]">Active listings allowed</p>
                                                        </div>
                                                    </div>
                                                    <div className="flex items-center gap-3 py-3 border-b border-[var(--bd)]">
                                                        <div className="w-8 h-8 rounded-lg bg-[rgba(90,159,212,0.15)] text-[var(--blue)] flex items-center justify-center text-sm font-bold">
                                                            {pkg.durationDays || '∞'}
                                                        </div>
                                                        <div>
                                                            <p className="text-sm font-bold text-[var(--t0)]">Duration</p>
                                                            <p className="text-[11px] text-[var(--t2)]">{pkg.durationDays ? `${pkg.durationDays} day subscription` : 'Unlimited'}</p>
                                                        </div>
                                                    </div>
                                                    <div className="flex items-center gap-3 py-3">
                                                        <div className={`w-8 h-8 rounded-lg flex items-center justify-center text-sm font-bold ${pkg.active !== false ? 'bg-[var(--pri-transparent)] text-[var(--pri)]' : 'bg-[rgba(224,96,96,0.15)] text-[var(--red)]'}`}>
                                                            {pkg.active !== false ? '✓' : '✗'}
                                                        </div>
                                                        <div>
                                                            <p className="text-sm font-bold text-[var(--t0)]">Status</p>
                                                            <p className="text-[11px] text-[var(--t2)]">{pkg.active !== false ? 'Available for purchase' : 'Currently disabled'}</p>
                                                        </div>
                                                    </div>
                                                </div>
                                            )}
                                        </div>
                                    </div>
                                </div>
                            );
                        })}
                    </div>
                )}
            </div>

            {/* Carousel Navigation */}
            {packageKeys.length > 1 && (
                <div className="flex items-center justify-center gap-4 pb-6 pt-2">
                    <button
                        onClick={() => scrollToCard(activeIndex - 1)}
                        disabled={activeIndex === 0}
                        className="w-10 h-10 rounded-full bg-[var(--bg2)] border border-[var(--bd)] flex items-center justify-center text-[var(--t1)] hover:bg-[var(--bg3)] disabled:opacity-30 transition-all"
                    >
                        <ChevronLeft size={20} />
                    </button>
                    <div className="flex gap-2">
                        {packageKeys.map((_, idx) => (
                            <button
                                key={idx}
                                onClick={() => scrollToCard(idx)}
                                className={`w-2.5 h-2.5 rounded-full transition-all duration-300 ${idx === activeIndex ? 'bg-[var(--pri)] w-8' : 'bg-[var(--bg4)] hover:bg-[var(--t2)]'}`}
                            />
                        ))}
                    </div>
                    <button
                        onClick={() => scrollToCard(activeIndex + 1)}
                        disabled={activeIndex === packageKeys.length - 1}
                        className="w-10 h-10 rounded-full bg-[var(--bg2)] border border-[var(--bd)] flex items-center justify-center text-[var(--t1)] hover:bg-[var(--bg3)] disabled:opacity-30 transition-all"
                    >
                        <ChevronRight size={20} />
                    </button>
                </div>
            )}

            {/* Add Package Modal */}
            {showAddForm && (
                <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
                    <div className="absolute inset-0 bg-black/60 backdrop-blur-sm transition-opacity" onClick={() => setShowAddForm(false)}></div>
                    <div className="relative bg-[var(--nav)] backdrop-blur-2xl border border-[var(--bd)] flex flex-col rounded-[24px] max-w-md w-full shadow-2xl z-10 animate-in fade-in zoom-in-95 duration-200">
                        <div className="flex items-center justify-between p-6 border-b border-[var(--bd)]">
                            <h3 className="text-xl font-fraunces font-bold text-[var(--t0)]">Add New Plan</h3>
                            <button onClick={() => setShowAddForm(false)} className="text-[var(--t2)] hover:text-[var(--t0)] transition-colors">
                                <X size={24} />
                            </button>
                        </div>
                        <div className="p-6 space-y-5">
                            <div className="space-y-1.5">
                                <label className="text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider ml-1">Plan Name</label>
                                <input
                                    type="text"
                                    className="w-full bg-[var(--bg2)] border border-[var(--bd)] text-[var(--t0)] rounded-xl py-3 px-4 text-sm focus:outline-none focus:border-[var(--pri)] transition-colors"
                                    value={newPkgId}
                                    onChange={e => setNewPkgId(e.target.value)}
                                    placeholder="e.g. enterprise"
                                />
                            </div>
                            <div className="space-y-1.5">
                                <label className="text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider ml-1">Price (PKR)</label>
                                <input
                                    type="number"
                                    className="w-full bg-[var(--bg2)] border border-[var(--bd)] text-[var(--t0)] rounded-xl py-3 px-4 text-sm focus:outline-none focus:border-[var(--pri)] transition-colors"
                                    value={newPkgPrice}
                                    onChange={e => setNewPkgPrice(parseInt(e.target.value) || 0)}
                                    placeholder="0"
                                />
                            </div>
                            <div className="grid grid-cols-2 gap-4">
                                <div className="space-y-1.5">
                                    <label className="text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider ml-1">Duration (Days)</label>
                                    <input
                                        type="number"
                                        className="w-full bg-[var(--bg2)] border border-[var(--bd)] text-[var(--t0)] rounded-xl py-3 px-4 text-sm focus:outline-none focus:border-[var(--pri)] transition-colors"
                                        value={newPkgDuration}
                                        onChange={e => setNewPkgDuration(parseInt(e.target.value) || 0)}
                                        placeholder="30"
                                    />
                                </div>
                                <div className="space-y-1.5">
                                    <label className="text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider ml-1">Max Listings</label>
                                    <input
                                        type="number"
                                        className="w-full bg-[var(--bg2)] border border-[var(--bd)] text-[var(--t0)] rounded-xl py-3 px-4 text-sm focus:outline-none focus:border-[var(--pri)] transition-colors"
                                        value={newPkgMaxListings}
                                        onChange={e => setNewPkgMaxListings(parseInt(e.target.value) || 0)}
                                        placeholder="10"
                                    />
                                </div>
                            </div>
                            <button onClick={handleAdd} className="w-full mt-4 py-3 rounded-xl card-gradient text-white text-sm font-bold shadow-lg shadow-[var(--pri-transparent)] hover:opacity-90 transition-opacity">
                                Create Plan
                            </button>
                        </div>
                    </div>
                </div>
            )
            }

            {
                showDeleteDialog && (
                    <ConfirmDialog
                        title={`Delete "${showDeleteDialog}" Plan`}
                        message="Are you sure you want to delete this plan? Existing users on this plan may be affected. This will save to the database immediately."
                        confirmText="Delete Plan"
                        isDestructive
                        onConfirm={handleDelete}
                        onCancel={() => setShowDeleteDialog(null)}
                    />
                )
            }
        </div >
    );
}
