/* eslint-disable @typescript-eslint/no-explicit-any */
"use client";

import { useEffect, useState } from 'react';
import { ToggleSwitch, StatusBadge } from '../ui/index';
import { useDocument } from '../../lib/hooks';
import { doc, setDoc } from 'firebase/firestore';
import { db } from '../../lib/firebase';
import { Save } from 'lucide-react';

export default function SettingsView() {
    const { data: config } = useDocument('config/admin');
    const [saving, setSaving] = useState(false);

    const [formData, setFormData] = useState({
        platformName: 'RentO',
        supportEmail: 'admin@rento.pk',
        whatsapp: '+92 300 1234567',
        expiryDays: 3,
        requireListingApproval: true,
        requireRequestApproval: false,
        bankName: '',
        accountTitle: '',
        accountNumber: '',
        iban: '',
        minAppVersionAndroid: '1.0.0',
        latestAppVersionAndroid: '1.0.0',
        googleMapsApiKey: ''
    });

    useEffect(() => {
        if (config) {
            const data = config as Record<string, any>;
            setFormData({
                platformName: data.platformName || 'RentO',
                supportEmail: data.supportEmail || 'admin@rento.pk',
                whatsapp: data.whatsapp || '+92 300 1234567',
                expiryDays: data.expiryDays || 3,
                requireListingApproval: data.requireListingApproval ?? true,
                requireRequestApproval: data.requireRequestApproval ?? false,
                bankName: data.bankName || '',
                accountTitle: data.accountTitle || '',
                accountNumber: data.accountNumber || '',
                iban: data.iban || '',
                minAppVersionAndroid: data.minAppVersionAndroid || '1.0.0',
                latestAppVersionAndroid: data.latestAppVersionAndroid || '1.0.0',
                googleMapsApiKey: data.googleMapsApiKey || ''
            });
        }
    }, [config]);

    const handleSave = async () => {
        setSaving(true);
        try {
            await setDoc(doc(db, 'config/admin'), formData, { merge: true });
            alert("Configurations saved successfully!");
        } catch (e) {
            console.error("Failed to save configuration:", e);
            alert("Failed to save configurations.");
        } finally {
            setSaving(false);
        }
    };

    return (
        <div className="grid grid-cols-12 gap-6 pb-20">
            <div className="col-span-12 lg:col-span-8 flex flex-col gap-6">
                <div className="rounded-[24px] py-4">
                    <h3 className="text-xl font-fraunces font-semibold text-[var(--t0)] mb-6 border-b border-[var(--bd)] pb-4">Platform Configuration</h3>

                    <div className="space-y-6">
                        <div className="grid grid-cols-2 gap-6">
                            <div>
                                <label className="block text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider mb-2">Platform Name</label>
                                <input type="text" value={formData.platformName} onChange={e => setFormData({ ...formData, platformName: e.target.value })} className="w-full bg-[var(--bg0)] border border-[var(--bd)] text-[var(--t0)] rounded-xl py-3 px-4 text-sm focus:outline-none focus:border-[var(--pri)]" />
                            </div>
                            <div>
                                <label className="block text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider mb-2">Admin Support Email</label>
                                <input type="email" value={formData.supportEmail} onChange={e => setFormData({ ...formData, supportEmail: e.target.value })} className="w-full bg-[var(--bg0)] border border-[var(--bd)] text-[var(--t0)] rounded-xl py-3 px-4 text-sm focus:outline-none focus:border-[var(--pri)]" />
                            </div>
                        </div>

                        <div className="grid grid-cols-2 gap-6">
                            <div>
                                <label className="block text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider mb-2">WhatsApp Number (Payments)</label>
                                <input type="text" value={formData.whatsapp} onChange={e => setFormData({ ...formData, whatsapp: e.target.value })} className="w-full bg-[var(--bg0)] border border-[var(--bd)] text-[var(--t0)] rounded-xl py-3 px-4 text-sm focus:outline-none focus:border-[var(--pri)]" />
                            </div>
                            <div>
                                <label className="block text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider mb-2">Expiry Warning Days</label>
                                <input type="number" value={formData.expiryDays} onChange={e => setFormData({ ...formData, expiryDays: parseInt(e.target.value) || 0 })} className="w-full bg-[var(--bg0)] border border-[var(--bd)] text-[var(--t0)] rounded-xl py-3 px-4 text-sm focus:outline-none focus:border-[var(--pri)]" />
                            </div>
                        </div>

                        <h3 className="text-lg font-fraunces font-semibold text-[var(--t0)] mt-10 mb-4">Approval Settings</h3>

                        <div className="flex items-center justify-between p-4 bg-[var(--bg0)] border border-[var(--bd)] rounded-xl">
                            <div>
                                <p className="text-[14px] font-bold text-[var(--t0)]">Require Listing Approval</p>
                                <p className="text-[12px] text-[var(--t2)] mt-1">New properties must be reviewed by admin before going live.</p>
                            </div>
                            <ToggleSwitch checked={formData.requireListingApproval} onChange={val => setFormData({ ...formData, requireListingApproval: val })} />
                        </div>

                        <div className="flex items-center justify-between p-4 bg-[var(--bg0)] border border-[var(--bd)] rounded-xl">
                            <div>
                                <p className="text-[14px] font-bold text-[var(--t0)]">Require Request Approval</p>
                                <p className="text-[12px] text-[var(--t2)] mt-1">Tenant looking requests must be reviewed before appearing to hosts.</p>
                            </div>
                            <ToggleSwitch checked={formData.requireRequestApproval} onChange={val => setFormData({ ...formData, requireRequestApproval: val })} />
                        </div>
                    </div>
                </div>

                <div className="rounded-[24px] py-4">
                    <h3 className="text-xl font-fraunces font-semibold text-[var(--t0)] mb-6 border-b border-[var(--bd)] pb-4">Bank Account Details (Manual Payments)</h3>
                    <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
                        <div>
                            <label className="block text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider mb-2">Bank Name</label>
                            <input type="text" placeholder="e.g. Meezan Bank" value={formData.bankName} onChange={e => setFormData({ ...formData, bankName: e.target.value })} className="w-full bg-[var(--bg0)] border border-[var(--bd)] text-[var(--t0)] rounded-xl py-3 px-4 text-sm focus:outline-none focus:border-[var(--pri)]" />
                        </div>
                        <div>
                            <label className="block text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider mb-2">Account Title</label>
                            <input type="text" placeholder="e.g. RentO Official" value={formData.accountTitle} onChange={e => setFormData({ ...formData, accountTitle: e.target.value })} className="w-full bg-[var(--bg0)] border border-[var(--bd)] text-[var(--t0)] rounded-xl py-3 px-4 text-sm focus:outline-none focus:border-[var(--pri)]" />
                        </div>
                        <div>
                            <label className="block text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider mb-2">Account Number</label>
                            <input type="text" placeholder="0123456789" value={formData.accountNumber} onChange={e => setFormData({ ...formData, accountNumber: e.target.value })} className="w-full bg-[var(--bg0)] border border-[var(--bd)] text-[var(--t0)] rounded-xl py-3 px-4 text-sm focus:outline-none focus:border-[var(--pri)]" />
                        </div>
                        <div>
                            <label className="block text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider mb-2">IBAN</label>
                            <input type="text" placeholder="PK00MEZN000000000000" value={formData.iban} onChange={e => setFormData({ ...formData, iban: e.target.value })} className="w-full bg-[var(--bg0)] border border-[var(--bd)] text-[var(--t0)] rounded-xl py-3 px-4 text-sm focus:outline-none focus:border-[var(--pri)]" />
                        </div>
                    </div>
                </div>

                <div className="flex justify-end sticky bottom-6 z-10 hidden lg:flex">
                    <button onClick={handleSave} disabled={saving} className="flex items-center gap-2 card-gradient px-8 py-3 rounded-full text-sm font-bold text-white shadow-xl shadow-[var(--pri-transparent)] hover:opacity-90 transition-opacity disabled:opacity-50">
                        <Save size={16} /> {saving ? "Saving..." : "Save All Configurations"}
                    </button>
                </div>
            </div>

            <div className="col-span-12 lg:col-span-4 flex flex-col gap-6">
                <div className="rounded-[24px] py-4">
                    <h3 className="text-lg font-fraunces font-semibold text-[var(--t0)] mb-4">App Version Gates</h3>

                    <div className="space-y-4">
                        <div className="p-4 border border-[var(--bd)] rounded-xl bg-[var(--bg0)]">
                            <h4 className="font-bold text-[13px] text-[var(--t0)] mb-3">Android App Version</h4>
                            <div className="space-y-3">
                                <div>
                                    <label className="block text-[10px] font-bold text-[var(--t2)] uppercase mb-1">Minimum (Force Update)</label>
                                    <input type="text" value={formData.minAppVersionAndroid} onChange={e => setFormData({ ...formData, minAppVersionAndroid: e.target.value })} className="w-full bg-[var(--bg1)] border border-[var(--bd)] text-[var(--t0)] rounded-lg py-2 px-3 text-xs focus:outline-none focus:border-[var(--pri)]" />
                                </div>
                                <div>
                                    <label className="block text-[10px] font-bold text-[var(--t2)] uppercase mb-1">Latest Available</label>
                                    <input type="text" value={formData.latestAppVersionAndroid} onChange={e => setFormData({ ...formData, latestAppVersionAndroid: e.target.value })} className="w-full bg-[var(--bg1)] border border-[var(--bd)] text-[var(--t0)] rounded-lg py-2 px-3 text-xs focus:outline-none focus:border-[var(--pri)]" />
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div className="rounded-[24px] py-4">
                    <h3 className="text-lg font-fraunces font-semibold text-[var(--t0)] mb-4">API Integrations</h3>

                    <div className="space-y-4">
                        <div className="p-4 border border-[var(--bd)] rounded-xl bg-[var(--bg0)]">
                            <div className="flex justify-between items-center mb-2">
                                <span className="font-bold text-[13px] text-[var(--t0)]">Firebase Cloud Messaging</span>
                                <StatusBadge status="Active" />
                            </div>
                            <p className="text-[11px] text-[var(--t2)] truncate">Configured via google-services.json</p>
                        </div>
                    </div>
                </div>

                <div className="flex justify-end sticky bottom-6 z-10 lg:hidden">
                    <button onClick={handleSave} disabled={saving} className="w-full flex justify-center items-center gap-2 card-gradient px-8 py-3 rounded-full text-sm font-bold text-white shadow-xl shadow-[var(--pri-transparent)] hover:opacity-90 transition-opacity disabled:opacity-50">
                        <Save size={16} /> {saving ? "Saving..." : "Save All Configurations"}
                    </button>
                </div>
            </div>
        </div>
    );
}

