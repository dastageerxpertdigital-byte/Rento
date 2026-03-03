import { httpsCallable } from "firebase/functions";
import { functions } from "./firebase";

// Notification payload type
interface NotificationPayload {
    title: string;
    body: string;
    targetUserId: string;
    data?: Record<string, string>;
}

// Broadcast target types
export type BroadcastTarget = 'all' | 'free_only' | 'paid_only';
interface BroadcastPayload {
    title: string;
    body: string;
    target: BroadcastTarget;
    data?: Record<string, string>;
}

/**
 * Sends a targeted FCM notification to a specific user
 */
export const sendFCMNotification = async (payload: NotificationPayload) => {
    try {
        const callable = httpsCallable<NotificationPayload, { success: boolean, message?: string }>(functions, 'sendFCMNotification');
        const response = await callable(payload);
        return response.data;
    } catch (error) {
        console.error("Error calling sendFCMNotification:", error);
        throw error;
    }
};

/**
 * Broadcasts a notification to multiple users based on target group
 */
export const broadcastToAll = async (payload: BroadcastPayload) => {
    try {
        const callable = httpsCallable<BroadcastPayload, { success: boolean, count: number }>(functions, 'broadcastToAll');
        const response = await callable(payload);
        return response.data;
    } catch (error) {
        console.error("Error calling broadcastToAll:", error);
        throw error;
    }
};

/**
 * Securely completely deletes a user's account and all their related data
 */
export const deleteUserAccount = async (userId: string) => {
    try {
        const callable = httpsCallable<{ userId: string }, { success: boolean }>(functions, 'deleteUserAccount');
        const response = await callable({ userId });
        return response.data;
    } catch (error) {
        console.error("Error calling deleteUserAccount:", error);
        throw error;
    }
};
