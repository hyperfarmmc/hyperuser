package kr.junhyung.hyperfarm.hyperuser.core.user

import java.time.LocalDateTime
import java.util.*

internal interface UserRepository {

    fun save(user: User): User

    fun findByMinecraftId(minecraftId: UUID): User?

    fun findByMinecraftUsername(name: String): User?

    fun findByName(name: String): User?

    fun findByDiscordId(discordId: String): User?

    fun updateDiscordId(user: User, discordId: String): Int

    fun updateMinecraftUsername(user: User, minecraftUsername: String?): Int

    fun updateLastLoginAt(user: User, lastLoginAt: LocalDateTime): Int

    fun updateLastLogoutAt(user: User, lastLogoutAt: LocalDateTime): Int

    fun updateName(user: User, name: String?): Int

}