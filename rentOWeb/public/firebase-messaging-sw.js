importScripts('https://www.gstatic.com/firebasejs/9.0.0/firebase-app-compat.js');
importScripts('https://www.gstatic.com/firebasejs/9.0.0/firebase-messaging-compat.js');

firebase.initializeApp({
    apiKey: "AIzaSyBWqFscMEzOYAFwxA5noArvmMLYhJ1DDXY",
    authDomain: "rento-6bc38.firebaseapp.com",
    projectId: "rento-6bc38",
    storageBucket: "rento-6bc38.firebasestorage.app",
    messagingSenderId: "449548580573",
    appId: "1:449548580573:web:bcfdd0ca71ccfa5c0d8d34"
});

const messaging = firebase.messaging();

messaging.onBackgroundMessage((payload) => {
    console.log('[firebase-messaging-sw.js] Received background message ', payload);
    const notificationTitle = payload.notification.title;
    const notificationOptions = {
        body: payload.notification.body,
        icon: '/logo.png'
    };

    self.registration.showNotification(notificationTitle, notificationOptions);
});
