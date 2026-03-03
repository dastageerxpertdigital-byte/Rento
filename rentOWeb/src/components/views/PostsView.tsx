"use client";

import { useState } from 'react';
import ListingsView from './ListingsView';
import RequestsView from './RequestsView';

export default function PostsView() {
    const [activeTab, setActiveTab] = useState<'listings' | 'requests'>('listings');

    return (
        <div className="flex flex-col h-full gap-4">
            <div className="flex items-center justify-between">
                <div className="flex bg-[var(--bg2)] border border-[var(--bd)] rounded-xl p-1.5 shadow-inner">
                    <button
                        onClick={() => setActiveTab('listings')}
                        className={`px-8 py-2.5 text-[13px] font-bold tracking-wide transition-all rounded-lg whitespace-nowrap flex items-center gap-2 ${activeTab === 'listings' ? 'bg-[var(--bg1)] text-[var(--pri)] shadow-md border border-[var(--bd)]' : 'text-[var(--t2)] hover:text-[var(--t0)] border border-transparent'}`}
                    >
                        Listings
                    </button>
                    <button
                        onClick={() => setActiveTab('requests')}
                        className={`px-8 py-2.5 text-[13px] font-bold tracking-wide transition-all rounded-lg whitespace-nowrap flex items-center gap-2 ${activeTab === 'requests' ? 'bg-[var(--bg1)] text-[var(--pri)] shadow-md border border-[var(--bd)]' : 'text-[var(--t2)] hover:text-[var(--t0)] border border-transparent'}`}
                    >
                        Requests
                    </button>
                </div>
            </div>

            <div className="flex-1 overflow-hidden">
                {activeTab === 'listings' ? <ListingsView /> : <RequestsView />}
            </div>
        </div>
    );
}
