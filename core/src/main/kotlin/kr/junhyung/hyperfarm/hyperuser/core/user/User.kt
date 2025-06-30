package kr.junhyung.hyperfarm.hyperuser.core.user

import java.time.LocalDateTime
import java.util.UUID

public data class User(
    public val id: Int,
    public val minecraftId: UUID,
    public val minecraftUsername: String?,
    public val name: String?,
    public val discordId: String?,
    public val createdAt: LocalDateTime,

    public val lastLoginAt: LocalDateTime,
    public val lastLogoutAt: LocalDateTime?
) {

    public companion object {
        public val namePattern: Regex = Regex("^[가-힣0-9_]{1,15}\$")

        public fun new(
            minecraftId: UUID,
            minecraftUsername: String
        ): User {
            return User(
                id = 0,
                minecraftId = minecraftId,
                minecraftUsername = minecraftUsername,
                name = null,
                discordId = null,
                createdAt = LocalDateTime.now(),
                lastLoginAt = LocalDateTime.now(),
                lastLogoutAt = null,
            )
        }
    }

    init {
        if (name != null) {
            require(name.matches(namePattern)) { "Invalid name format: $name" }
        }
        if (discordId != null) {
            require(discordId.length in 17..18) { "Invalid Discord ID format: $discordId" }
        }
    }

    public fun isOnline(): Boolean {
        return lastLogoutAt == null || lastLoginAt.isAfter(lastLogoutAt)
    }

    public fun getDisplayName(fallback: String = "Unknown"): String {
        return name ?: minecraftUsername ?: fallback
    }

}