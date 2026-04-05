# Admin Portal Feedback Log (Non-Authoritative)

This file is a historical log of stakeholder feedback and what changed in response.

For the single source of truth on current status, run history, and verification evidence, see `PROGRESS.md`.

---

## Overview
This document serves to track the feedback points provided and the progress made to incorporate them into the Admin Portal.

## Feedback Points & Resolution Status

### 1. Dashboard Dummy Data
- **Feedback:** Dashboard still shows dummy data, revenue and 68 percent is hardcoded, and number of requests also.
- **Status:** **Resolved.**
  - Updated `DashboardView.tsx` to pull `totalRequests`, `totalPackages` from Firestore.
  - Replaced the hard-coded 68% approval rate with a dynamically calculated percentage based on `publishedListings` vs `totalListings`.
  - Monthly Recurring Revenue (MRR) now shows an estimated dynamic value based on active subscriptions instead of a hardcoded "PKR 446.7K".

### 2. Non-Functional Top-Right Buttons ("Add New")
- **Feedback:** Every button on screens' top right is not functional, like add new ?
- **Status:** **Resolved.**
  - The generic "Add New" and "Filters" buttons that were statically placed in `page.tsx` across all views have been removed.
  - Functionality like "Broadcast Message" is implemented in the dedicated `NotificationsView` (broadcast + specific user sends).

### 3. Gemini Keys Configuration
- **Feedback:** How do I add Gemini keys?
- **Status:** **Resolved.**
  - Created a dedicated `GeminiView.tsx` which now includes both an editable section for managing the Gemini API Key (saved securely to the config/settings collection) and a section for viewing the Gemini logs.

### 4. Contact Management vs. Help & Support
- **Feedback:** Contact management contains feedback and support. Sidebar also contains help and support - what's the difference? Help & Support screen is unnecessary as it shows nothing.
- **Status:** **Resolved.**
  - The empty and confusing "Help & Support" view has been completely removed from the sidebar and the application.
  - "Contact Management" remains as the single source for viewing user-submitted feedback and support tickets.

### 5. Anonymous Users Issue
- **Feedback:** Users screen shows anonymous user? There is no name written?
- **Status:** **Resolved.**
  - Added robust fallback logic in `UsersView.tsx`. It now checks for multiple possible name fields in the Firestore documents (`userName`, `name`, `displayName`) instead of relying solely on `name`, preventing "Anonymous User" from showing up when a valid name exists under a different key.

### 6. Settings Screen Refinements
- **Feedback:** Google APIs section in settings is not clickable. Remove the iOS force version check, we don't have an iOS app.
- **Status:** **Resolved.**
  - Restructured `SettingsView.tsx`.
  - Removed the iOS App Version configuration sections.
  - Added an editable field for the Google Maps API Key under the Google APIs section.

### 7. Posts Refactoring
- **Feedback:** Listing and requests should be merged with tabs as it is a post? Or we use two separate collections? Anything is fine just merge it.
- **Status:** **Resolved.**
  - Merged these sections into a new `PostsView.tsx` using a tabbed interface, combining Listings and Requests contextually.

### 8. App Banners
- **Feedback:** App banners are requests right where user will see and approve them? This also should be a separate screen.
- **Status:** **Resolved.**
  - Extracted "App Banners" into its own dedicated `BannersView.tsx` screen, added it to the sidebar for direct access.

### 9. Notifications
- **Feedback:** Notification should have separate side bar menu and there should be an option to send notification to specific user also.
- **Status:** **Resolved.**
  - Created a standalone `NotificationsView.tsx` screen and added it to the sidebar. It includes a modal option to send a custom push notification to specific users based on UID.

## Round 2 Feedback & Resolution Status

### 10. Package/Plan Addition Flow
- **Feedback:** Adding a plan/package is confusing, it should popup a dialog with complete details in card form with glass transparent design and we should add it.
- **Status:** **Resolved.** (Refactored `PackagesView.tsx` to use a glassmorphic modal with complete input details).

### 11. Performance and Animations
- **Feedback:** Improve the performance of web page, they look slow and add animations to it.
- **Status:** **Resolved.** (Added native CSS data-fetching stagger animations and page layout transitions using Tailwind `animate-in fade-in slide-in-from-bottom` across data-rich tables and components).

### 12. Green Palette in Light Mode
- **Feedback:** The green color and gradient that we use are lighter and in white they are dark, I want lighter same green palette in white mode.
- **Status:** **Resolved.** (Updated `globals.css` variables to match the dark mode palette in the light mode section).

### 13. Appearance Toggle Location
- **Feedback:** Appearance toggle in side bar takes too much space, move it to top right in larger screen and in smaller screen it should be just a toggle with text without these corner borders making it large component.
- **Status:** **Resolved.** (Removed bottom card from sidebar. Added sun/moon toggle to the top right header for large screens, and a simple toggle button for small screens).

### 14. Gemini Keys Format
- **Feedback:** In gemini there should be 4 gemini keys option to upload but it should be separated by tabs.
- **Status:** **Resolved.** (Added 4 tabbed inputs in the `GeminiView` to save multiple API keys).

### 15. Add Banner Button
- **Feedback:** There is add banner button I think that should be removed.
- **Status:** **Resolved.** (Removed the Add Banner button from `BannersView.tsx`).

### 16. Notification User Dropdown
- **Feedback:** In add notification there should be a paginated dropdown of users with search (pagination should be ui only) not firestore because it lacks search, so we should load all the users and show in dropdown paginated.
- **Status:** **Resolved.** (Created a custom searchable, paginated select dropdown component for users in `NotificationsView`).

### 17. Premium UI Feel & Page Consistency
- **Feedback:** UI should feel premium current it does not feel, some screens are not in design sync like they lack titles or filters.
- **Status:** **Resolved.** (Standardized all main views to have clear titles, descriptions, and consistent toolbars. Introduced subtle glassy components and card gradients).

### 18. Screen Specific Filters
- **Feedback:** Filters in every page make sure they should be clickable and functional top bar contains filters but they should be added inside every screen seperately.
- **Status:** **Resolved.** (Added individual inline Search and Status dropdown filters inside `UsersView`, `ListingsView`, `RequestsView`, and `ReportsView`). (Removed dummy image and pulled the active user's image, hooked up the notification button to navigate to the notifications tab. Removed global filters from header).

### 19. Tab Styling
- **Feedback:** Tab style should not be the one that is current but it should be like the screenshot attached. (Segmented control style).
- **Status:** **Resolved.** (Updated the tab style in `PostsView.tsx` to use a segmented control layout with a colored background container and floating buttons).

## Round 3 Feedback & Resolution Status

### 20. Subscription Tab Style
- **Feedback:** Subscription tab style is not changed to segmented control like the Posts view.
- **Status:** **Resolved.** (Updated `SubscriptionsView.tsx` tabs from underline style to segmented control matching `PostsView`).

### 21. Remove Hardcoded Packages
- **Feedback:** PackagesView shows hardcoded default packages (free/premium/business) even before Firestore loads.
- **Status:** **Resolved.** (Removed hardcoded fallback packages from `PackagesView.tsx`. Now starts empty and only loads from Firestore).

### 22. Dashboard "Show Details" Button
- **Feedback:** Dashboard has a "Show details" button that is not working.
- **Status:** **Resolved.** (Replaced the non-functional button with an inline Approved/Not Approved breakdown panel showing real data).

### 23. Dashboard MoreHorizontal Buttons
- **Feedback:** There are MoreHorizontal (⋯) buttons on the dashboard that are not functional.
- **Status:** **Resolved.** (Replaced all 4 non-functional MoreHorizontal buttons with informational labels like "This Month", "Live", "Next 30 days").

### 24. Dashboard Hardcoded Growth Values
- **Feedback:** Dashboard shows growth in users or listings with hardcoded % values (+15.5%, -8.4%, etc.).
- **Status:** **Resolved.** (Removed the hardcoded growth percentages from KPI cards. Made `change`/`isUp` props optional in the `KPICard` component so they no longer show fake data).

## Conclusion

All requested UI/UX improvements, structural refactorings, and dynamic data implementations have been successfully incorporated into the RentO Admin Portal.
