"use client";

import { useEffect, useState } from 'react';
import { useAuth } from '../context/AuthContext';
import { useCollectionCount } from '../lib/hooks';
import { where } from 'firebase/firestore';
import { signOut } from 'firebase/auth';
import { auth, db, messaging } from '../lib/firebase';
import { getToken } from 'firebase/messaging';
import { doc, updateDoc } from 'firebase/firestore';
import {
  LayoutDashboard, Users, Building, Search, Package,
  Settings, Bell,
  Sun, Moon, LogOut, MessageSquare, CheckCircle, FileTerminal
} from 'lucide-react';
import { NavItem } from '../components/ui/index';
import LoginView from '../components/LoginView';

import DashboardView from '../components/views/DashboardView';
import SettingsView from '../components/views/SettingsView';
import SubscriptionsView from '../components/views/SubscriptionsView';
import UsersView from '../components/views/UsersView';
import PackagesView from '../components/views/PackagesView';
import PostsView from '../components/views/PostsView';
import BannersView from '../components/views/BannersView';
import ReportsView from '../components/views/ReportsView';
import ContactManagementView from '../components/views/ContactManagementView';
import PageLoader from '../components/ui/PageLoader';
import NotificationsView from '../components/views/NotificationsView';
import GeminiView from '../components/views/GeminiView';
import StandardTablePage from '../components/views/StandardTablePage';

export default function Home() {
  const { user, loading } = useAuth();

  if (loading) {
    return (
      <div className="h-screen flex items-center justify-center bg-[var(--bg0)]">
        <PageLoader message="Loading RentO admin portal..." />
      </div>
    );
  }

  if (!user) return <LoginView />;
  return <AppShell />;
}

function AppShell() {
  const [isDark, setIsDark] = useState(true);
  const [activeTab, setActiveTab] = useState('Dashboard');
  const [hydrated, setHydrated] = useState(false);
  const { user } = useAuth();
  const pendingListings = useCollectionCount("listings", [where("status", "==", "Pending")]);

  // Load persistence
  useEffect(() => {
    const savedTheme = localStorage.getItem('rento_theme');
    const savedTab = localStorage.getItem('rento_active_tab');

    if (savedTheme !== null) {
      const isD = savedTheme === 'dark';
      setIsDark(isD);
      if (isD) document.documentElement.classList.add('dark');
      else document.documentElement.classList.remove('dark');
    }
    if (savedTab !== null) {
      setActiveTab(savedTab);
    }
    setHydrated(true);
  }, []);

  // Persist theme
  useEffect(() => {
    if (!hydrated) return;
    if (isDark) {
      document.documentElement.classList.add('dark');
      localStorage.setItem('rento_theme', 'dark');
    } else {
      document.documentElement.classList.remove('dark');
      localStorage.setItem('rento_theme', 'light');
    }
  }, [isDark, hydrated]);

  useEffect(() => {
    if (!hydrated) return;
    localStorage.setItem('rento_active_tab', activeTab);
  }, [activeTab, hydrated]);

  // FCM Registration for Admin
  useEffect(() => {
    if (!user || !messaging || typeof window === 'undefined') return;

    const registerFCM = async () => {
      try {
        const permission = await Notification.requestPermission();
        if (permission === 'granted' && messaging) {
          const token = await getToken(messaging, {
            vapidKey: process.env.NEXT_PUBLIC_WEB_PUSH_VAPID_KEY
          });
          if (token) {
            await updateDoc(doc(db, 'users', user.uid), {
              fcmToken: token,
              lastTokenUpdate: new Date()
            });
            console.log("Admin FCM Token registered successfully.");
          }
        }
      } catch (err) {
        console.warn("FCM Registration skipped or failed:", err);
      }
    };

    // Small delay to ensure browser readiness
    const timer = setTimeout(registerFCM, 3000);
    return () => clearTimeout(timer);
  }, [user]);

  const renderContent = () => {
    switch (activeTab) {
      case 'Dashboard': return <DashboardView />;
      case 'Settings': return <SettingsView />;
      case 'Subscriptions': return <SubscriptionsView />;
      case 'Posts': return <PostsView />;
      case 'Users': return <UsersView />;
      case 'Banners': return <BannersView />;
      case 'Reports': return <ReportsView />;
      case 'Contact Management': return <ContactManagementView />;
      case 'Notifications': return <NotificationsView />;
      case 'Packages': return <PackagesView />;
      case 'Gemini': return <GeminiView />;
      default:
        return <StandardTablePage title={activeTab} />;
    }
  };

  if (!hydrated) {
    return (
      <div className="h-screen flex items-center justify-center bg-[var(--bg0)]">
        <PageLoader message="Restoring session..." />
      </div>
    );
  }

  return (
    <div className="flex h-screen overflow-hidden transition-colors duration-300 bg-[var(--bg0)]">
      <aside className="w-64 bg-[var(--bg1)] border-r border-[var(--bd)] flex flex-col transition-colors duration-300 z-10 hidden md:flex">
        <div className="h-20 flex items-center px-6">
          <div className="flex items-center gap-3">
            <div className="w-8 h-8 rounded-lg card-gradient flex items-center justify-center text-white font-bold text-lg">R</div>
            <span className="font-fraunces text-2xl font-bold text-[var(--t0)]">RentO</span>
          </div>
        </div>

        <div className="flex-1 overflow-y-auto py-6 px-4 flex flex-col gap-1">
          <span className="text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider mb-2 px-3">Main Menu</span>
          <NavItem icon={<LayoutDashboard size={20} />} label="Dashboard" active={activeTab === 'Dashboard'} onClick={() => setActiveTab('Dashboard')} />
          <NavItem icon={<Building size={20} />} label="Posts" badge={pendingListings > 0 ? pendingListings.toString() : undefined} active={activeTab === 'Posts'} onClick={() => setActiveTab('Posts')} />
          <NavItem icon={<Users size={20} />} label="Users" active={activeTab === 'Users'} onClick={() => setActiveTab('Users')} />
          <NavItem icon={<Search size={20} />} label="Banners" active={activeTab === 'Banners'} onClick={() => setActiveTab('Banners')} />
          <NavItem icon={<FileTerminal size={20} />} label="User Reports" active={activeTab === 'Reports'} onClick={() => setActiveTab('Reports')} />
          <NavItem icon={<MessageSquare size={20} />} label="Contact Management" active={activeTab === 'Contact Management'} onClick={() => setActiveTab('Contact Management')} />

          <span className="text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider mt-6 mb-2 px-3">Finances</span>
          <NavItem icon={<CheckCircle size={20} />} label="Subscriptions" active={activeTab === 'Subscriptions'} onClick={() => setActiveTab('Subscriptions')} />
          <NavItem icon={<Package size={20} />} label="Packages" active={activeTab === 'Packages'} onClick={() => setActiveTab('Packages')} />

          <span className="text-[11px] font-bold text-[var(--t2)] uppercase tracking-wider mt-6 mb-2 px-3">System</span>
          <NavItem icon={<Settings size={20} />} label="Settings" active={activeTab === 'Settings'} onClick={() => setActiveTab('Settings')} />
          <NavItem icon={<FileTerminal size={20} />} label="Gemini" active={activeTab === 'Gemini'} onClick={() => setActiveTab('Gemini')} />
        </div>

        <div className="p-4 border-t border-[var(--bd)] flex flex-col gap-1">
          <NavItem icon={<LogOut size={20} />} label="Sign Out" onClick={() => signOut(auth)} />

          <button
            onClick={() => setIsDark(!isDark)}
            className="w-full flex items-center gap-3 px-3 py-2.5 rounded-xl text-sm font-medium text-[var(--t2)] hover:bg-[var(--bg3)] hover:text-[var(--pri)] transition-colors mt-2 md:hidden"
          >
            {isDark ? <Sun size={20} /> : <Moon size={20} />}
            <span>{isDark ? 'Light Mode' : 'Dark Mode'}</span>
          </button>
        </div>
      </aside>

      <main className="flex-1 flex flex-col relative overflow-hidden transition-colors duration-300">
        <header className="h-20 flex items-center justify-between px-8 bg-[var(--bg0)] z-10 glass-effect">
          <div className="flex items-center gap-4 w-96 invisible md:visible">
            <h2 className="text-xl font-fraunces font-bold text-[var(--t0)] tracking-tight">{activeTab}</h2>
          </div>

          <div className="flex items-center gap-4">
            <button
              onClick={() => setIsDark(!isDark)}
              className="hidden md:flex w-10 h-10 items-center justify-center rounded-full bg-[var(--bg2)] border border-[var(--bd)] text-[var(--t1)] hover:text-[var(--pri)] hover:bg-[var(--bg3)] transition-colors relative"
            >
              {isDark ? <Sun size={18} /> : <Moon size={18} />}
            </button>
            <button
              onClick={() => setActiveTab('Notifications')}
              className="w-10 h-10 flex items-center justify-center rounded-full bg-[var(--bg2)] border border-[var(--bd)] text-[var(--t1)] hover:text-[var(--pri)] hover:bg-[var(--bg3)] transition-colors relative"
            >
              <Bell size={18} />
              <span className="absolute top-2 right-2.5 w-2 h-2 rounded-full bg-[var(--red)] border-2 border-[var(--bg2)]"></span>
            </button>
            <div className="w-10 h-10 rounded-full card-gradient p-0.5 ml-2 cursor-pointer flex items-center justify-center">
              <div className="w-full h-full rounded-full bg-[var(--bg1)] overflow-hidden flex items-center justify-center">
                {user?.photoURL ? (
                  /* eslint-disable-next-line @next/next/no-img-element */
                  <img src={user.photoURL} alt="Admin" className="w-full h-full object-cover" />
                ) : (
                  <span className="text-[var(--t1)] font-bold">{user?.displayName?.[0]?.toUpperCase() || 'A'}</span>
                )}
              </div>
            </div>
          </div>
        </header>

        <div className="flex-1 overflow-y-auto p-4 md:p-8 pt-4">
          <div className="mb-6 invisible h-0 overflow-hidden text-transparent" aria-hidden="true" aria-label="legacy-removed-header-spacer"></div>
          {renderContent()}
        </div>
      </main>
    </div>
  );
}
