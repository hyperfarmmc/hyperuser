package kr.junhyung.hyperuser.core.user

import java.time.LocalDateTime
import java.util.UUID

internal interface UserRepository {

    fun save(user: User): User

    fun findById(id: Int): User?

    fun findByMinecraftId(minecraftId: UUID): User?

    fun findByName(name: String): User?

    fun findByDiscordId(discordId: String): User?

    fun updateDiscordIdById(userId: Int, discordId: String): Int

    fun updateMinecraftUsernameById(userId: Int, minecraftUsername: String?): Int

    fun updateLastLoginAtById(userId: Int, lastLoginAt: LocalDateTime): Int

    fun updateLastLogoutAtById(userId: Int, lastLogoutAt: LocalDateTime): Int

    fun updateNameById(userId: Int, name: String?): Int

}