package com.dastageer.rento.domain.model

sealed class RentoAuthException(message: String) : Exception(message) {
    class InvalidEmail(msg: String = "Invalid email address.") : RentoAuthException(msg)
    class WrongPassword(msg: String = "Incorrect password.") : RentoAuthException(msg)
    class UserNotFound(msg: String = "No account found with this email.") : RentoAuthException(msg)
    class EmailAlreadyInUse(msg: String = "An account already exists with this email.") : RentoAuthException(msg)
    class WeakPassword(msg: String = "Password must be at least 8 characters.") : RentoAuthException(msg)
    class TooManyRequests(msg: String = "Too many attempts. Please wait and try again.") : RentoAuthException(msg)
    class NetworkError(msg: String = "Network error. Please check your connection.") : RentoAuthException(msg)
    class GooglePlayServicesUnavailable(msg: String = "Google Play Services unavailable.") : RentoAuthException(msg)
    class Unknown(msg: String = "An unexpected error occurred. Please try again.") : RentoAuthException(msg)
}
