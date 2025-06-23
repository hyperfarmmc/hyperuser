package kr.junhyung.hyperuser.core.user.exposed

import kr.junhyung.mainframe.exposed.table.Table
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.datetime
import java.time.LocalDateTime

@Table
internal object ExposedUserTable : IntIdTable("user") {

    val minecraftId = uuid("minecraft_id").uniqueIndex()
    val minecraftUsername = varchar("minecraft_username", 16).nullable().uniqueIndex()
    val name = varchar("name", 16).nullable().uniqueIndex()
    val discordId = varchar("discord_id", 18).nullable().uniqueIndex()
    val createdAt = datetime("created_at").clientDefault { LocalDateTime.now() }

    val lastLoginAt = datetime("last_login_at").clientDefault { LocalDateTime.now() }
    val lastLogoutAt = datetime("last_logout_at").nullable()

    val updatedAt = datetime("updated_at").nullable()

}