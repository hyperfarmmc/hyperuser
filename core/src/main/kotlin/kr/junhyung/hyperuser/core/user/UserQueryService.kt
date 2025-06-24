package kr.junhyung.hyperuser.core.user

import java.util.UUID

public interface UserQueryService {

    public suspend fun findByMinecraftId(minecraftId: UUID): User?

}