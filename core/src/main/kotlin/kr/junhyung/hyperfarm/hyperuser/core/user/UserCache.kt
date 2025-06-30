package kr.junhyung.hyperfarm.hyperuser.core.user

import java.util.UUID

internal interface UserCache {

    suspend fun put(user: User)

    suspend fun findByMinecraftId(minecraftId: UUID): User?

    suspend fun findByMinecraftUsername(name: String): User?

    suspend fun findByName(name: String): User?

}