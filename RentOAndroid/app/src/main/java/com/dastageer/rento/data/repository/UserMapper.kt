package com.dastageer.rento.data.repository

import com.dastageer.rento.data.model.UserDto
import com.dastageer.rento.domain.model.AccountType
import com.dastageer.rento.domain.model.User
import com.dastageer.rento.domain.model.UserMode
import com.google.firebase.firestore.DocumentSnapshot

fun User.toDto(): UserDto = UserDto(
    uid = uid,
    name = name,
    email = email,
    phone = phone,
    photoUrl = photoUrl,
    emailVerified = emailVerified,
    isBlocked = isBlocked,
    blockReason = blockReason,
    accountType = accountType.name.lowercase(),
    defaultMode = defaultMode.name.lowercase(),
    onboardingComplete = onboardingComplete,
    province = province,
    city = city,
    deviceLat = deviceLat,
    deviceLng = deviceLng,
    referralSource = referralSource,
    currentPackageId = currentPackageId,
    packageName = packageName,
    createdAt = com.google.firebase.Timestamp(createdAt / 1000, ((createdAt % 1000) * 1000000).toInt())
)

fun DocumentSnapshot.toUser(): User? {
    val dto = toObject(UserDto::class.java) ?: return null
    return User(
        uid = dto.uid,
        name = dto.name,
        email = dto.email,
        phone = dto.phone,
        photoUrl = dto.photoUrl,
        emailVerified = dto.emailVerified,
        isBlocked = dto.isBlocked,
        blockReason = dto.blockReason,
        accountType = runCatching { AccountType.valueOf(dto.accountType.uppercase()) }.getOrDefault(AccountType.INDIVIDUAL),
        defaultMode = runCatching { UserMode.valueOf(dto.defaultMode.uppercase()) }.getOrDefault(UserMode.LOOKING),
        onboardingComplete = dto.onboardingComplete,
        province = dto.province,
        city = dto.city,
        deviceLat = dto.deviceLat,
        deviceLng = dto.deviceLng,
        referralSource = dto.referralSource,
        currentPackageId = dto.currentPackageId,
        packageName = dto.packageName,
        createdAt = dto.createdAt.toDate().time,
    )
}
