package kr.junhyung.hyperuser.core.user

import java.util.UUID

public interface UserService {

    public suspend fun new(minecraftId: UUID, minecraftUsername: String): User

    public suspend fun findByMinecraftId(minecraftId: UUID): User?

    public suspend fun findByMinecraftUsername(name: String): User?

    public suspend fun findByName(name: String): User?

    public suspend fun updateMinecraftUsername(user: User, minecraftUsername: String): User

    public suspend fun updateName(user: User, name: String): User

    public suspend fun updateLoginTime(user: User): User

    public suspend fun updateLogoutTime(user: User): User

}