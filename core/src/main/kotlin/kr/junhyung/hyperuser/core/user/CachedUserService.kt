package kr.junhyung.hyperuser.core.user

import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Service
import java.util.UUID

@Primary
@Service
internal class CachedUserService(
    private val delegate: TxUserService,
    private val userCache: UserCache
) : UserService {

    private val logger = LoggerFactory.getLogger(CachedUserService::class.java)

    override suspend fun new(
        minecraftId: UUID,
        minecraftUsername: String
    ): User {
        val newUser = delegate.new(minecraftId, minecraftUsername)
        cacheUser(newUser)
        return newUser
    }

    override suspend fun findByMinecraftId(minecraftId: UUID): User? {
        val cached = userCache.findByMinecraftId(minecraftId)
        if (cached != null) {
            logger.debug("Cache hit: {}, minecraftId={}", cached, minecraftId)
            return cached
        }
        return delegate.findByMinecraftId(minecraftId)
    }

    override suspend fun findByMinecraftUsername(name: String): User? {
        val cached = userCache.findByMinecraftUsername(name)
        if (cached != null) {
            logger.debug("Cache hit: {}, minecraftUsername={}", cached, name)
            return cached
        }
        return delegate.findByMinecraftUsername(name)
    }

    override suspend fun findByName(name: String): User? {
        val cached = userCache.findByName(name)
        if (cached != null) {
            logger.debug("Cache hit: {}, name={}", cached, name)
            return cached
        }
        return delegate.findByName(name)
    }

    override suspend fun updateMinecraftUsername(
        user: User,
        minecraftUsername: String
    ): User {
        val updatedUser = delegate.updateMinecraftUsername(user, minecraftUsername)
        cacheUser(updatedUser)
        return updatedUser
    }

    override suspend fun updateName(
        user: User,
        name: String
    ): User {
        val updatedUser = delegate.updateName(user, name)
        cacheUser(updatedUser)
        return updatedUser
    }

    override suspend fun updateLoginTime(user: User): User {
        val updatedUser = delegate.updateLoginTime(user)
        cacheUser(updatedUser)
        return updatedUser
    }

    override suspend fun updateLogoutTime(user: User): User {
        val updatedUser = delegate.updateLogoutTime(user)
        cacheUser(updatedUser)
        return updatedUser
    }

    private suspend fun cacheUser(user: User) {
        try {
            userCache.put(user)
            logger.debug("Cached user: {}", user)
        } catch (exception: Exception) {
            logger.error("Failed to cache user: $user", exception)
        }
    }

}