"use client";

import React, { useState } from 'react';
import { TrendingUp, TrendingDown, X } from 'lucide-react';

export function StatusBadge({ status }: { status: string }) {
    let colors = "bg-[var(--bg4)] text-[var(--t1)]"; // Default neutral

    const s = status.toLowerCase();
    if (["active", "published", "completed", "approved", "resolved", "ready"].includes(s)) {
        colors = "bg-[var(--pri-transparent)] text-[var(--pri)]";
    } else if (["pending", "draft", "open", "processing"].includes(s)) {
        colors = "bg-[rgba(240,201,74,0.15)] text-[var(--acc)]";
    } else if (["rejected", "blocked", "closed", "failed"].includes(s)) {
        colors = "bg-[rgba(224,96,96,0.15)] text-[var(--red)]";
    }

    return (
        <span className={`text-[11px] font-bold px-2.5 py-1 rounded-md inline-flex items-center gap-1 ${colors}`}>
            {status}
        </span>
    );
}

export function ToggleSwitch({ checked, onChange }: { checked: boolean, onChange: (val: boolean) => void }) {
    return (
        <div className="relative inline-block w-12 mr-2 align-middle select-none transition duration-200 ease-in cursor-pointer" onClick={() => onChange(!checked)}>
            <input type="checkbox" name="toggle" checked={checked} readOnly className="toggle-checkbox absolute block w-6 h-6 rounded-full bg-white border-4 appearance-none cursor-pointer border-[var(--bg4)]" />
            <label className={`toggle-label block overflow-hidden h-6 rounded-full cursor-pointer transition-colors ${checked ? 'bg-[var(--pri)]' : 'bg-[var(--bg4)]'}`}></label>
        </div>
    );
}

export function NavItem({ icon, label, badge, active, onClick }: { icon: React.ReactNode, label: string, badge?: string, active?: boolean, onClick?: () => void }) {
    return (
        <div onClick={onClick} className={`flex items-center justify-between px-3 py-3 rounded-2xl cursor-pointer transition-all duration-200 ${active ? 'bg-[var(--pri-transparent)] text-[var(--pri)] font-bold' : 'text-[var(--t1)] hover:bg-[var(--bg3)] hover:text-[var(--t0)] font-medium'}`}>
            <div className="flex items-center gap-3">
                {icon}
                <span className="text-[13px] tracking-wide">{label}</span>
            </div>
            {badge && (
                <span className="text-[10px] font-bold bg-[var(--pri-transparent)] text-[var(--pri)] px-2 py-0.5 rounded-md">
                    {badge}
                </span>
            )}
        </div>
    );
}


export function KPICard({ title, value, change, trend, isUp, icon, trendColor, sparkline }: {
    title: string,
    value: string,
    change?: string,
    trend?: string,
    isUp?: boolean,
    icon: React.ReactNode,
    trendColor?: string,
    sparkline?: number[]
}) {
    const displayTrend = trend || change;
    const isPositive = isUp !== undefined ? isUp : (displayTrend ? !displayTrend.includes('-') : true);
    const tColor = trendColor || (isPositive ? 'var(--pri)' : 'var(--red)');
    const tBg = trendColor ? `${trendColor}15` : (isPositive ? 'var(--pri-transparent)' : 'rgba(224,96,96,0.15)');

    return (
        <div className="bg-[var(--bg2)] border border-[var(--bd)] rounded-[24px] p-5 flex flex-col hover:-translate-y-1 transition-transform duration-300 relative overflow-hidden group">
            <div className="flex justify-between items-center mb-4 relative z-10">
                <h3 className="text-sm font-bold text-[var(--t0)]">{title}</h3>
                <div className="p-2 rounded-xl bg-[var(--bg3)] text-[var(--t1)] group-hover:text-[var(--pri)] transition-colors">
                    {icon}
                </div>
            </div>

            <div className="flex items-end justify-between mt-auto relative z-10">
                <div>
                    <h2 className="text-3xl font-fraunces font-bold text-[var(--t0)]">{value}</h2>
                    {displayTrend && (
                        <div className="flex items-center gap-2 mt-1">
                            <span className="flex items-center gap-1 text-[10px] font-bold px-2 py-0.5 rounded-md" style={{ color: tColor, backgroundColor: tBg }}>
                                {isPositive ? <TrendingUp size={10} /> : <TrendingDown size={10} />}
                                {displayTrend}
                            </span>
                            <span className="text-[10px] font-medium text-[var(--t2)] italic">growth</span>
                        </div>
                    )}
                </div>

                {sparkline && (
                    <div className="w-16 h-8 opacity-50 group-hover:opacity-100 transition-opacity">
                        <svg viewBox="0 0 100 40" className="w-full h-full">
                            <path
                                d={`M 0 40 ${sparkline.map((v, i) => `L ${(i / (sparkline.length - 1)) * 100} ${40 - (v / Math.max(...sparkline)) * 30}`).join(' ')}`}
                                fill="none"
                                stroke={tColor}
                                strokeWidth="3"
                                strokeLinecap="round"
                                strokeLinejoin="round"
                            />
                        </svg>
                    </div>
                )}
            </div>

            {/* Subtle background decoration */}
            <div className="absolute -right-4 -bottom-4 w-24 h-24 bg-[var(--bg3)] rounded-full blur-3xl opacity-0 group-hover:opacity-20 transition-opacity"></div>
        </div>
    );
}

export function StatusBar({ height, label, color, value }: { height: string, label: string, color: string, value: string }) {
    return (
        <div className="flex flex-col items-center gap-2 flex-1 group cursor-pointer relative">
            <div className="absolute top-0 left-1/2 transform -translate-x-1/2 -translate-y-full opacity-0 group-hover:opacity-100 bg-[var(--t0)] text-[var(--bg1)] text-[10px] font-bold py-1 px-2 rounded-lg mb-2 z-10 transition-opacity pointer-events-none">
                {value}
                <div className="absolute -bottom-1 left-1/2 -translate-x-1/2 w-2 h-2 bg-[var(--t0)] rotate-45"></div>
            </div>
            <div className="w-full bg-[var(--bg3)] rounded-t-lg rounded-b-sm relative flex items-end h-32 overflow-hidden">
                <div
                    className="w-full rounded-t-lg rounded-b-sm transition-all duration-500 hover:brightness-110"
                    style={{ height, backgroundColor: color }}
                ></div>
            </div>
            <span className="text-[11px] font-bold text-[var(--t0)] mt-1">{label}</span>
        </div>
    );
}

export function Bar({ height, label, active }: { height: string, label: string, active?: boolean }) {
    return (
        <div className="flex flex-col items-center gap-2 flex-1 group cursor-pointer">
            <div className="w-full bg-[var(--bg3)] rounded-t-lg rounded-b-sm relative flex items-end h-32 overflow-hidden">
                <div
                    className={`w-full rounded-t-lg rounded-b-sm transition-all duration-500 ${active ? 'bg-[var(--pri)]' : 'bg-[var(--bd2)] group-hover:bg-[var(--pri-transparent)]'}`}
                    style={{ height }}
                ></div>
            </div>
            <span className={`text-[11px] font-bold ${active ? 'text-[var(--pri)]' : 'text-[var(--t2)]'}`}>{label}</span>
        </div>
    );
}

export function DashboardTableRow({ id, name, icon, pkg, expiry }: { id: string, name: string, icon: string, pkg: string, expiry: string }) {
    const isUrgent = expiry.includes('3') || expiry.includes('5');

    return (
        <tr className="border-b border-[var(--bd)] hover:bg-[var(--bg3)] transition-colors group cursor-pointer">
            <td className="py-4 text-[13px] text-[var(--t2)] font-medium">{id}</td>
            <td className="py-4 flex items-center gap-3">
                <div className="w-8 h-8 rounded-full bg-[var(--bg4)] text-[var(--t1)] flex items-center justify-center font-bold text-xs">
                    {icon}
                </div>
                <span className="font-bold text-[var(--t0)] text-[14px]">{name}</span>
            </td>
            <td className="py-4 text-right">
                <span className="text-[13px] font-medium text-[var(--t1)]">{pkg}</span>
            </td>
            <td className="py-4 text-right">
                <span className={`text-[12px] font-bold px-2.5 py-1 rounded-md ${isUrgent ? 'bg-[rgba(224,96,96,0.15)] text-[var(--red)]' : 'bg-[var(--bg4)] text-[var(--t1)]'}`}>
                    {expiry}
                </span>
            </td>
            <td className="py-4 text-right">
                <button className="text-[12px] font-bold text-[var(--pri)] bg-[var(--pri-transparent)] px-3 py-1.5 rounded-full hover:bg-[var(--pri)] hover:text-white transition-colors">
                    Review
                </button>
            </td>
        </tr>
    );
}

export function ConfirmDialog({ title, message, onConfirm, onCancel, confirmText = "Confirm", cancelText = "Cancel", isDestructive = false }: { title: string, message: string, onConfirm: () => void, onCancel: () => void, confirmText?: string, cancelText?: string, isDestructive?: boolean }) {
    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
            <div className="absolute inset-0 bg-black/60 backdrop-blur-sm transition-opacity" onClick={onCancel}></div>
            <div className="relative bg-[var(--bg1)] border border-[var(--bd)] p-6 rounded-[24px] max-w-md w-full shadow-2xl z-10 animate-in fade-in zoom-in-95 duration-200">
                <h3 className="text-xl font-fraunces font-bold text-[var(--t0)] mb-2">{title}</h3>
                <p className="text-[13px] text-[var(--t2)] mb-6">{message}</p>
                <div className="flex justify-end gap-3">
                    <button onClick={onCancel} className="px-5 py-2.5 rounded-full text-sm font-bold text-[var(--t1)] hover:bg-[var(--bg3)] transition-colors">
                        {cancelText}
                    </button>
                    <button onClick={onConfirm} className={`px-5 py-2.5 rounded-full text-sm font-bold text-white transition-colors shadow-lg ${isDestructive ? 'bg-[var(--red)] hover:bg-[#a03030] shadow-[rgba(224,96,96,0.3)]' : 'card-gradient shadow-[var(--pri-transparent)] hover:opacity-90'}`}>
                        {confirmText}
                    </button>
                </div>
            </div>
        </div>
    );
}

export function InputReasonDialog({ title, message, onConfirm, onCancel, placeholder = "Enter reason..." }: { title: string, message: string, onConfirm: (reason: string) => void, onCancel: () => void, placeholder?: string }) {
    const [reason, setReason] = useState("");

    return (
        <div className="fixed inset-0 z-50 flex items-center justify-center p-4">
            <div className="absolute inset-0 bg-black/60 backdrop-blur-sm transition-opacity" onClick={onCancel}></div>
            <div className="relative bg-[var(--bg1)] border border-[var(--bd)] p-6 rounded-[24px] max-w-md w-full shadow-2xl z-10 animate-in fade-in zoom-in-95 duration-200">
                <h3 className="text-xl font-fraunces font-bold text-[var(--t0)] mb-2">{title}</h3>
                <p className="text-[13px] text-[var(--t2)] mb-4">{message}</p>

                <textarea
                    value={reason}
                    onChange={e => setReason(e.target.value)}
                    placeholder={placeholder}
                    className="w-full h-24 bg-[var(--bg0)] border border-[var(--bd)] text-[var(--t0)] rounded-xl py-3 px-4 text-sm focus:outline-none focus:border-[var(--pri)] resize-none mb-6"
                ></textarea>

                <div className="flex justify-end gap-3">
                    <button onClick={onCancel} className="px-5 py-2.5 rounded-full text-sm font-bold text-[var(--t1)] hover:bg-[var(--bg3)] transition-colors">
                        Cancel
                    </button>
                    <button disabled={!reason.trim()} onClick={() => onConfirm(reason)} className="px-5 py-2.5 rounded-full text-sm font-bold text-white card-gradient shadow-lg shadow-[var(--pri-transparent)] hover:opacity-90 transition-opacity disabled:opacity-50">
                        Submit
                    </button>
                </div>
            </div>
        </div>
    );
}

export function Shimmer({ className = "" }: { className?: string }) {
    return (
        <div className={`relative overflow-hidden bg-[var(--bg3)] rounded-xl ${className}`}>
            <div className="absolute inset-0 -translate-x-full animate-[shimmer_1.5s_infinite] bg-gradient-to-r from-transparent via-[var(--bg4)] to-transparent"></div>
        </div>
    );
}

export function ImageModal({ imageUrl, altText = "Image preview", onClose }: { imageUrl: string, altText?: string, onClose: () => void }) {
    return (
        <div className="fixed inset-0 z-[60] flex items-center justify-center p-4">
            <div className="absolute inset-0 bg-black/80 backdrop-blur-sm transition-opacity" onClick={onClose}></div>
            <div className="relative bg-[var(--bg1)] rounded-2xl max-w-4xl w-full max-h-[90vh] overflow-hidden flex flex-col shadow-2xl z-10 animate-in zoom-in-95 duration-200">
                <div className="flex justify-between items-center p-4 border-b border-[var(--bd)] bg-[var(--bg2)]">
                    <h3 className="font-bold text-[var(--t0)]">{altText}</h3>
                    <button onClick={onClose} className="text-[var(--t2)] hover:text-[var(--red)]"><X size={20} /></button>
                </div>
                <div className="flex-1 overflow-auto p-4 bg-[var(--bg0)] flex justify-center">
                    {/* eslint-disable-next-line @next/next/no-img-element */}
                    <img src={imageUrl} alt={altText} className="max-w-full object-contain rounded-lg" />
                </div>
            </div>
        </div>
    );
}
