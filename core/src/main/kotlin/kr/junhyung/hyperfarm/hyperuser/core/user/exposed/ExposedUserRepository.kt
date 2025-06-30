package kr.junhyung.hyperfarm.hyperuser.core.user.exposed

import kr.junhyung.hyperfarm.hyperuser.core.user.User
import kr.junhyung.hyperfarm.hyperuser.core.user.UserRepository
import org.jetbrains.exposed.sql.ResultRow
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.update
import org.springframework.stereotype.Repository
import java.time.LocalDateTime
import java.util.UUID

@Repository
internal class ExposedUserRepository : UserRepository {

    override fun save(user: User): User {
        require(user.id == 0) { "User ID must be 0 for new users" }
        val id = ExposedUserTable.insertAndGetId {
            it[minecraftId] = user.minecraftId
            it[minecraftUsername] = user.minecraftUsername
            it[name] = user.name
            it[discordId] = user.discordId
            it[createdAt] = user.createdAt
            it[lastLoginAt] = user.lastLoginAt
            it[lastLogoutAt] = user.lastLogoutAt
        }
        return user.copy(id = id.value)
    }

    override fun findByMinecraftId(minecraftId: UUID): User? {
        return ExposedUserTable.selectAll()
            .where { ExposedUserTable.minecraftId eq minecraftId }
            .map(::wrapRow)
            .firstOrNull()
    }

    override fun findByMinecraftUsername(name: String): User? {
        return ExposedUserTable.selectAll()
            .where { ExposedUserTable.minecraftUsername eq name }
            .map(::wrapRow)
            .firstOrNull()
    }

    override fun findByName(name: String): User? {
        return ExposedUserTable.selectAll()
            .where { ExposedUserTable.name eq name }
            .map(::wrapRow)
            .firstOrNull()
    }

    override fun findByDiscordId(discordId: String): User? {
        return ExposedUserTable.selectAll()
            .where { ExposedUserTable.discordId eq discordId }
            .map(::wrapRow)
            .firstOrNull()
    }

    override fun updateDiscordId(user: User, discordId: String): Int {
        return ExposedUserTable.update({ ExposedUserTable.id eq user.id }) {
            it[ExposedUserTable.discordId] = discordId
            it[ExposedUserTable.updatedAt] = LocalDateTime.now()
        }
    }

    override fun updateMinecraftUsername(user: User, minecraftUsername: String?): Int {
        return ExposedUserTable.update({ ExposedUserTable.id eq user.id }) {
            it[ExposedUserTable.minecraftUsername] = minecraftUsername
            it[ExposedUserTable.updatedAt] = LocalDateTime.now()
        }
    }

    override fun updateLastLoginAt(user: User, lastLoginAt: LocalDateTime): Int {
        return ExposedUserTable.update({ ExposedUserTable.id eq user.id }) {
            it[ExposedUserTable.lastLoginAt] = lastLoginAt
            it[ExposedUserTable.updatedAt] = LocalDateTime.now()
        }
    }

    override fun updateLastLogoutAt(user: User, lastLogoutAt: LocalDateTime): Int {
        return ExposedUserTable.update({ ExposedUserTable.id eq user.id }) {
            it[ExposedUserTable.lastLogoutAt] = lastLogoutAt
            it[ExposedUserTable.updatedAt] = LocalDateTime.now()
        }
    }

    override fun updateName(user: User, name: String?): Int {
        return ExposedUserTable.update({ ExposedUserTable.id eq user.id }) {
            it[ExposedUserTable.name] = name
            it[ExposedUserTable.updatedAt] = LocalDateTime.now()
        }
    }

    private fun wrapRow(row: ResultRow): User {
        return User(
            id = row[ExposedUserTable.id].value,
            minecraftId = row[ExposedUserTable.minecraftId],
            minecraftUsername = row[ExposedUserTable.minecraftUsername],
            name = row[ExposedUserTable.name],
            discordId = row[ExposedUserTable.discordId],
            createdAt = row[ExposedUserTable.createdAt],
            lastLoginAt = row[ExposedUserTable.lastLoginAt],
            lastLogoutAt = row[ExposedUserTable.lastLogoutAt]
        )
    }

}