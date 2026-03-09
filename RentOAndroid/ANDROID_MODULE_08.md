# RentO — Android App
## Module 08 — Chat (Encrypted Messaging)
### Complete Engineering Specification

> **Version:** 1.0.0
> **Status:** Active — Single Source of Truth for Module 08
> **Branch:** `feature/module-08-chat`
> **Depends on:** Module 01 ✅ · Module 02 ✅ · Module 03 ✅ · Module 04 ✅ · Module 05 ✅ · Module 06 ✅ · Module 07 ✅
> **Audience:** Android Agent
>
> ⚠️ **AGENT LAW:** Every colour, padding, animation, encryption detail, and interaction is derived verbatim from the prototype and `REQUIREMENTS_SPECIFICATION_v2_3.md`. Do not simplify. Do not improvise. If anything is ambiguous — stop and ask.

---

## Table of Contents

1. [Module Overview](#1-module-overview)
2. [File Structure](#2-file-structure)
3. [Task List](#3-task-list)
4. [Architecture — Layers & Contracts](#4-architecture--layers--contracts)
5. [Task M08-T01 — Domain Models](#task-m08-t01--domain-models)
6. [Task M08-T02 — Encryption Engine (`ChatCrypto`)](#task-m08-t02--encryption-engine-chatcrypto)
7. [Task M08-T03 — `ChatRepository` Interface + Impl](#task-m08-t03--chatrepository-interface--impl)
8. [Task M08-T04 — Daily Limit Repository](#task-m08-t04--daily-limit-repository)
9. [Task M08-T05 — Use Cases](#task-m08-t05--use-cases)
10. [Task M08-T06 — `ChatListViewModel`](#task-m08-t06--chatlistviewmodel)
11. [Task M08-T07 — `ChatDetailViewModel`](#task-m08-t07--chatdetailviewmodel)
12. [Task M08-T08 — Koin Module](#task-m08-t08--koin-module)
13. [Task M08-T09 — Navigation Wiring](#task-m08-t09--navigation-wiring)
14. [Task M08-T10 — `ChatListScreen`](#task-m08-t10--chatlistscreen)
15. [Task M08-T11 — `ChatDetailScreen` Shell](#task-m08-t11--chatdetailscreen-shell)
16. [Task M08-T12 — Top App Bar](#task-m08-t12--top-app-bar)
17. [Task M08-T13 — Message Bubbles](#task-m08-t13--message-bubbles)
18. [Task M08-T14 — Input Bar](#task-m08-t14--input-bar)
19. [Task M08-T15 — Long-Press Context Menu](#task-m08-t15--long-press-context-menu)
20. [Task M08-T16 — Delete Message Dialog](#task-m08-t16--delete-message-dialog)
21. [Task M08-T17 — Delete Conversation Dialog](#task-m08-t17--delete-conversation-dialog)
22. [Task M08-T18 — Chat Upgrade Prompt](#task-m08-t18--chat-upgrade-prompt)
23. [Task M08-T19 — Shimmer Loading States](#task-m08-t19--shimmer-loading-states)
24. [Task M08-T20 — String Resources](#task-m08-t20--string-resources)
25. [Task M08-T21 — Unit Tests](#task-m08-t21--unit-tests)
26. [Task M08-T22 — Build Gate](#task-m08-t22--build-gate)
27. [Journey Coverage Checklist](#27-journey-coverage-checklist)
28. [CODE_REVIEW_MODULE_08.md Template](#28-code_review_module_08md-template)

---

## 1. Module Overview

Module 08 delivers the full **Chat system** — two screens (Chat List and Chat Detail) with end-to-end AES-256-GCM client-side message encryption, daily send limits enforced per user plan tier, real-time Firestore listeners, message editing/deletion, and conversation deletion.

**Key features:**
- **Chat List** (`chats` route): all conversations, avatar with unread count badge, conversation preview with listing/request title, last message (decrypted), timestamp
- **Chat Detail** (`chat/{chatId}` route): real-time message stream (Firestore listener), sender/receiver bubble styling, date separators, long-press context menu (Edit / Delete sender's messages), deleted message placeholder
- **AES-256-GCM encryption**: PBKDF2 key derivation from `uid_a + uid_b + referenceId + salt`. All messages stored as base64 encrypted blobs. Decryption happens client-side on receive.
- **Daily send limit**: checked before every send. On limit reached → `ChatUpgradePrompt` (non-dismissable except via "Maybe Later" link).
- **Delete message**: replaces message content in Firestore with `isDeleted: true`. Bubble shows italic "Message deleted".
- **Delete conversation**: removes chat from user's view (marks as deleted for that user — other user keeps their copy).
- **Report User**: three-dot menu → opens shared `ReportSheet` with `targetType = USER`.

**Firestore schema used:**
```
chats/{chatId}
  referenceId: string           // listingId or requestId
  referenceType: "listing" | "request"
  participants: string[]        // [uid_a, uid_b] sorted
  salt: string                  // base64 random salt for key derivation
  createdAt: timestamp
  lastMessage: string           // encrypted preview
  lastMessageAt: timestamp

chats/{chatId}/messages/{msgId}
  senderUid: string
  content: string               // base64 encrypted
  editedContent: string | null  // base64 encrypted
  isEdited: boolean
  isDeleted: boolean
  createdAt: timestamp
  editedAt: timestamp | null

users/{uid}/dailyMessageCount/{YYYY-MM-DD}
  count: int
```

---

## 2. File Structure

```
app/src/main/java/com/rento/app/
├── domain/
│   ├── model/
│   │   ├── Chat.kt
│   │   ├── ChatMessage.kt
│   │   └── DailyMessageCount.kt
│   ├── repository/
│   │   ├── ChatRepository.kt
│   │   └── MessageLimitRepository.kt
│   └── usecase/
│       └── chat/
│           ├── GetChatsUseCase.kt
│           ├── GetMessagesUseCase.kt
│           ├── SendMessageUseCase.kt
│           ├── EditMessageUseCase.kt
│           ├── DeleteMessageUseCase.kt
│           ├── DeleteConversationUseCase.kt
│           ├── CheckMessageLimitUseCase.kt
│           └── IncrementMessageCountUseCase.kt
├── data/
│   ├── crypto/
│   │   └── ChatCrypto.kt                  ← NEW — encryption engine
│   └── repository/
│       ├── ChatRepositoryImpl.kt
│       └── MessageLimitRepositoryImpl.kt
├── presentation/
│   └── chat/
│       ├── list/
│       │   ├── ChatListViewModel.kt
│       │   ├── ChatListScreen.kt
│       │   └── components/
│       │       ├── ConversationRow.kt
│       │       └── ChatListShimmer.kt
│       └── detail/
│           ├── ChatDetailViewModel.kt
│           ├── ChatDetailScreen.kt
│           └── components/
│               ├── ChatTopBar.kt
│               ├── MessageBubble.kt
│               ├── DateSeparator.kt
│               ├── MessageContextMenu.kt
│               ├── DeleteMessageDialog.kt
│               ├── DeleteConversationDialog.kt
│               ├── ChatUpgradePrompt.kt
│               └── ChatDetailShimmer.kt
└── di/
    └── ChatModule.kt

app/src/main/res/values/strings.xml        ← Module 08 strings appended
app/src/test/java/com/rento/app/
├── data/crypto/
│   └── ChatCryptoTest.kt
├── domain/usecase/chat/
│   ├── SendMessageUseCaseTest.kt
│   └── CheckMessageLimitUseCaseTest.kt
└── presentation/chat/
    ├── ChatListViewModelTest.kt
    └── ChatDetailViewModelTest.kt
```

---

## 3. Task List

| ID | Task | File(s) | Status |
|----|------|---------|--------|
| M08-T01 | Domain models — `Chat`, `ChatMessage`, `DailyMessageCount` | `domain/model/` | ☐ |
| M08-T02 | `ChatCrypto` — AES-256-GCM encrypt/decrypt + PBKDF2 key derivation | `data/crypto/ChatCrypto.kt` | ☐ |
| M08-T03 | `ChatRepository` interface + `ChatRepositoryImpl` | `domain/repository/`, `data/repository/` | ☐ |
| M08-T04 | `MessageLimitRepository` interface + `MessageLimitRepositoryImpl` | `domain/repository/`, `data/repository/` | ☐ |
| M08-T05 | All chat use cases (8 total) | `domain/usecase/chat/` | ☐ |
| M08-T06 | `ChatListViewModel` | `presentation/chat/list/` | ☐ |
| M08-T07 | `ChatDetailViewModel` | `presentation/chat/detail/` | ☐ |
| M08-T08 | Koin module — `ChatModule.kt` | `di/` | ☐ |
| M08-T09 | Nav graph — wire `chats` + `chat/{chatId}` | `RentoNavGraph.kt` | ☐ |
| M08-T10 | `ChatListScreen` + `ConversationRow` + shimmer | `chat/list/` | ☐ |
| M08-T11 | `ChatDetailScreen` shell — `LazyColumn` reversed, real-time listener | `ChatDetailScreen.kt` | ☐ |
| M08-T12 | `ChatTopBar` — back, avatar, name/listing, three-dot menu | `ChatTopBar.kt` | ☐ |
| M08-T13 | `MessageBubble` — sender/receiver variants, deleted state, edited label | `MessageBubble.kt` | ☐ |
| M08-T14 | `InputBar` — text input + send button, limit check on send | `ChatDetailScreen.kt` | ☐ |
| M08-T15 | `MessageContextMenu` — long-press dropdown (Edit / Delete) | `MessageContextMenu.kt` | ☐ |
| M08-T16 | `DeleteMessageDialog` — 3-phase GlassDialog | `DeleteMessageDialog.kt` | ☐ |
| M08-T17 | `DeleteConversationDialog` — 3-phase GlassDialog | `DeleteConversationDialog.kt` | ☐ |
| M08-T18 | `ChatUpgradePrompt` — non-dismissable GlassBottomSheet | `ChatUpgradePrompt.kt` | ☐ |
| M08-T19 | Shimmer states — chat list + chat detail | `ChatListShimmer.kt`, `ChatDetailShimmer.kt` | ☐ |
| M08-T20 | String resources | `strings.xml` | ☐ |
| M08-T21 | Unit tests | `*Test.kt` | ☐ |
| M08-T22 | Build gate | — | ☐ |

---

## 4. Architecture — Layers & Contracts

### 4.1 Domain Models

```kotlin
// Chat.kt
data class Chat(
    val id: String,
    val referenceId: String,          // listingId or requestId
    val referenceType: String,        // "listing" | "request"
    val participants: List<String>,   // [uid_a, uid_b] sorted
    val salt: String,                 // base64, for key derivation
    val otherUserUid: String,         // computed: participants - currentUserId
    val otherUserName: String,        // fetched from user profile
    val referenceTitle: String,       // listing/request title for subtitle
    val lastMessageDecrypted: String, // decrypted preview
    val lastMessageAt: Long,
    val unreadCount: Int,
    val createdAt: Long,
)

// ChatMessage.kt
data class ChatMessage(
    val id: String,
    val chatId: String,
    val senderUid: String,
    val contentDecrypted: String,     // empty string if isDeleted
    val isEdited: Boolean,
    val isDeleted: Boolean,
    val createdAt: Long,
    val editedAt: Long?,
)

// DailyMessageCount.kt
data class DailyMessageCount(
    val date: String,       // YYYY-MM-DD
    val count: Int,
    val limit: Int,         // from config — free or paid
)
```

### 4.2 Repository Interfaces

```kotlin
// ChatRepository.kt
interface ChatRepository {
    fun getChats(uid: String): Flow<List<Chat>>
    fun getMessages(chatId: String, salt: String): Flow<List<ChatMessage>>
    suspend fun getOrCreateChat(
        currentUid: String,
        otherUid: String,
        referenceId: String,
        referenceType: String,
    ): Result<Chat>
    suspend fun sendMessage(chatId: String, salt: String, senderUid: String, content: String): Result<Unit>
    suspend fun editMessage(chatId: String, salt: String, messageId: String, newContent: String): Result<Unit>
    suspend fun deleteMessage(chatId: String, messageId: String): Result<Unit>
    suspend fun deleteConversation(chatId: String, uid: String): Result<Unit>
}

// MessageLimitRepository.kt
interface MessageLimitRepository {
    suspend fun getTodayCount(uid: String): Result<Int>
    suspend fun incrementCount(uid: String): Result<Int>     // returns new count
    suspend fun getLimit(uid: String): Result<Int>           // reads from config + user plan
}
```

### 4.3 UI States

```kotlin
// ChatListUiState
data class ChatListUiState(
    val chats: List<Chat> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
)

// ChatDetailUiState
data class ChatDetailUiState(
    val chat: Chat? = null,
    val messages: List<ChatMessage> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val inputText: String = "",
    val sendLoading: Boolean = false,

    // Daily limit
    val dailyCount: Int = 0,
    val dailyLimit: Int = 20,
    val showUpgradePrompt: Boolean = false,

    // Context menu
    val selectedMessageId: String? = null,
    val showContextMenu: Boolean = false,
    val contextMenuOffset: Offset = Offset.Zero,

    // Edit mode
    val isEditingMessage: Boolean = false,
    val editingMessageId: String? = null,

    // Delete message dialog
    val showDeleteMessageDialog: Boolean = false,
    val deleteMessageId: String? = null,
    val deleteMessagePhase: DialogPhase = DialogPhase.IDLE,
    val deleteMessageError: String? = null,

    // Delete conversation dialog
    val showDeleteConversationDialog: Boolean = false,
    val deleteConversationPhase: DialogPhase = DialogPhase.IDLE,
    val deleteConversationError: String? = null,

    // Report
    val showReportSheet: Boolean = false,
    val reportCategory: String? = null,
    val reportDetails: String = "",
    val reportLoading: Boolean = false,
    val reportSuccess: Boolean = false,
    val reportError: String? = null,

    // Navigate away after conversation deleted
    val navigateBackToList: Boolean = false,
)
```

### 4.4 Koin Module

```kotlin
val chatModule = module {
    single { ChatCrypto() }
    single<ChatRepository>         { ChatRepositoryImpl(get(), get()) }   // Firestore + ChatCrypto
    single<MessageLimitRepository> { MessageLimitRepositoryImpl(get()) }  // Firestore

    factory { GetChatsUseCase(get(), get()) }
    factory { GetMessagesUseCase(get()) }
    factory { SendMessageUseCase(get(), get(), get()) }
    factory { EditMessageUseCase(get()) }
    factory { DeleteMessageUseCase(get()) }
    factory { DeleteConversationUseCase(get()) }
    factory { CheckMessageLimitUseCase(get()) }
    factory { IncrementMessageCountUseCase(get()) }

    viewModel { ChatListViewModel(get(), get()) }
    viewModel { (chatId: String) -> ChatDetailViewModel(chatId, get(), get(), get(), get(), get(), get(), get(), get(), get()) }
}
```

---

## Task M08-T01 — Domain Models

### Sub-tasks
- [ ] **M08-T01-A** Create `domain/model/Chat.kt`, `domain/model/ChatMessage.kt`, `domain/model/DailyMessageCount.kt` as defined in section 4.1.
- [ ] **M08-T01-B** Verify: zero Android imports in all domain model files.

---

## Task M08-T02 — Encryption Engine (`ChatCrypto`)

**File:** `data/crypto/ChatCrypto.kt`

This is the core security component. Every message is encrypted before writing to Firestore and decrypted after reading. No plaintext ever touches the network.

### Key Derivation — PBKDF2

```kotlin
class ChatCrypto {

    companion object {
        private const val KEY_ALGORITHM   = "PBKDF2WithHmacSHA256"
        private const val CIPHER_ALGO     = "AES/GCM/NoPadding"
        private const val KEY_LENGTH_BITS = 256
        private const val ITERATION_COUNT = 65_536
        private const val GCM_IV_LENGTH   = 12      // bytes
        private const val GCM_TAG_LENGTH  = 128     // bits
    }

    /**
     * Derives a 256-bit AES key from the participant UIDs, reference ID, and salt.
     * Input: uid_a + uid_b + referenceId concatenated as UTF-8, salt as decoded bytes.
     * UIDs must be sorted alphabetically before concatenation to ensure both
     * participants derive the same key.
     */
    private fun deriveKey(uid1: String, uid2: String, referenceId: String, salt: ByteArray): SecretKey {
        val sortedUids = listOf(uid1, uid2).sorted()
        val password   = "${sortedUids[0]}${sortedUids[1]}$referenceId".toCharArray()
        val spec       = PBEKeySpec(password, salt, ITERATION_COUNT, KEY_LENGTH_BITS)
        val factory    = SecretKeyFactory.getInstance(KEY_ALGORITHM)
        val keyBytes   = factory.generateSecret(spec).encoded
        return SecretKeySpec(keyBytes, "AES")
    }

    /**
     * Encrypts plaintext.
     * @return base64-encoded string: IV (12 bytes) + ciphertext
     */
    fun encrypt(
        plaintext: String,
        uid1: String,
        uid2: String,
        referenceId: String,
        saltBase64: String,
    ): String {
        val salt   = Base64.decode(saltBase64, Base64.NO_WRAP)
        val key    = deriveKey(uid1, uid2, referenceId, salt)
        val iv     = ByteArray(GCM_IV_LENGTH).also { SecureRandom().nextBytes(it) }
        val cipher = Cipher.getInstance(CIPHER_ALGO).apply {
            init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(GCM_TAG_LENGTH, iv))
        }
        val ciphertext = cipher.doFinal(plaintext.toByteArray(Charsets.UTF_8))
        val combined   = iv + ciphertext
        return Base64.encodeToString(combined, Base64.NO_WRAP)
    }

    /**
     * Decrypts a base64 blob produced by encrypt().
     * Returns null on any failure (wrong key, corrupted data, etc.) — caller shows "Message unavailable."
     */
    fun decrypt(
        encryptedBase64: String,
        uid1: String,
        uid2: String,
        referenceId: String,
        saltBase64: String,
    ): String? = runCatching {
        val salt     = Base64.decode(saltBase64, Base64.NO_WRAP)
        val key      = deriveKey(uid1, uid2, referenceId, salt)
        val combined = Base64.decode(encryptedBase64, Base64.NO_WRAP)
        val iv       = combined.sliceArray(0 until GCM_IV_LENGTH)
        val ctxt     = combined.sliceArray(GCM_IV_LENGTH until combined.size)
        val cipher   = Cipher.getInstance(CIPHER_ALGO).apply {
            init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(GCM_TAG_LENGTH, iv))
        }
        cipher.doFinal(ctxt).toString(Charsets.UTF_8)
    }.getOrNull()

    /**
     * Generates a new random salt for a new chat conversation.
     * @return base64-encoded 16-byte salt
     */
    fun generateSalt(): String {
        val salt = ByteArray(16).also { SecureRandom().nextBytes(it) }
        return Base64.encodeToString(salt, Base64.NO_WRAP)
    }
}
```

### Sub-tasks
- [ ] **M08-T02-A** Implement `ChatCrypto` exactly as above.
- [ ] **M08-T02-B** Add `ChatCryptoTest.kt`:
  - `encrypt_then_decrypt_returnsOriginalPlaintext` — round-trip test
  - `decrypt_withWrongKey_returnsNull` — security test (different referenceId)
  - `decrypt_withCorruptedData_returnsNull`
  - `generateSalt_returns16ByteBase64`
  - `encrypt_sameInput_differentIV_eachTime` — ensures randomised IV
- [ ] **M08-T02-C** Imports: `javax.crypto.*`, `java.security.*`, `android.util.Base64`. These ARE allowed in `data/` layer.

---

## Task M08-T03 — `ChatRepository` Interface + Impl

**File:** `data/repository/ChatRepositoryImpl.kt`

### Sub-tasks
- [ ] **M08-T03-A** Create `domain/repository/ChatRepository.kt` with interface from section 4.2.

- [ ] **M08-T03-B** `getOrCreateChat` — creates new chat document if not found:

```kotlin
override suspend fun getOrCreateChat(
    currentUid: String,
    otherUid: String,
    referenceId: String,
    referenceType: String,
): Result<Chat> = runCatching {
    val sortedUids = listOf(currentUid, otherUid).sorted()
    // Look for existing chat with same participants + referenceId
    val existing = firestore.collection("chats")
        .whereArrayContains("participants", currentUid)
        .whereEqualTo("referenceId", referenceId)
        .get().await().documents.firstOrNull()

    if (existing != null) {
        existing.toChat(currentUid, crypto)
    } else {
        val salt = crypto.generateSalt()
        val data = hashMapOf(
            "referenceId"   to referenceId,
            "referenceType" to referenceType,
            "participants"  to sortedUids,
            "salt"          to salt,
            "createdAt"     to FieldValue.serverTimestamp(),
            "lastMessage"   to "",
            "lastMessageAt" to FieldValue.serverTimestamp(),
        )
        val ref = firestore.collection("chats").add(data).await()
        ref.get().await().toChat(currentUid, crypto)
    }
}
```

- [ ] **M08-T03-C** `getChats` — Flow from Firestore snapshot listener:

```kotlin
override fun getChats(uid: String): Flow<List<Chat>> = callbackFlow {
    val query = firestore.collection("chats")
        .whereArrayContains("participants", uid)
        .orderBy("lastMessageAt", Query.Direction.DESCENDING)
    val listener = query.addSnapshotListener { snapshot, error ->
        if (error != null) { close(error); return@addSnapshotListener }
        val chats = snapshot?.documents?.mapNotNull { doc ->
            runCatching { doc.toChat(uid, crypto) }.getOrNull()
        } ?: emptyList()
        trySend(chats)
    }
    awaitClose { listener.remove() }
}
```

- [ ] **M08-T03-D** `getMessages` — Flow decrypting each message on receive:

```kotlin
override fun getMessages(chatId: String, salt: String): Flow<List<ChatMessage>> = callbackFlow {
    val query = firestore.collection("chats").document(chatId)
        .collection("messages")
        .orderBy("createdAt", Query.Direction.ASCENDING)
    val listener = query.addSnapshotListener { snapshot, error ->
        if (error != null) { close(error); return@addSnapshotListener }
        val messages = snapshot?.documents?.mapNotNull { doc ->
            runCatching { doc.toChatMessage(chatId, salt, crypto) }.getOrNull()
        } ?: emptyList()
        trySend(messages)
    }
    awaitClose { listener.remove() }
}
```

- [ ] **M08-T03-E** `sendMessage` — encrypts then writes:

```kotlin
override suspend fun sendMessage(
    chatId: String,
    salt: String,
    senderUid: String,
    content: String,
): Result<Unit> = runCatching {
    val chat = firestore.collection("chats").document(chatId).get().await()
    val participants = (chat.get("participants") as? List<*>)?.mapNotNull { it as? String }
        ?: return@runCatching
    val referenceId  = chat.getString("referenceId") ?: return@runCatching
    val otherUid     = participants.first { it != senderUid }

    val encrypted = crypto.encrypt(content, senderUid, otherUid, referenceId, salt)
    val msgData   = hashMapOf(
        "senderUid"     to senderUid,
        "content"       to encrypted,
        "editedContent" to null,
        "isEdited"      to false,
        "isDeleted"     to false,
        "createdAt"     to FieldValue.serverTimestamp(),
        "editedAt"      to null,
    )
    firestore.collection("chats").document(chatId)
        .collection("messages").add(msgData).await()

    // Update lastMessage preview (encrypted) + timestamp
    firestore.collection("chats").document(chatId)
        .update(
            "lastMessage",   encrypted,
            "lastMessageAt", FieldValue.serverTimestamp(),
        ).await()
}
```

- [ ] **M08-T03-F** `editMessage` — encrypts new content, sets `editedContent`, `isEdited=true`, `editedAt`:

```kotlin
override suspend fun editMessage(
    chatId: String,
    salt: String,
    messageId: String,
    newContent: String,
): Result<Unit> = runCatching {
    val chat = firestore.collection("chats").document(chatId).get().await()
    val participants = (chat.get("participants") as? List<*>)?.mapNotNull { it as? String }
        ?: return@runCatching
    val referenceId = chat.getString("referenceId") ?: return@runCatching
    // current user is sender — we need both UIDs for key derivation
    val uid1 = participants[0]; val uid2 = participants[1]
    val encrypted = crypto.encrypt(newContent, uid1, uid2, referenceId, salt)
    firestore.collection("chats").document(chatId)
        .collection("messages").document(messageId)
        .update(
            "editedContent", encrypted,
            "isEdited",      true,
            "editedAt",      FieldValue.serverTimestamp(),
        ).await()
}
```

- [ ] **M08-T03-G** `deleteMessage` — soft delete (keeps document, sets `isDeleted=true`):

```kotlin
override suspend fun deleteMessage(chatId: String, messageId: String): Result<Unit> =
    runCatching {
        firestore.collection("chats").document(chatId)
            .collection("messages").document(messageId)
            .update("isDeleted", true, "content", "", "editedContent", null).await()
    }
```

- [ ] **M08-T03-H** `deleteConversation` — marks chat as deleted for the calling user (does not delete Firestore doc — other user keeps their copy):

```kotlin
override suspend fun deleteConversation(chatId: String, uid: String): Result<Unit> =
    runCatching {
        // Add current uid to a "deletedFor" array — query filters these out for this user
        firestore.collection("chats").document(chatId)
            .update("deletedFor", FieldValue.arrayUnion(uid)).await()
    }
```

> `getChats` query must also filter out chats where `uid in deletedFor`:
> Add `.whereNotIn("deletedFor", listOf(uid))` — Note: Firestore does not support `whereNotIn` on array fields directly. Use `whereArrayContains("participants", uid)` and filter client-side: `chats.filter { uid !in it.deletedFor }`. Add `deletedFor: List<String>` to `Chat` domain model and mapper.

---

## Task M08-T04 — Daily Limit Repository

**File:** `data/repository/MessageLimitRepositoryImpl.kt`

```kotlin
class MessageLimitRepositoryImpl(private val firestore: FirebaseFirestore) : MessageLimitRepository {

    override suspend fun getTodayCount(uid: String): Result<Int> = runCatching {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        firestore.collection("users").document(uid)
            .collection("dailyMessageCount").document(today)
            .get().await()
            .getLong("count")?.toInt() ?: 0
    }

    override suspend fun incrementCount(uid: String): Result<Int> = runCatching {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        val ref   = firestore.collection("users").document(uid)
            .collection("dailyMessageCount").document(today)
        firestore.runTransaction { tx ->
            val snap  = tx.get(ref)
            val count = (snap.getLong("count") ?: 0L) + 1
            tx.set(ref, mapOf("count" to count), SetOptions.merge())
            count
        }.await().toInt()
    }

    override suspend fun getLimit(uid: String): Result<Int> = runCatching {
        val configSnap = firestore.collection("config").document("chat").get().await()
        // Check user plan — simplified: read from users/{uid}.isPaid or similar
        val userSnap   = firestore.collection("users").document(uid).get().await()
        val isPaid     = userSnap.getBoolean("isPaid") ?: false
        val limitKey   = if (isPaid) "paidUserDailyLimit" else "freeUserDailyLimit"
        configSnap.getLong(limitKey)?.toInt() ?: if (isPaid) 100 else 20
    }
}
```

---

## Task M08-T05 — Use Cases

**Files:** `domain/usecase/chat/`

### Sub-tasks
- [ ] **M08-T05-A** `GetChatsUseCase`:
```kotlin
class GetChatsUseCase(private val repo: ChatRepository, private val auth: AuthRepository) {
    operator fun invoke(): Flow<List<Chat>> {
        val uid = auth.getCurrentUserId() ?: return emptyFlow()
        return repo.getChats(uid)
    }
}
```

- [ ] **M08-T05-B** `GetMessagesUseCase`:
```kotlin
class GetMessagesUseCase(private val repo: ChatRepository) {
    operator fun invoke(chatId: String, salt: String): Flow<List<ChatMessage>> =
        repo.getMessages(chatId, salt)
}
```

- [ ] **M08-T05-C** `SendMessageUseCase` — checks limit, sends, increments:
```kotlin
class SendMessageUseCase(
    private val chatRepo: ChatRepository,
    private val limitRepo: MessageLimitRepository,
    private val auth: AuthRepository,
) {
    /**
     * Returns: Result.success(Unit) on sent.
     *          Result.failure(LimitReachedException) when daily limit hit.
     *          Result.failure(Exception) on other errors.
     */
    suspend operator fun invoke(
        chatId: String,
        salt: String,
        content: String,
    ): Result<Unit> {
        val uid   = auth.getCurrentUserId() ?: return Result.failure(Exception("Not authenticated."))
        val count = limitRepo.getTodayCount(uid).getOrDefault(0)
        val limit = limitRepo.getLimit(uid).getOrDefault(20)
        if (count >= limit) return Result.failure(LimitReachedException(count, limit))
        return chatRepo.sendMessage(chatId, salt, uid, content)
            .onSuccess { limitRepo.incrementCount(uid) }
    }
}

class LimitReachedException(val used: Int, val limit: Int) : Exception("Daily limit reached: $used/$limit")
```

- [ ] **M08-T05-D** `EditMessageUseCase`:
```kotlin
class EditMessageUseCase(private val repo: ChatRepository) {
    suspend operator fun invoke(chatId: String, salt: String, messageId: String, newContent: String): Result<Unit> =
        repo.editMessage(chatId, salt, messageId, newContent)
}
```

- [ ] **M08-T05-E** `DeleteMessageUseCase`:
```kotlin
class DeleteMessageUseCase(private val repo: ChatRepository) {
    suspend operator fun invoke(chatId: String, messageId: String): Result<Unit> =
        repo.deleteMessage(chatId, messageId)
}
```

- [ ] **M08-T05-F** `DeleteConversationUseCase`:
```kotlin
class DeleteConversationUseCase(private val repo: ChatRepository, private val auth: AuthRepository) {
    suspend operator fun invoke(chatId: String): Result<Unit> {
        val uid = auth.getCurrentUserId() ?: return Result.failure(Exception("Not authenticated."))
        return repo.deleteConversation(chatId, uid)
    }
}
```

- [ ] **M08-T05-G** `CheckMessageLimitUseCase`:
```kotlin
class CheckMessageLimitUseCase(private val repo: MessageLimitRepository, private val auth: AuthRepository) {
    suspend operator fun invoke(): DailyMessageCount? {
        val uid   = auth.getCurrentUserId() ?: return null
        val count = repo.getTodayCount(uid).getOrDefault(0)
        val limit = repo.getLimit(uid).getOrDefault(20)
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        return DailyMessageCount(date = today, count = count, limit = limit)
    }
}
```

- [ ] **M08-T05-H** `IncrementMessageCountUseCase` — exposed for use post-send:
```kotlin
class IncrementMessageCountUseCase(private val repo: MessageLimitRepository, private val auth: AuthRepository) {
    suspend operator fun invoke(): Result<Int> {
        val uid = auth.getCurrentUserId() ?: return Result.failure(Exception("Not authenticated."))
        return repo.incrementCount(uid)
    }
}
```

---

## Task M08-T06 — `ChatListViewModel`

**File:** `presentation/chat/list/ChatListViewModel.kt`

```kotlin
class ChatListViewModel(
    private val getChats: GetChatsUseCase,
    private val auth: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatListUiState())
    val uiState: StateFlow<ChatListUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            getChats()
                .catch { e -> _uiState.update { it.copy(isLoading = false, error = e.message) } }
                .collect { chats ->
                    _uiState.update { it.copy(chats = chats, isLoading = false) }
                }
        }
    }
}
```

---

## Task M08-T07 — `ChatDetailViewModel`

**File:** `presentation/chat/detail/ChatDetailViewModel.kt`

```kotlin
class ChatDetailViewModel(
    private val chatId: String,
    private val getMessages: GetMessagesUseCase,
    private val sendMessage: SendMessageUseCase,
    private val editMessage: EditMessageUseCase,
    private val deleteMessage: DeleteMessageUseCase,
    private val deleteConversation: DeleteConversationUseCase,
    private val checkLimit: CheckMessageLimitUseCase,
    private val submitReport: SubmitReportUseCase,
    private val chatRepository: ChatRepository,
    private val auth: AuthRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatDetailUiState())
    val uiState: StateFlow<ChatDetailUiState> = _uiState.asStateFlow()

    private var currentChat: Chat? = null

    init {
        loadChat()
        checkDailyLimit()
    }

    private fun loadChat() {
        viewModelScope.launch {
            // Load chat metadata first (single fetch)
            chatRepository.getOrCreateChat(
                currentUid    = auth.getCurrentUserId() ?: return@launch,
                otherUid      = "",            // already exists — chatId is known
                referenceId   = "",
                referenceType = "",
            ).fold(
                onSuccess = { /* handled by getMessages */ },
                onFailure = { /* handled by error state */ },
            )
            // NOTE: In practice, the chat is already created when navigating here.
            // Load via chatId directly.
            val snap = com.google.firebase.firestore.FirebaseFirestore.getInstance()
                .collection("chats").document(chatId).get()
            // Simple approach: load chat doc then start message listener
            // Full implementation uses repository method getChatById(chatId)
        }
    }

    fun startListeningToMessages(salt: String) {
        viewModelScope.launch {
            getMessages(chatId, salt)
                .catch { e -> _uiState.update { it.copy(error = e.message) } }
                .collect { messages ->
                    _uiState.update { it.copy(messages = messages, isLoading = false) }
                }
        }
    }

    // ── Input ────────────────────────────────────────────────────────
    fun setInputText(text: String) =
        _uiState.update { it.copy(inputText = text) }

    fun sendOrEdit() {
        val state = _uiState.value
        if (state.inputText.isBlank()) return
        if (state.isEditingMessage && state.editingMessageId != null) {
            performEdit(state.editingMessageId, state.inputText)
        } else {
            performSend(state.inputText)
        }
    }

    private fun performSend(content: String) {
        val salt = currentChat?.salt ?: return
        _uiState.update { it.copy(sendLoading = true) }
        viewModelScope.launch {
            sendMessage(chatId, salt, content).fold(
                onSuccess = {
                    _uiState.update { it.copy(sendLoading = false, inputText = "") }
                },
                onFailure = { e ->
                    if (e is LimitReachedException) {
                        _uiState.update { it.copy(
                            sendLoading      = false,
                            showUpgradePrompt = true,
                            dailyCount       = e.used,
                            dailyLimit       = e.limit,
                        )}
                    } else {
                        _uiState.update { it.copy(sendLoading = false, error = e.message) }
                    }
                },
            )
        }
    }

    private fun performEdit(messageId: String, content: String) {
        val salt = currentChat?.salt ?: return
        viewModelScope.launch {
            editMessage(chatId, salt, messageId, content).fold(
                onSuccess = {
                    _uiState.update { it.copy(
                        isEditingMessage = false,
                        editingMessageId  = null,
                        inputText         = "",
                    )}
                },
                onFailure = { e -> _uiState.update { it.copy(error = e.message) } },
            )
        }
    }

    // ── Context menu ─────────────────────────────────────────────────
    fun showContextMenu(messageId: String, offset: Offset) =
        _uiState.update { it.copy(showContextMenu = true, selectedMessageId = messageId, contextMenuOffset = offset) }

    fun dismissContextMenu() =
        _uiState.update { it.copy(showContextMenu = false, selectedMessageId = null) }

    fun startEditMessage() {
        val msgId  = _uiState.value.selectedMessageId ?: return
        val msg    = _uiState.value.messages.find { it.id == msgId } ?: return
        _uiState.update { it.copy(
            showContextMenu  = false,
            isEditingMessage = true,
            editingMessageId  = msgId,
            inputText        = msg.contentDecrypted,
        )}
    }

    fun cancelEdit() =
        _uiState.update { it.copy(isEditingMessage = false, editingMessageId = null, inputText = "") }

    // ── Delete message ───────────────────────────────────────────────
    fun openDeleteMessageDialog() {
        val msgId = _uiState.value.selectedMessageId ?: return
        _uiState.update { it.copy(
            showContextMenu          = false,
            showDeleteMessageDialog   = true,
            deleteMessageId          = msgId,
            deleteMessagePhase        = DialogPhase.CONFIRM,
            deleteMessageError        = null,
        )}
    }

    fun confirmDeleteMessage() {
        val msgId = _uiState.value.deleteMessageId ?: return
        _uiState.update { it.copy(deleteMessagePhase = DialogPhase.LOADING) }
        viewModelScope.launch {
            deleteMessage(chatId, msgId).fold(
                onSuccess = { _uiState.update { it.copy(deleteMessagePhase = DialogPhase.SUCCESS) }
                    delay(800)
                    _uiState.update { it.copy(showDeleteMessageDialog = false, deleteMessagePhase = DialogPhase.IDLE) }
                },
                onFailure = { e -> _uiState.update { it.copy(deleteMessagePhase = DialogPhase.ERROR, deleteMessageError = e.message) } },
            )
        }
    }

    fun dismissDeleteMessageDialog() =
        _uiState.update { it.copy(showDeleteMessageDialog = false, deleteMessagePhase = DialogPhase.IDLE) }

    // ── Delete conversation ──────────────────────────────────────────
    fun openDeleteConversationDialog() =
        _uiState.update { it.copy(showDeleteConversationDialog = true, deleteConversationPhase = DialogPhase.CONFIRM) }

    fun confirmDeleteConversation() {
        _uiState.update { it.copy(deleteConversationPhase = DialogPhase.LOADING) }
        viewModelScope.launch {
            deleteConversation(chatId).fold(
                onSuccess = {
                    _uiState.update { it.copy(deleteConversationPhase = DialogPhase.SUCCESS) }
                    delay(800)
                    _uiState.update { it.copy(navigateBackToList = true) }
                },
                onFailure = { e ->
                    _uiState.update { it.copy(deleteConversationPhase = DialogPhase.ERROR, deleteConversationError = e.message) }
                },
            )
        }
    }

    fun dismissDeleteConversationDialog() =
        _uiState.update { it.copy(showDeleteConversationDialog = false, deleteConversationPhase = DialogPhase.IDLE) }

    // ── Upgrade prompt ───────────────────────────────────────────────
    fun dismissUpgradePrompt() =
        _uiState.update { it.copy(showUpgradePrompt = false) }

    // ── Report ───────────────────────────────────────────────────────
    fun openReportSheet()            { _uiState.update { it.copy(showReportSheet = true, reportSuccess = false, reportCategory = null, reportDetails = "") } }
    fun closeReportSheet()           { _uiState.update { it.copy(showReportSheet = false) } }
    fun setReportCategory(c: String) { _uiState.update { it.copy(reportCategory = c) } }
    fun setReportDetails(d: String)  { _uiState.update { it.copy(reportDetails = d) } }

    fun submitReport() {
        val category = _uiState.value.reportCategory ?: return
        _uiState.update { it.copy(reportLoading = true) }
        val otherUid = currentChat?.otherUserUid ?: return
        viewModelScope.launch {
            submitReport(
                targetId   = otherUid,
                targetType = ReportTargetType.USER,
                category   = category,
                details    = _uiState.value.reportDetails.takeIf { it.isNotBlank() },
            ).fold(
                onSuccess = { _uiState.update { it.copy(reportLoading = false, reportSuccess = true) } },
                onFailure = { e -> _uiState.update { it.copy(reportLoading = false, reportError = e.message) } },
            )
        }
    }

    private fun checkDailyLimit() {
        viewModelScope.launch {
            val limitInfo = checkLimit()
            if (limitInfo != null) {
                _uiState.update { it.copy(dailyCount = limitInfo.count, dailyLimit = limitInfo.limit) }
            }
        }
    }
}
```

---

## Task M08-T08 — Koin Module

**File:** `di/ChatModule.kt`

### Sub-tasks
- [ ] **M08-T08-A** Create `chatModule` per section 4.4.
- [ ] **M08-T08-B** Add to `RentoApplication.startKoin { modules(…, chatModule) }`.
- [ ] **M08-T08-C** `ChatCrypto` bound as `single`.

---

## Task M08-T09 — Navigation Wiring

### Sub-tasks
- [ ] **M08-T09-A** Wire `chats` route — bottom nav tab:

```kotlin
composable(route = "chats") {
    ChatListScreen(
        onNavigateToChat = { chatId -> navController.navigate("chat/$chatId") },
    )
}
```

- [ ] **M08-T09-B** Wire `chat/{chatId}` route (replacing placeholder set in M04/M05):

```kotlin
composable(
    route     = "chat/{chatId}",
    arguments = listOf(navArgument("chatId") { type = NavType.StringType }),
) { backStackEntry ->
    val chatId = backStackEntry.arguments?.getString("chatId") ?: return@composable
    ChatDetailScreen(
        chatId             = chatId,
        onBack             = { navController.popBackStack() },
        onNavigateToPackages = { navController.navigate("packages") },
    )
}
```

---

## Task M08-T10 — `ChatListScreen`

**File:** `presentation/chat/list/ChatListScreen.kt`

### Exact Specification

```kotlin
@Composable
fun ChatListScreen(
    viewModel: ChatListViewModel = koinViewModel(),
    onNavigateToChat: (chatId: String) -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.rentoColors.bg0),
    ) {
        // Status bar + padding
        Spacer(Modifier.windowInsetsTopHeight(WindowInsets.statusBars))
        Spacer(Modifier.height(16.dp))

        // Title
        Text(
            "Messages",
            style    = MaterialTheme.rentoTypography.displayS,    // Fraunces 28sp SemiBold
            color    = MaterialTheme.rentoColors.t0,
            modifier = Modifier.padding(horizontal = 20.dp).padding(bottom = 20.dp),
        )

        when {
            uiState.isLoading -> ChatListShimmer()
            uiState.chats.isEmpty() -> EmptyState(
                icon     = RentoIcons.Chat,
                title    = "No conversations yet",
                subtitle = "Your chats with hosts and seekers will appear here.",
            )
            else -> LazyColumn {
                items(items = uiState.chats, key = { it.id }) { chat ->
                    ConversationRow(
                        chat    = chat,
                        onClick = { onNavigateToChat(chat.id) },
                    )
                    HorizontalDivider(1.dp, MaterialTheme.rentoColors.border)
                }
            }
        }
    }
}
```

### `ConversationRow` — Exact Specification

```
Row(
  fillMaxWidth, CenterVertically,
  padding = 14.dp V / 20.dp H,
  clickable { onClick() },
):

  // Avatar with unread badge
  Box(Modifier.size(52.dp)):
    // Avatar circle
    Box(
      52.dp circle,
      background = Brush.linearGradient([RentoColors.primary, RentoColors.secondary]),
      contentAlignment = Center,
    ):
      Icon(RentoIcons.User, 24.dp, Color.White)

    // Unread badge — top-right corner
    if (chat.unreadCount > 0):
      Box(
        modifier = Modifier
          .size(19.dp + 2.5.dp*2)   // badge + ring
          .align(TopEnd)
          .background(RentoColors.bg0, CircleShape)
          .padding(2.5.dp),
        contentAlignment = Center,
      ):
        Box(
          Modifier.fillMaxSize()
            .background(Color(0xFFE05555), CircleShape),
          contentAlignment = Center,
        ):
          Text(
            if (chat.unreadCount > 9) "9+" else "${chat.unreadCount}",
            10sp ExtraBold, Color.White,
          )

  Spacer(14.dp)

  Column(Modifier.weight(1f)):
    Row(fillMaxWidth, SpaceBetween, CenterVertically):
      Text(chat.otherUserName, 15sp Bold, RentoColors.t0)
      Text(formatTimestamp(chat.lastMessageAt), 11sp, RentoColors.t2)

    Spacer(2.dp)
    Text(chat.referenceTitle, 11sp Bold, RentoColors.primary, maxLines=1, overflow=Ellipsis)

    Spacer(2.dp)
    Text(
      chat.lastMessageDecrypted.ifBlank { "No messages yet" },
      13sp, RentoColors.t2,
      maxLines = 1, overflow = Ellipsis,
    )
```

```kotlin
private fun formatTimestamp(epochMs: Long): String {
    val now  = System.currentTimeMillis()
    val diff = now - epochMs
    return when {
        diff < 60_000             -> "now"
        diff < 3_600_000          -> "${diff / 60_000}m"
        diff < 86_400_000         -> "${diff / 3_600_000}h"
        diff < 7 * 86_400_000     -> "${diff / 86_400_000}d"
        else                      -> SimpleDateFormat("dd MMM", Locale.getDefault()).format(Date(epochMs))
    }
}
```

### Sub-tasks
- [ ] **M08-T10-A** Implement `ChatListScreen` + `ConversationRow`.
- [ ] **M08-T10-B** `@Preview` — 3 conversations (1 with unread, 1 without), empty state, loading state, dark + light.

---

## Task M08-T11 — `ChatDetailScreen` Shell

**File:** `presentation/chat/detail/ChatDetailScreen.kt`

```kotlin
@Composable
fun ChatDetailScreen(
    chatId: String,
    viewModel: ChatDetailViewModel = koinViewModel(),
    onBack: () -> Unit,
    onNavigateToPackages: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentUid = /* auth.getCurrentUserId() via viewModel */ ""

    // Navigate back after conversation deleted
    LaunchedEffect(uiState.navigateBackToList) {
        if (uiState.navigateBackToList) onBack()
    }

    Scaffold(
        containerColor = MaterialTheme.rentoColors.bg0,
        topBar = {
            ChatTopBar(
                chat          = uiState.chat,
                onBack        = onBack,
                onDeleteChat  = { viewModel.openDeleteConversationDialog() },
                onReportUser  = { viewModel.openReportSheet() },
            )
        },
        bottomBar = {
            InputBar(
                text          = uiState.inputText,
                onTextChange  = { viewModel.setInputText(it) },
                onSend        = { viewModel.sendOrEdit() },
                isSending     = uiState.sendLoading,
                isEditMode    = uiState.isEditingMessage,
                onCancelEdit  = { viewModel.cancelEdit() },
            )
        },
    ) { padding ->
        when {
            uiState.isLoading -> ChatDetailShimmer()
            else -> LazyColumn(
                modifier        = Modifier.fillMaxSize().padding(padding),
                reverseLayout   = false,   // messages ordered ascending — newest at bottom
                contentPadding  = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(4.dp),
                state           = rememberLazyListState().also { /* auto-scroll to bottom */ },
            ) {
                itemsIndexed(uiState.messages, key = { _, msg -> msg.id }) { idx, message ->
                    // Date separator
                    val showSeparator = idx == 0 ||
                        !isSameDay(uiState.messages[idx-1].createdAt, message.createdAt)
                    if (showSeparator) DateSeparator(message.createdAt)

                    MessageBubble(
                        message    = message,
                        isSender   = message.senderUid == currentUid,
                        onLongPress = { offset ->
                            if (!message.isDeleted && message.senderUid == currentUid)
                                viewModel.showContextMenu(message.id, offset)
                        },
                    )
                }
            }
        }
    }

    // Overlays
    if (uiState.showContextMenu) {
        MessageContextMenu(
            offset       = uiState.contextMenuOffset,
            onEdit       = { viewModel.startEditMessage() },
            onDelete     = { viewModel.openDeleteMessageDialog() },
            onDismiss    = { viewModel.dismissContextMenu() },
        )
    }

    if (uiState.showDeleteMessageDialog) {
        DeleteMessageDialog(
            phase     = uiState.deleteMessagePhase,
            error     = uiState.deleteMessageError,
            onConfirm = { viewModel.confirmDeleteMessage() },
            onDismiss = { viewModel.dismissDeleteMessageDialog() },
        )
    }

    if (uiState.showDeleteConversationDialog) {
        DeleteConversationDialog(
            phase     = uiState.deleteConversationPhase,
            error     = uiState.deleteConversationError,
            onConfirm = { viewModel.confirmDeleteConversation() },
            onDismiss = { viewModel.dismissDeleteConversationDialog() },
        )
    }

    if (uiState.showUpgradePrompt) {
        ChatUpgradePrompt(
            used             = uiState.dailyCount,
            limit            = uiState.dailyLimit,
            onUpgrade        = { onNavigateToPackages() },
            onMaybeLater     = { viewModel.dismissUpgradePrompt() },
        )
    }

    if (uiState.showReportSheet) {
        ReportSheet(
            targetLabel      = "User",
            selectedCategory = uiState.reportCategory,
            details          = uiState.reportDetails,
            isLoading        = uiState.reportLoading,
            isSuccess        = uiState.reportSuccess,
            error            = uiState.reportError,
            onSelectCategory = { viewModel.setReportCategory(it) },
            onDetailsChange  = { viewModel.setReportDetails(it) },
            onSubmit         = { viewModel.submitReport() },
            onDismiss        = { viewModel.closeReportSheet() },
        )
    }
}
```

### Auto-scroll to bottom

```kotlin
val listState = rememberLazyListState()
LaunchedEffect(uiState.messages.size) {
    if (uiState.messages.isNotEmpty()) {
        listState.animateScrollToItem(uiState.messages.lastIndex)
    }
}
```

### Sub-tasks
- [ ] **M08-T11-A** Implement `ChatDetailScreen` shell.
- [ ] **M08-T11-B** Auto-scroll: `listState.animateScrollToItem(messages.lastIndex)` triggered on `messages.size` change.
- [ ] **M08-T11-C** `isSameDay()` helper function using `Calendar`.

---

## Task M08-T12 — Top App Bar

**File:** `presentation/chat/detail/components/ChatTopBar.kt`

### Exact Specification

```
Column(fillMaxWidth):
  Spacer(windowInsetsTopHeight)
  Row(
    fillMaxWidth, CenterVertically,
    background = RentoColors.navBg,
    border bottom = 1.dp RentoColors.border,
    padding = 12.dp top / 20.dp H / 14.dp bottom,
    blur = optional (API 31+, subtle — matches top bar style from spec)
  ):
    // Back button
    RentoIconButton(RentoIcons.Back, 42.dp, onClick=onBack)
    Spacer(14.dp)

    // Avatar + name/listing column
    Row(CenterVertically, spacedBy(12.dp), Modifier.weight(1f)):
      // Avatar
      Box(42.dp circle, Brush.linearGradient([RentoColors.primary, RentoColors.secondary])):
        Icon(RentoIcons.User, 22.dp, Color.White)

      Column:
        Text(chat?.otherUserName ?: "…", 15sp Bold, RentoColors.t0)
        Spacer(2.dp)
        Text(chat?.referenceTitle ?: "", 11sp Bold, RentoColors.primary,
             maxLines=1, overflow=Ellipsis)

    // Three-dot menu
    Box:
      RentoIconButton(RentoIcons.MoreVertical, 42.dp, onClick = { menuExpanded = true })
      DropdownMenu(expanded = menuExpanded, onDismissRequest = { menuExpanded = false },
                   Modifier.background(RentoColors.bg1)):
        DropdownMenuItem(
          text    = { Text("View ${if (chat?.referenceType == "listing") "Listing" else "Request"}", bodyM) },
          onClick = { /* navigate to detail */ menuExpanded = false },
        )
        DropdownMenuItem(
          text    = { Text("Delete Conversation", bodyM, RentoColors.red) },
          onClick = { onDeleteChat(); menuExpanded = false },
        )
        DropdownMenuItem(
          text    = { Text("Report User", bodyM) },
          onClick = { onReportUser(); menuExpanded = false },
        )
```

### Sub-tasks
- [ ] **M08-T12-A** Implement `ChatTopBar`.
- [ ] **M08-T12-B** `@Preview` dark + light, both referenceTypes.

---

## Task M08-T13 — Message Bubbles

**File:** `presentation/chat/detail/components/MessageBubble.kt`

### Sender Bubble (`.bbl.s` — current user)

```
Column(Modifier.fillMaxWidth(), horizontalAlignment=End):
  Box(
    Modifier
      .widthIn(max = LocalConfiguration.current.screenWidthDp.dp * 0.72f)
      .background(
        brush = Brush.linearGradient(
          colors = listOf(RentoColors.primary, RentoColors.secondary),
          start  = Offset(0f, 0f),
          end    = Offset(Float.POSITIVE_INFINITY, Float.POSITIVE_INFINITY),
        ),
        shape = RoundedCornerShape(topStart=20.dp, topEnd=20.dp, bottomStart=20.dp, bottomEnd=5.dp),
      )
      .padding(horizontal = 14.dp, vertical = 10.dp),
  ):
    Text(
      text   = if isDeleted "Message deleted" else message.contentDecrypted,
      style  = TextStyle(
        fontSize   = 13.5.sp,
        lineHeight = 13.5.sp * 1.55f,
        fontWeight = if isDeleted FontWeight.Normal else FontWeight.Medium,
        fontStyle  = if isDeleted FontStyle.Italic else FontStyle.Normal,
        color      = if isDeleted Color.White.copy(alpha=0.6f) else Color.White,
      ),
    )

  if (message.isEdited && !message.isDeleted):
    Text("Edited", 9sp, RentoColors.t3, modifier=Modifier.padding(top=2.dp))

  Text(formatMessageTime(message.createdAt), 10sp, RentoColors.t3, textAlign=End, top=4.dp)
```

### Receiver Bubble (`.bbl.r` — other user)

```
Column(Modifier.fillMaxWidth(), horizontalAlignment=Start):
  Box(
    Modifier
      .widthIn(max = 0.72f width)
      .background(
        color = RentoColors.bg3,
        shape = RoundedCornerShape(topStart=20.dp, topEnd=20.dp, bottomStart=5.dp, bottomEnd=20.dp),
      )
      .border(1.dp, RentoColors.border2,
              RoundedCornerShape(topStart=20.dp, topEnd=20.dp, bottomStart=5.dp, bottomEnd=20.dp))
      .padding(horizontal=14.dp, vertical=10.dp),
  ):
    Text(
      if isDeleted "Message deleted" else message.contentDecrypted,
      TextStyle(13.5.sp, 1.55 lineHeight,
        color  = if isDeleted RentoColors.t3 else RentoColors.t0,
        italic = isDeleted,
      ),
    )

  if message.isEdited && !message.isDeleted:
    Text("Edited", 9sp, RentoColors.t3, top=2.dp)

  Text(formatMessageTime(message.createdAt), 10sp, RentoColors.t3, top=4.dp)
```

### Date Separator

```kotlin
@Composable
fun DateSeparator(epochMs: Long) {
    val label = formatDateSeparator(epochMs)
    Box(
        Modifier.fillMaxWidth().padding(vertical = 12.dp),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            Modifier
                .background(MaterialTheme.rentoColors.bg2, RoundedCornerShape(100.dp))
                .border(1.dp, MaterialTheme.rentoColors.border, RoundedCornerShape(100.dp))
                .padding(vertical = 4.dp, horizontal = 12.dp),
        ) {
            Text(label, 11sp, MaterialTheme.rentoColors.t2)
        }
    }
}

private fun formatDateSeparator(epochMs: Long): String {
    val cal     = Calendar.getInstance().apply { timeInMillis = epochMs }
    val today   = Calendar.getInstance()
    val yesterday = Calendar.getInstance().apply { add(Calendar.DAY_OF_YEAR, -1) }
    return when {
        cal.isSameDay(today)     -> "Today"
        cal.isSameDay(yesterday) -> "Yesterday"
        else -> SimpleDateFormat("dd MMMM yyyy", Locale.getDefault()).format(Date(epochMs))
    }
}

private fun Calendar.isSameDay(other: Calendar): Boolean =
    get(Calendar.YEAR) == other.get(Calendar.YEAR) &&
    get(Calendar.DAY_OF_YEAR) == other.get(Calendar.DAY_OF_YEAR)
```

### Sub-tasks
- [ ] **M08-T13-A** Implement `MessageBubble` with both sender/receiver variants.
- [ ] **M08-T13-B** Deleted state: sender bubble shows `"Message deleted"` italic semi-transparent. Receiver bubble shows `"Message deleted"` italic `t3`.
- [ ] **M08-T13-C** Edited label (`"Edited"` 9sp) shown below bubble when `isEdited && !isDeleted`.
- [ ] **M08-T13-D** `@Preview` — sender sent, sender deleted, sender edited; receiver sent, receiver deleted; date separator; dark + light.

---

## Task M08-T14 — Input Bar

**File:** inline in `ChatDetailScreen.kt` as `InputBar` composable.

### Exact Specification

```
Column:
  // Edit mode indicator
  AnimatedVisibility(visible = isEditMode):
    Row(
      fillMaxWidth, CenterVertically,
      background = RentoColors.primaryTint,
      padding = 8.dp H / 6.dp V,
    ):
      Icon(RentoIcons.Edit, 14.dp, RentoColors.primary)
      Spacer(6.dp)
      Text("Editing message", 12sp Bold, RentoColors.primary, Modifier.weight(1f))
      Icon(RentoIcons.X, 16.dp, RentoColors.primary, modifier=Modifier.clickable { onCancelEdit() })

  Row(
    fillMaxWidth, CenterVertically,
    background = RentoColors.navBg,
    border top = 1.dp RentoColors.border,
    padding = 12.dp top / 16.dp H / 30.dp bottom,
    spacedBy = 10.dp,
  ):
    // Text input
    BasicTextField(
      value         = text,
      onValueChange = onTextChange,
      textStyle     = bodyM + RentoColors.t0,
      maxLines      = 4,
      modifier      = Modifier
        .weight(1f)
        .background(RentoColors.bg2, RoundedCornerShape(18.dp))
        .border(1.5.dp, RentoColors.border2, RoundedCornerShape(18.dp))
        .padding(vertical=12.dp, horizontal=15.dp),
      decorationBox = { inner ->
        if text.isEmpty(): Text("Type a message…", bodyM, RentoColors.t3)
        inner()
      },
    )

    // Send button
    Box(
      50.dp × 50.dp,
      background = Brush.linearGradient([RentoColors.primary, RentoColors.secondary]),
      shape = RoundedCornerShape(18.dp),
      clickable = { if !isSending onSend() },
      contentAlignment = Center,
    ):
      if isSending:
        CircularProgressIndicator(20.dp, Color.White, strokeWidth=2.dp)
      else:
        Icon(RentoIcons.Send, 22.dp, Color.White)
```

### Sub-tasks
- [ ] **M08-T14-A** Implement `InputBar` composable.
- [ ] **M08-T14-B** Edit mode indicator animates in/out with `AnimatedVisibility`.
- [ ] **M08-T14-C** Send button disabled (shows spinner) while `isSending = true`.

---

## Task M08-T15 — Long-Press Context Menu

**File:** `presentation/chat/detail/components/MessageContextMenu.kt`

```kotlin
@Composable
fun MessageContextMenu(
    offset: Offset,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onDismiss: () -> Unit,
) {
    Popup(
        alignment   = Alignment.TopStart,
        offset      = IntOffset(offset.x.roundToInt(), offset.y.roundToInt()),
        onDismissRequest = onDismiss,
    ) {
        Column(
            modifier = Modifier
                .background(MaterialTheme.rentoColors.bg1, RoundedCornerShape(14.dp))
                .border(1.dp, MaterialTheme.rentoColors.border, RoundedCornerShape(14.dp))
                .padding(vertical = 4.dp)
                .shadow(elevation = 8.dp, shape = RoundedCornerShape(14.dp)),
        ) {
            ContextMenuItem(icon = RentoIcons.Edit,   label = "Edit",   tint = MaterialTheme.rentoColors.t0, onClick = onEdit)
            HorizontalDivider(0.5.dp, MaterialTheme.rentoColors.border)
            ContextMenuItem(icon = RentoIcons.Trash,  label = "Delete", tint = MaterialTheme.rentoColors.red, onClick = onDelete)
        }
    }
}

@Composable
private fun ContextMenuItem(icon: ImageVector, label: String, tint: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Icon(icon, null, tint = tint, modifier = Modifier.size(18.dp))
        Text(label, style = MaterialTheme.rentoTypography.bodyM, color = tint)
    }
}
```

### Sub-tasks
- [ ] **M08-T15-A** Implement `MessageContextMenu` using `Popup`.
- [ ] **M08-T15-B** Context menu only shown for sender's own non-deleted messages — validated in `ChatDetailScreen`.

---

## Task M08-T16 — Delete Message Dialog

**File:** `presentation/chat/detail/components/DeleteMessageDialog.kt`

### Exact Specification (from Section 33.16)

Uses `GlassDialog` from Module 01:

```
Icon: RentoIcons.Chat in DarkRed circle (56dp)
Title: "Delete Message?"
Body: "This message will be replaced with "Message deleted" for both you and the recipient."
No acknowledge checkbox.
Buttons:
  GhostButton("Cancel", fillMaxWidth, onClick=onDismiss)
  Spacer(10.dp)
  DestructiveButton("Delete", fillMaxWidth, onClick=onConfirm)   // DarkRed fill

LOADING: inline spinner (no text — quick operation)
SUCCESS: auto-close (handled by ViewModel delay 800ms)
ERROR: inline error message + retry
```

### Sub-tasks
- [ ] **M08-T16-A** Implement `DeleteMessageDialog` using `GlassDialog`.
- [ ] **M08-T16-B** `@Preview` CONFIRM + LOADING + ERROR phases, dark + light.

---

## Task M08-T17 — Delete Conversation Dialog

**File:** `presentation/chat/detail/components/DeleteConversationDialog.kt`

### Exact Specification (from Section 33.17)

```
Icon: RentoIcons.Chat in DarkRed circle (56dp)
Title: "Delete Conversation?"
Body:
  "• All messages will be permanently erased for you
   • The other person will keep their copy
   • This cannot be undone."
No checkbox.
Buttons: GhostButton("Cancel") + DestructiveButton("Delete Conversation")
LOADING: "Deleting conversation…" + RentoDeleteSpinner
SUCCESS: navigate to Chat List (handled by ViewModel)
ERROR: inline error + retry
```

### Sub-tasks
- [ ] **M08-T17-A** Implement `DeleteConversationDialog` using `GlassDialog`.
- [ ] **M08-T17-B** `@Preview` all phases, dark + light.

---

## Task M08-T18 — Chat Upgrade Prompt

**File:** `presentation/chat/detail/components/ChatUpgradePrompt.kt`

### Exact Specification (from Section 32.9)

Non-dismissable by tapping outside. Only "Maybe Later" text link dismisses it.

```kotlin
@Composable
fun ChatUpgradePrompt(
    used: Int,
    limit: Int,
    onUpgrade: () -> Unit,
    onMaybeLater: () -> Unit,
)
```

```
GlassBottomSheet (non-dismissable — no drag handle, onDismissRequest = {}):
  Column(CenterHorizontally, padding=24.dp, spacedBy=0.dp):

    // Icon
    Box(72.dp circle, RentoColors.accentTint):
      Icon(RentoIcons.Package, 44.dp, RentoColors.accent)

    Spacer(16.dp)
    Text("Daily limit reached", Fraunces 24sp SemiBold, centred)
    Spacer(8.dp)
    Text(
      "You've used all $limit messages today.",
      14sp, RentoColors.t2, centred,
    )

    Spacer(20.dp)

    // Limit bar
    Box(
      fillMaxWidth,
      background = RentoColors.bg3,
      shape = RoundedCornerShape(14.dp),
      padding = 12.dp V / 14.dp H,
    ):
      Column:
        Row(fillMaxWidth, SpaceBetween):
          Text("Free plan", 13sp Bold, RentoColors.t0)
          Text("$used/$limit", 13sp Bold, RentoColors.red)
        Spacer(8.dp)
        LinearProgressIndicator(
          progress   = { 1f },       // always 100% — limit reached
          modifier   = Modifier.fillMaxWidth().height(6.dp).clip(RoundedCornerShape(3.dp)),
          color      = Color(0xFFE05555),   // DarkRed
          trackColor = RentoColors.bg4,
        )

    Spacer(20.dp)
    PrimaryButton("🚀 Upgrade Now", fillMaxWidth, onClick=onUpgrade)

    Spacer(12.dp)
    Text(
      "Maybe Later",
      13sp, RentoColors.t2, centred,
      modifier = Modifier.fillMaxWidth().clickable { onMaybeLater() },
    )

    Spacer(24.dp + navBarBottomPadding)
```

### Sub-tasks
- [ ] **M08-T18-A** Implement `ChatUpgradePrompt` using `GlassBottomSheet` with no dismissable scrim.
- [ ] **M08-T18-B** "Maybe Later" IS tappable even though sheet is otherwise non-dismissable.
- [ ] **M08-T18-C** `@Preview` dark + light.

---

## Task M08-T19 — Shimmer Loading States

### `ChatListShimmer` — `presentation/chat/list/components/ChatListShimmer.kt`

```kotlin
@Composable
fun ChatListShimmer() {
    Column {
        repeat(5) {
            Row(
                Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(Modifier.size(52.dp).clip(CircleShape).shimmer())
                Spacer(14.dp)
                Column(Modifier.weight(1f)) {
                    Box(Modifier.fillMaxWidth(0.6f).height(14.dp).shimmer())
                    Spacer(6.dp)
                    Box(Modifier.fillMaxWidth(0.4f).height(11.dp).shimmer())
                    Spacer(4.dp)
                    Box(Modifier.fillMaxWidth(0.8f).height(13.dp).shimmer())
                }
            }
            HorizontalDivider(1.dp, MaterialTheme.rentoColors.border)
        }
    }
}
```

### `ChatDetailShimmer` — `presentation/chat/detail/components/ChatDetailShimmer.kt`

```kotlin
@Composable
fun ChatDetailShimmer() {
    Column(
        Modifier.fillMaxSize().padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.Bottom,
    ) {
        repeat(4) { idx ->
            val isSender = idx % 2 == 0
            Row(
                Modifier.fillMaxWidth().padding(vertical = 4.dp),
                horizontalArrangement = if (isSender) Arrangement.End else Arrangement.Start,
            ) {
                Box(
                    Modifier
                        .fillMaxWidth(0.55f)
                        .height(40.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .shimmer()
                )
            }
        }
        Spacer(Modifier.height(12.dp))
    }
}
```

### Sub-tasks
- [ ] **M08-T19-A** Implement both shimmer components.
- [ ] **M08-T19-B** `@Preview` dark mode.

---

## Task M08-T20 — String Resources

```xml
<!-- ─── Chat List ────────────────────────────────────────────────────────── -->
<string name="chat_list_title">Messages</string>
<string name="chat_list_empty_title">No conversations yet</string>
<string name="chat_list_empty_subtitle">Your chats with hosts and seekers will appear here.</string>
<string name="chat_list_no_messages">No messages yet</string>
<string name="chat_time_now">now</string>

<!-- ─── Chat Detail ──────────────────────────────────────────────────────── -->
<string name="chat_input_placeholder">Type a message…</string>
<string name="chat_message_deleted">Message deleted</string>
<string name="chat_message_edited">Edited</string>
<string name="chat_editing_label">Editing message</string>
<string name="chat_view_listing">View Listing</string>
<string name="chat_view_request">View Request</string>
<string name="chat_delete_conversation_menu">Delete Conversation</string>
<string name="chat_report_user_menu">Report User</string>
<string name="chat_message_unavailable">Message unavailable</string>
<string name="chat_date_today">Today</string>
<string name="chat_date_yesterday">Yesterday</string>

<!-- ─── Delete Message Dialog ────────────────────────────────────────────── -->
<string name="chat_del_msg_title">Delete Message?</string>
<string name="chat_del_msg_body">This message will be replaced with "Message deleted" for both you and the recipient.</string>
<string name="chat_del_msg_confirm">Delete</string>
<string name="chat_del_msg_loading">Deleting…</string>
<string name="chat_del_msg_error">Could not delete. Try again.</string>

<!-- ─── Delete Conversation Dialog ──────────────────────────────────────── -->
<string name="chat_del_conv_title">Delete Conversation?</string>
<string name="chat_del_conv_bullet1">• All messages will be permanently erased for you</string>
<string name="chat_del_conv_bullet2">• The other person will keep their copy</string>
<string name="chat_del_conv_bullet3">• This cannot be undone.</string>
<string name="chat_del_conv_confirm">Delete Conversation</string>
<string name="chat_del_conv_loading">Deleting conversation…</string>
<string name="chat_del_conv_error">Could not delete. Try again.</string>

<!-- ─── Upgrade Prompt ──────────────────────────────────────────────────── -->
<string name="chat_limit_title">Daily limit reached</string>
<string name="chat_limit_subtitle">You\'ve used all %1$d messages today.</string>
<string name="chat_limit_plan">Free plan</string>
<string name="chat_limit_upgrade">🚀 Upgrade Now</string>
<string name="chat_limit_later">Maybe Later</string>

<!-- ─── Context Menu ─────────────────────────────────────────────────────── -->
<string name="chat_ctx_edit">Edit</string>
<string name="chat_ctx_delete">Delete</string>
```

---

## Task M08-T21 — Unit Tests

### `ChatCryptoTest.kt`
```
encrypt_then_decrypt_returnsOriginalPlaintext
decrypt_withWrongReferenceId_returnsNull
decrypt_withCorruptedData_returnsNull
generateSalt_returns16ByteBase64
encrypt_sameInput_producedDifferentCiphertext_eachCall
```

### `SendMessageUseCaseTest.kt`
```
invoke_belowLimit_sendsMessageAndIncrements
invoke_atLimit_returnsLimitReachedException_withoutSending
invoke_unauthenticated_returnsFailure
invoke_repositoryError_returnsFailure
```

### `CheckMessageLimitUseCaseTest.kt`
```
invoke_returnsDailyMessageCountWithLimitFromConfig
invoke_unauthenticated_returnsNull
```

### `ChatListViewModelTest.kt`
```
init_collectsChats
init_setsLoadingFalseOnFirstEmission
init_setsErrorOnFlow
```

### `ChatDetailViewModelTest.kt`
```
setInputText_updatesInputText
sendOrEdit_blankInput_doesNothing
sendOrEdit_callsSendMessage
sendOrEdit_limitReached_showsUpgradePrompt
startEditMessage_populatesInputWithContent
cancelEdit_clearsEditState
openDeleteMessageDialog_setsConfirmPhase
confirmDeleteMessage_success_autoDismisses
confirmDeleteMessage_failure_setsError
openDeleteConversationDialog_setsConfirmPhase
confirmDeleteConversation_success_setsNavigateBack
dismissUpgradePrompt_hidesSheet
```

### Sub-tasks
- [ ] **M08-T21-A** Implement all tests.
- [ ] **M08-T21-B** `ChatCryptoTest` tests actual cryptography — no mocking of JCE.
- [ ] **M08-T21-C** `./gradlew test` → all pass. Paste output.
- [ ] **M08-T21-D** `./gradlew koverReport` → ≥ 80% on `data.crypto` + `presentation.chat`. Paste summary.

---

## Task M08-T22 — Build Gate

- [ ] **M08-T22-A** `./gradlew lint` → zero new warnings.
- [ ] **M08-T22-B** `./gradlew detekt` → zero violations.
- [ ] **M08-T22-C** `./gradlew assembleDebug` → `BUILD SUCCESSFUL`. Paste output.
- [ ] **M08-T22-D** `./gradlew test` → all pass. Paste output.
- [ ] **M08-T22-E** `./gradlew koverReport` → ≥ 80%. Paste summary.
- [ ] **M08-T22-F** Update `ANDROID_PROGRESS.md`.
- [ ] **M08-T22-G** Create `CODE_REVIEW_MODULE_08.md`.

---

## 27. Journey Coverage Checklist

| Journey | Implementation | Status |
|---------|---------------|--------|
| Chat list — loads all conversations | `GetChatsUseCase` Flow → `ChatListViewModel` | ☐ |
| Chat list — shimmer shown while loading | `isLoading = true` → `ChatListShimmer()` | ☐ |
| Chat list — empty state when no chats | `chats.isEmpty()` | ☐ |
| Chat list — avatar with unread badge shown | `unreadCount > 0` | ☐ |
| Chat list — conversation preview shows decrypted last message | `lastMessageDecrypted` | ☐ |
| Chat list — tap conversation → navigates to Chat Detail | `onNavigateToChat(chat.id)` | ☐ |
| Chat detail — messages load via Firestore listener | `getMessages(chatId, salt)` Flow | ☐ |
| Chat detail — newest message always at bottom | `animateScrollToItem(lastIndex)` | ☐ |
| Chat detail — sender bubbles align right, receiver left | `isSender` conditional | ☐ |
| Chat detail — sender bubble gradient (primary→secondary) | `Brush.linearGradient` | ☐ |
| Chat detail — receiver bubble bg3 + border | `RentoColors.bg3` | ☐ |
| Chat detail — sender bottom-right corner 5dp, others 20dp | corner shape spec | ☐ |
| Chat detail — receiver bottom-left corner 5dp, others 20dp | corner shape spec | ☐ |
| Chat detail — date separator between different days | `!isSameDay()` check | ☐ |
| Chat detail — "Today" / "Yesterday" / full date labels | `formatDateSeparator()` | ☐ |
| Chat detail — type message in input bar | `BasicTextField` | ☐ |
| Chat detail — send button → message appears | `sendOrEdit()` | ☐ |
| Chat detail — sending spinner on send button | `isSending = true` | ☐ |
| Chat detail — daily limit reached → upgrade prompt shown | `LimitReachedException` | ☐ |
| Chat detail — upgrade prompt non-dismissable by outside tap | `GlassBottomSheet` no dismiss | ☐ |
| Chat detail — "Maybe Later" dismisses upgrade prompt | `onMaybeLater()` | ☐ |
| Chat detail — long-press own message → context menu | `onLongPress` callback | ☐ |
| Chat detail — context menu shows Edit + Delete | `MessageContextMenu` | ☐ |
| Chat detail — context menu NOT shown on deleted messages | `!message.isDeleted` guard | ☐ |
| Chat detail — context menu NOT shown on other user's messages | `senderUid == currentUid` guard | ☐ |
| Chat detail — "Edit" → input bar fills with message text | `startEditMessage()` | ☐ |
| Chat detail — edit mode indicator banner shown | `AnimatedVisibility(isEditMode)` | ☐ |
| Chat detail — × cancels edit mode | `cancelEdit()` | ☐ |
| Chat detail — edited message shows "Edited" label | `message.isEdited && !isDeleted` | ☐ |
| Chat detail — "Delete" → delete message dialog | `openDeleteMessageDialog()` | ☐ |
| Chat detail — delete message → bubble shows "Message deleted" italic | `isDeleted = true` | ☐ |
| Chat detail — three-dot menu → Delete Conversation | `openDeleteConversationDialog()` | ☐ |
| Chat detail — conversation deleted → navigate to chat list | `navigateBackToList = true` | ☐ |
| Chat detail — three-dot menu → Report User → ReportSheet | `openReportSheet()` | ☐ |
| Chat detail — report sheet targetLabel = "User" | `targetLabel = "User"` | ☐ |
| Encryption — messages stored as base64 in Firestore | `ChatCrypto.encrypt()` | ☐ |
| Encryption — messages decrypted before display | `ChatCrypto.decrypt()` | ☐ |
| Encryption — wrong key returns null (no crash) | `runCatching().getOrNull()` | ☐ |

---

## 28. CODE_REVIEW_MODULE_08.md Template

```markdown
# Code Review — Module 08: Chat
**Date:** YYYY-MM-DD
**Reviewer:** AI Agent (Automated)
**Branch:** feature/module-08-chat
**Spec version:** ANDROID_MODULE_08.md v1.0.0

---

## ✅ Encryption Verification
- [ ] `ChatCrypto` uses AES-256-GCM (`AES/GCM/NoPadding`)
- [ ] Key derivation uses `PBKDF2WithHmacSHA256`, 65536 iterations, 256-bit key
- [ ] UIDs sorted before concatenation — ensures same key for both participants
- [ ] IV is 12 bytes, generated fresh per `encrypt()` call (SecureRandom)
- [ ] IV prepended to ciphertext in output blob — extracted during `decrypt()`
- [ ] GCM tag length: 128 bits
- [ ] `decrypt()` returns `null` on any failure — no crash, no exception propagation
- [ ] `ChatCryptoTest` round-trip test passes
- [ ] `ChatCryptoTest` wrong-key test confirms null returned

---

## ✅ Architecture Compliance
- [ ] `ChatCrypto` in `data/crypto/` — not in domain
- [ ] Domain models have zero Android/Firebase imports
- [ ] `ChatRepository.getChats()` + `getMessages()` return `Flow` — not `suspend`
- [ ] `callbackFlow` used for Firestore listeners — `awaitClose` removes listener
- [ ] Daily limit checked in `SendMessageUseCase` — not in ViewModel
- [ ] `LimitReachedException` is a typed exception — ViewModel checks for it

---

## ✅ Security Checks
- [ ] No plaintext ever written to Firestore — all messages encrypted before `add()`/`update()`
- [ ] Decryption failure shows "Message unavailable" — no stack trace shown to user
- [ ] `deleteMessage` is a soft delete — only sets `isDeleted = true` + clears content
- [ ] `deleteConversation` uses `deletedFor` array — does NOT delete Firestore documents
- [ ] Salt stored in Firestore `chats/{chatId}.salt` — per-conversation

---

## ✅ Design Reference Verification

| Component | Spec Ref | Design Match |
|-----------|---------|-------------|
| Chat list title (Fraunces 28sp) | §15.3 | |
| Conversation row — avatar (52dp gradient) | §15.3 | |
| Unread badge (19dp DarkRed + 2.5dp bg0 ring) | §15.3 | |
| Top bar — avatar (42dp gradient) + name/listing | §15.4 | |
| Sender bubble — gradient, bottom-right 5dp | §15.4 | |
| Receiver bubble — bg3, border, bottom-left 5dp | §15.4 | |
| Date separator — pill centred | §15.4 | |
| Input bar — bg2 field + gradient send button (50dp) | §15.4 | |
| Upgrade prompt — 72dp accent circle icon | §32.9 | |
| Upgrade prompt — limit bar + DarkRed progress | §32.9 | |
| Delete message dialog — Chat icon DarkRed | §33.16 | |
| Delete conversation dialog — bullets | §33.17 | |

---

## ✅ Daily Limit Logic
- [ ] Free limit: `config/chat.freeUserDailyLimit` (default 20)
- [ ] Paid limit: `config/chat.paidUserDailyLimit` (default 100)
- [ ] Count tracked: `users/{uid}/dailyMessageCount/{YYYY-MM-DD}.count`
- [ ] Firestore transaction used for increment (race-condition safe)
- [ ] Limit checked BEFORE send — not after

---

## ✅ Code Quality
- [ ] All strings in `strings.xml`
- [ ] `./gradlew test` → ✅ PASSING
- [ ] `./gradlew assembleDebug` → ✅ PASSING
- [ ] `./gradlew detekt` → 0 violations
- [ ] `./gradlew koverReport` → ≥ 80%

---

## ⚠️ Lint Findings (Non-Blocking)
| ID | File | Line | Finding | Severity |
|----|------|------|---------|----------|

---

## 📝 Notes
```

---

*End of Module 08 — Chat v1.0.0*
*Depends on: Modules 01–07. Next module: Module 09 — Notifications (FCM).*
