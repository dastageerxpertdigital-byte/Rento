# RentO — Backend
## Cloud Functions + Firebase Security Rules
### Complete Engineering Specification

> **Version:** 1.0.0  
> **Branch:** `feature/cloud-functions`  
> **Runtime:** Node.js 20 + TypeScript  
> **SDK:** `firebase-admin` v12, `firebase-functions` v4  
> **Deploy:** `firebase deploy --only functions,firestore:rules`

---

## Table of Contents

1. [Project Setup](#1-project-setup)
2. [Function: `sendFCMNotification`](#2-function-sendfcmnotification)
3. [Function: `broadcastToAll`](#3-function-broadcasttoall)
4. [Function: `deleteUserAccount`](#4-function-deleteuseraccount)
5. [Function: `syncSubscriptions`](#5-function-syncsubscriptions)
6. [Function: `onListingDeleted`](#6-function-onlistingdeleted)
7. [Function: `onRequestDeleted`](#7-function-onrequestdeleted)
8. [Firebase Security Rules](#8-firebase-security-rules)
9. [Deployment Checklist](#9-deployment-checklist)

---

## 1. Project Setup

```
functions/
├── src/
│   ├── index.ts              ← exports all functions
│   ├── notifications.ts      ← sendFCMNotification, broadcastToAll
│   ├── account.ts            ← deleteUserAccount
│   ├── subscriptions.ts      ← syncSubscriptions
│   └── listings.ts           ← onListingDeleted, onRequestDeleted
├── package.json
└── tsconfig.json
```

**`package.json` key deps:**
```json
{
  "dependencies": {
    "firebase-admin": "^12.0.0",
    "firebase-functions": "^4.8.0"
  },
  "engines": { "node": "20" }
}
```

**`functions/src/index.ts`:**
```typescript
import * as admin from 'firebase-admin';
admin.initializeApp();
export { sendFCMNotification, broadcastToAll } from './notifications';
export { deleteUserAccount } from './account';
export { syncSubscriptions } from './subscriptions';
export { onListingDeleted, onRequestDeleted } from './listings';
```

---

## 2. Function: `sendFCMNotification`

**File:** `functions/src/notifications.ts`  
**Trigger:** HTTP Callable — called by admin portal and other Cloud Functions.

```typescript
export const sendFCMNotification = functions.https.onCall(async (data, context) => {
    // Admin-only or internal call — verify caller
    if (!context.auth) throw new functions.https.HttpsError('unauthenticated', 'Must be signed in.');

    const { fcmToken, title, body, notifType, targetUid, extraData = {} } = data as {
        fcmToken: string;
        title: string;
        body: string;
        notifType: string;
        targetUid: string;
        extraData?: Record<string, string>;
    };

    if (!fcmToken || !title || !notifType || !targetUid) {
        throw new functions.https.HttpsError('invalid-argument', 'Missing required fields.');
    }

    const message: admin.messaging.Message = {
        token: fcmToken,
        notification: { title, body },
        data: {
            notifType,
            listingId:  extraData.listingId  ?? '',
            requestId:  extraData.requestId  ?? '',
            chatId:     extraData.chatId     ?? '',
            targetUid:  targetUid,
            ...extraData,
        },
        android: {
            priority: 'high',
            notification: {
                channelId: 'nesco_default',
                color: '#2ECC8A',
            },
        },
    };

    try {
        await admin.messaging().send(message);
    } catch (err: any) {
        // Token may be stale — don't throw; log it
        console.error(`FCM send failed for token ${fcmToken}:`, err.message);
    }

    // Write to Firestore notification log
    await admin.firestore()
        .collection('notifications')
        .doc(targetUid)
        .collection('items')
        .add({
            notifType,
            title,
            body,
            data: { ...extraData, targetUid },
            isRead: false,
            createdAt: admin.firestore.FieldValue.serverTimestamp(),
        });

    return { success: true };
});
```

---

## 3. Function: `broadcastToAll`

**Trigger:** HTTP Callable — admin portal "Broadcast" feature.

```typescript
export const broadcastToAll = functions.https.onCall(async (data, context) => {
    if (!context.auth) throw new functions.https.HttpsError('unauthenticated', 'Must be signed in.');

    const callerDoc = await admin.firestore()
        .collection('users').doc(context.auth.uid).get();
    if (callerDoc.data()?.userType !== 'admin') {
        throw new functions.https.HttpsError('permission-denied', 'Admin only.');
    }

    const { title, body } = data as { title: string; body: string };
    if (!title || !body) throw new functions.https.HttpsError('invalid-argument', 'title and body required.');

    await admin.messaging().sendToTopic('all_users', {
        notification: { title, body },
        android: { priority: 'high' },
    } as any);

    return { success: true };
});
```

---

## 4. Function: `deleteUserAccount`

**Trigger:** HTTP Callable — called from Android app's "Delete Account" flow.

Full cascade deletion:

```typescript
export const deleteUserAccount = functions.https.onCall(async (data, context) => {
    if (!context.auth) throw new functions.https.HttpsError('unauthenticated', 'Must be signed in.');
    const uid = context.auth.uid;
    const db  = admin.firestore();
    const bucket = admin.storage().bucket();

    // 1. Delete all listings + Storage photos
    const listings = await db.collection('listings').where('uid', '==', uid).get();
    for (const doc of listings.docs) {
        const listingId = doc.id;
        // Delete Storage folder
        await bucket.deleteFiles({ prefix: `listings/${uid}/${listingId}/` }).catch(() => {});
        // Delete listing doc
        await doc.ref.delete();
    }

    // 2. Delete all tenant requests
    const requests = await db.collection('tenantRequests').where('uid', '==', uid).get();
    await Promise.all(requests.docs.map(d => d.ref.delete()));

    // 3. Delete chats (mark deletedFor — we don't hard-delete to preserve other user's copy)
    const chats = await db.collection('chats')
        .where('participants', 'array-contains', uid).get();
    await Promise.all(chats.docs.map(d =>
        d.ref.update({ deletedFor: admin.firestore.FieldValue.arrayUnion(uid) })
    ));

    // 4. Delete saved items
    const savedListings = await db.collection('users').doc(uid).collection('savedListings').get();
    const savedRequests = await db.collection('users').doc(uid).collection('savedRequests').get();
    const batch = db.batch();
    savedListings.docs.forEach(d => batch.delete(d.ref));
    savedRequests.docs.forEach(d => batch.delete(d.ref));
    await batch.commit();

    // 5. Delete notifications
    const notifs = await db.collection('notifications').doc(uid).collection('items').get();
    const batch2 = db.batch();
    notifs.docs.forEach(d => batch2.delete(d.ref));
    batch2.delete(db.collection('notifications').doc(uid));
    await batch2.commit();

    // 6. Delete user profile avatar from Storage
    await bucket.deleteFiles({ prefix: `avatars/${uid}/` }).catch(() => {});

    // 7. Delete user Firestore document
    await db.collection('users').doc(uid).delete();

    // 8. Delete Firebase Auth user
    await admin.auth().deleteUser(uid);

    return { success: true };
});
```

---

## 5. Function: `syncSubscriptions`

**Trigger:** Cloud Scheduler — every 5 days (`every 5 days`). Also manually callable by admin.

```typescript
export const syncSubscriptions = functions.pubsub
    .schedule('every 5 days')
    .onRun(async () => {
        const db  = admin.firestore();
        const now = admin.firestore.Timestamp.now();

        // Fetch all users with active subscriptions that have expired
        const expiredUsers = await db.collection('users')
            .where('packageExpiryDate', '<', now)
            .where('isPaid', '==', true)
            .get();

        for (const userDoc of expiredUsers.docs) {
            const uid  = userDoc.id;
            const data = userDoc.data();

            // 1. Reset to free tier
            await userDoc.ref.update({
                isPaid:              false,
                packageId:           null,
                packageExpiryDate:   null,
                maxPublishedListings: 3,    // free tier defaults from config
                maxPublishedRequests: 2,
            });

            // 2. Unpublish excess listings (keep most recent 3)
            const publishedListings = await db.collection('listings')
                .where('uid', '==', uid)
                .where('status', '==', 'published')
                .orderBy('createdAt', 'desc')
                .get();
            const toUnpublish = publishedListings.docs.slice(3);
            for (const listingDoc of toUnpublish) {
                await listingDoc.ref.update({ status: 'draft' });
            }

            // 3. Close excess active requests (keep most recent 2)
            const activeRequests = await db.collection('tenantRequests')
                .where('uid', '==', uid)
                .where('status', '==', 'active')
                .orderBy('createdAt', 'desc')
                .get();
            const toClose = activeRequests.docs.slice(2);
            for (const reqDoc of toClose) {
                await reqDoc.ref.update({ status: 'closed' });
            }

            // 4. Send expiry FCM notification
            const fcmToken = data.fcmToken;
            if (fcmToken) {
                await admin.messaging().send({
                    token: fcmToken,
                    notification: {
                        title: 'Your plan has expired',
                        body:  'Your subscription has ended. Upgrade to continue enjoying premium features.',
                    },
                    data: { notifType: 'subscription_expiring' },
                    android: { priority: 'high', notification: { channelId: 'nesco_default' } },
                }).catch(() => {});
            }

            // 5. Write to notifications log
            await db.collection('notifications').doc(uid).collection('items').add({
                notifType: 'subscription_expiring',
                title:     'Your plan has expired',
                body:      'Your subscription has ended. Upgrade to continue enjoying premium features.',
                isRead:    false,
                createdAt: admin.firestore.FieldValue.serverTimestamp(),
            });
        }

        console.log(`syncSubscriptions: processed ${expiredUsers.size} expired users.`);
        return null;
    });
```

---

## 6. Function: `onListingDeleted`

**Trigger:** HTTP Callable from admin portal's "Delete Listing" action.  
(Also has a Firestore `onDelete` trigger for cleanup, but the admin action is explicit callable for FCM.)

```typescript
export const onListingDeleted = functions.https.onCall(async (data, context) => {
    if (!context.auth) throw new functions.https.HttpsError('unauthenticated', 'Must be signed in.');

    const callerDoc = await admin.firestore()
        .collection('users').doc(context.auth.uid).get();
    if (callerDoc.data()?.userType !== 'admin') {
        throw new functions.https.HttpsError('permission-denied', 'Admin only.');
    }

    const { listingId, ownerUid, adminMessage } = data as {
        listingId: string;
        ownerUid: string;
        adminMessage: string;
    };

    const db     = admin.firestore();
    const bucket = admin.storage().bucket();

    // 1. Delete Storage files
    await bucket.deleteFiles({ prefix: `listings/${ownerUid}/${listingId}/` }).catch(() => {});

    // 2. Set adminDeleteMessage + status = "blocked" (soft delete for audit, hard delete optional)
    await db.collection('listings').doc(listingId).update({
        status:             'blocked',
        adminDeleteMessage: adminMessage,
        updatedAt:          admin.firestore.FieldValue.serverTimestamp(),
    });

    // 3. Send FCM to listing owner
    const ownerDoc  = await db.collection('users').doc(ownerUid).get();
    const fcmToken  = ownerDoc.data()?.fcmToken;
    if (fcmToken) {
        await admin.messaging().send({
            token: fcmToken,
            notification: {
                title: 'Listing Removed',
                body:  adminMessage || 'Your listing has been removed by admin.',
            },
            data: { notifType: 'listing_deleted_by_admin', listingId },
            android: { priority: 'high', notification: { channelId: 'nesco_default' } },
        }).catch(() => {});
    }

    // 4. Write to notifications
    await db.collection('notifications').doc(ownerUid).collection('items').add({
        notifType:  'listing_deleted_by_admin',
        title:      'Listing Removed',
        body:       adminMessage || 'Your listing has been removed by admin.',
        data:       { listingId },
        isRead:     false,
        createdAt:  admin.firestore.FieldValue.serverTimestamp(),
    });

    return { success: true };
});
```

---

## 7. Function: `onRequestDeleted`

**Trigger:** HTTP Callable from admin portal's "Delete Request" action.

```typescript
export const onRequestDeleted = functions.https.onCall(async (data, context) => {
    if (!context.auth) throw new functions.https.HttpsError('unauthenticated', 'Must be signed in.');

    const callerDoc = await admin.firestore()
        .collection('users').doc(context.auth.uid).get();
    if (callerDoc.data()?.userType !== 'admin') {
        throw new functions.https.HttpsError('permission-denied', 'Admin only.');
    }

    const { requestId, ownerUid, adminMessage } = data as {
        requestId: string;
        ownerUid: string;
        adminMessage: string;
    };

    const db = admin.firestore();

    // 1. Update status + adminDeleteMessage
    await db.collection('tenantRequests').doc(requestId).update({
        status:             'rejected',
        adminDeleteMessage: adminMessage,
        updatedAt:          admin.firestore.FieldValue.serverTimestamp(),
    });

    // 2. Send FCM
    const ownerDoc = await db.collection('users').doc(ownerUid).get();
    const fcmToken = ownerDoc.data()?.fcmToken;
    if (fcmToken) {
        await admin.messaging().send({
            token: fcmToken,
            notification: {
                title: 'Request Removed',
                body:  adminMessage || 'Your request has been removed by admin.',
            },
            data: { notifType: 'request_deleted_by_admin', requestId },
            android: { priority: 'high', notification: { channelId: 'nesco_default' } },
        }).catch(() => {});
    }

    // 3. Write to notifications
    await db.collection('notifications').doc(ownerUid).collection('items').add({
        notifType: 'request_deleted_by_admin',
        title:     'Request Removed',
        body:      adminMessage || 'Your request has been removed by admin.',
        data:      { requestId },
        isRead:    false,
        createdAt: admin.firestore.FieldValue.serverTimestamp(),
    });

    return { success: true };
});
```

---

## 8. Firebase Security Rules

**File:** `firestore.rules`

```javascript
rules_version = '2';

service cloud.firestore {
  match /databases/{database}/documents {

    // ── Helpers ───────────────────────────────────────────────────
    function isAuth()       { return request.auth != null; }
    function isOwner(uid)   { return isAuth() && request.auth.uid == uid; }
    function isAdmin()      {
      return isAuth() &&
        get(/databases/$(database)/documents/users/$(request.auth.uid)).data.userType == 'admin';
    }
    function isEmailVerified() {
      return isAuth() && request.auth.token.email_verified == true;
    }

    // ── Users ─────────────────────────────────────────────────────
    match /users/{uid} {
      allow read:  if isOwner(uid) || isAdmin();
      allow write: if isOwner(uid) || isAdmin();

      match /savedListings/{listingId} {
        allow read, write: if isOwner(uid);
      }
      match /savedRequests/{requestId} {
        allow read, write: if isOwner(uid);
      }
      match /dailyMessageCount/{date} {
        allow read, write: if isOwner(uid);
      }
    }

    // ── Listings ──────────────────────────────────────────────────
    match /listings/{listingId} {
      allow read:   if resource.data.status == 'published' || isAdmin() ||
                       (isAuth() && resource.data.uid == request.auth.uid);
      allow create: if isEmailVerified() && request.resource.data.uid == request.auth.uid;
      allow update: if isAdmin() ||
                       (isAuth() && resource.data.uid == request.auth.uid);
      allow delete: if isAdmin() ||
                       (isAuth() && resource.data.uid == request.auth.uid);
    }

    // ── Tenant Requests ───────────────────────────────────────────
    match /tenantRequests/{requestId} {
      allow read:   if resource.data.status == 'active' || isAdmin() ||
                       (isAuth() && resource.data.uid == request.auth.uid);
      allow create: if isEmailVerified() && request.resource.data.uid == request.auth.uid;
      allow update: if isAdmin() ||
                       (isAuth() && resource.data.uid == request.auth.uid);
      allow delete: if isAdmin();
    }

    // ── Chats ─────────────────────────────────────────────────────
    match /chats/{chatId} {
      allow read, write: if isAuth() &&
        request.auth.uid in resource.data.participants || isAdmin();

      allow create: if isEmailVerified() &&
        request.auth.uid in request.resource.data.participants;

      match /messages/{msgId} {
        allow read:   if isAuth() &&
          request.auth.uid in get(/databases/$(database)/documents/chats/$(chatId)).data.participants;
        allow create: if isEmailVerified() &&
          request.resource.data.senderUid == request.auth.uid &&
          request.auth.uid in get(/databases/$(database)/documents/chats/$(chatId)).data.participants;
        allow update: if isAuth() &&
          resource.data.senderUid == request.auth.uid;   // only sender can edit/delete own messages
        allow delete: if isAdmin();
      }
    }

    // ── Reports ───────────────────────────────────────────────────
    match /reports/{reportId} {
      allow create: if isEmailVerified();
      allow read:   if isAdmin();
      allow update, delete: if isAdmin();
    }

    // ── Packages ──────────────────────────────────────────────────
    match /packages/{packageId} {
      allow read:   if isAuth();
      allow write:  if isAdmin();
    }

    // ── Subscription Requests ─────────────────────────────────────
    match /subscriptionRequests/{requestId} {
      allow create: if isEmailVerified() &&
        request.resource.data.uid == request.auth.uid;
      allow read:   if isAdmin() ||
        (isAuth() && resource.data.uid == request.auth.uid);
      allow update: if isAdmin();
      allow delete: if isAdmin();
    }

    // ── Notifications ─────────────────────────────────────────────
    match /notifications/{uid} {
      allow read, write: if isOwner(uid) || isAdmin();
      match /items/{itemId} {
        allow read, write: if isOwner(uid) || isAdmin();
      }
    }

    // ── Config ────────────────────────────────────────────────────
    match /config/{docId} {
      allow read:  if isAuth();
      allow write: if isAdmin();
    }

    // ── Gemini Key Logs ───────────────────────────────────────────
    match /geminiKeyLogs/{logId} {
      allow read:  if isAdmin();
      allow write: if false;   // Cloud Functions only — via admin SDK (bypasses rules)
    }

    // ── Feature Requests / Feedback ───────────────────────────────
    match /featureRequests/{requestId} {
      allow create: if isEmailVerified();
      allow read:   if isAdmin() ||
        (isAuth() && resource.data.uid == request.auth.uid);
      allow update, delete: if isAdmin();
    }

    // ── Feature Sliders (Banner) ──────────────────────────────────
    match /featureSliders/{sliderId} {
      allow read:   if true;                // public — shown on home screen
      allow write:  if isAdmin();
    }

    // ── Slider Requests ───────────────────────────────────────────
    match /sliderRequests/{requestId} {
      allow create: if isEmailVerified() && request.resource.data.uid == request.auth.uid;
      allow read:   if isAdmin() || (isAuth() && resource.data.uid == request.auth.uid);
      allow update, delete: if isAdmin();
    }
  }
}
```

**Firebase Storage Rules (`storage.rules`):**

```javascript
rules_version = '2';

service firebase.storage {
  match /b/{bucket}/o {

    // Listing photos: owner upload + public read
    match /listings/{ownerUid}/{listingId}/{allPaths=**} {
      allow read:   if true;
      allow write:  if request.auth != null && request.auth.uid == ownerUid
                    && request.resource.size < 2 * 1024 * 1024;   // 2MB limit
    }

    // User avatars: owner upload + public read
    match /avatars/{uid}/{allPaths=**} {
      allow read:   if true;
      allow write:  if request.auth != null && request.auth.uid == uid
                    && request.resource.size < 1 * 1024 * 1024;   // 1MB limit
    }

    // Subscription proof screenshots: owner upload + admin read
    match /subscriptionProofs/{uid}/{allPaths=**} {
      allow read:  if request.auth != null &&
        (request.auth.uid == uid ||
         firestore.get(/databases/(default)/documents/users/$(request.auth.uid)).data.userType == 'admin');
      allow write: if request.auth != null && request.auth.uid == uid
                   && request.resource.size < 5 * 1024 * 1024;   // 5MB limit
    }
  }
}
```

---

## 9. Deployment Checklist

```
- [ ] functions/package.json: firebase-admin ^12, firebase-functions ^4, engines.node = "20"
- [ ] functions/tsconfig.json: strict = true
- [ ] firebase.json: "functions": { "source": "functions", "runtime": "nodejs20" }
- [ ] .firebaserc: project aliases (dev, prod)
- [ ] firebase.json: "firestore": { "rules": "firestore.rules" }
- [ ] firebase.json: "storage": { "rules": "storage.rules" }
- [ ] Cloud Scheduler job for syncSubscriptions created in Firebase Console
- [ ] Admin FCM token: saved to config/admin.adminFcmToken on admin login
- [ ] test: npm run build in functions/ → 0 TypeScript errors
- [ ] firebase emulators:start --only functions,firestore → all functions register
- [ ] firebase deploy --only functions,firestore:rules,storage
```

---

*End of Cloud Functions + Firebase Security Rules v1.0.0*
