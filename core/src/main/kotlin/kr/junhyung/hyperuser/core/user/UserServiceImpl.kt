package kr.junhyung.hyperuser.core.user

import kr.junhyung.mainframe.core.transaction.TransactionManager
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Service
internal class UserServiceImpl(
    private val transactionManager: TransactionManager,
    private val userRepository: UserRepository,
    private val userCache: UserCache
) : UserService {

    private val logger = LoggerFactory.getLogger(UserServiceImpl::class.java)

    override suspend fun new(minecraftId: UUID, minecraftUsername: String): User {
        return transactionManager.transactional {
            val existingUserWithTheSameUsername = userRepository.findByName(minecraftUsername)
            if (existingUserWithTheSameUsername != null) {
                logger.debug("User with name $minecraftUsername already exists. overriding with new user.")
                userRepository.updateMinecraftUsernameById(existingUserWithTheSameUsername.id, null)
            }
            val user = User.new(minecraftId, minecraftUsername)
            val saved = userRepository.save(user)
            withTransaction {
                cacheUser(saved)
            }
            return@transactional saved
        }
    }

    override suspend fun findByMinecraftId(minecraftId: UUID): User? {
        val cached = userCache.findByMinecraftId(minecraftId)
        if (cached != null) {
            logger.debug("Found user in cache: {}, minecraftId={}", cached, minecraftId)
            return cached
        }

        return transactionManager.transactional {
            val user = userRepository.findByMinecraftId(minecraftId) ?: return@transactional null
            withTransaction {
                cacheUser(user)
            }
            return@transactional user
        }
    }

    override suspend fun findByMinecraftUsername(name: String): User? {
        val cached = userCache.findByMinecraftUsername(name)
        if (cached != null) {
            logger.debug("Found user in cache: {}, minecraftUsername={}", cached, name)
            return cached
        }
        return transactionManager.transactional {
            val user = userRepository.findByName(name) ?: return@transactional null
            withTransaction {
                cacheUser(user)
            }
            return@transactional user
        }
    }

    override suspend fun updateName(user: User, name: String): User {
        return transactionManager.transactional {
            userRepository.updateNameById(user.id, name)
            val updatedUser = user.copy(name = name)
            withTransaction {
                cacheUser(updatedUser)
            }
            return@transactional updatedUser
        }
    }

    override suspend fun updateMinecraftUsername(user: User, minecraftUsername: String): User {
        return transactionManager.transactional {
            userRepository.updateMinecraftUsernameById(user.id, minecraftUsername)
            val updatedUser = user.copy(minecraftUsername = minecraftUsername)
            withTransaction {
                cacheUser(updatedUser)
            }
            return@transactional updatedUser
        }
    }

    override suspend fun updateLoginTime(user: User): User {
        return transactionManager.transactional {
            val now = LocalDateTime.now()
            userRepository.updateLastLoginAtById(user.id, now)
            val updatedUser = user.copy(lastLoginAt = now)
            withTransaction {
                cacheUser(updatedUser)
            }
            return@transactional updatedUser
        }
    }

    override suspend fun updateLogoutTime(user: User): User {
        return transactionManager.transactional {
            val now = LocalDateTime.now()
            userRepository.updateLastLogoutAtById(user.id, now)
            val updatedUser = user.copy(lastLogoutAt = now)
            withTransaction {
                cacheUser(updatedUser)
            }
            return@transactional updatedUser
        }
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