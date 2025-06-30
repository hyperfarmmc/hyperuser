package kr.junhyung.hyperfarm.hyperuser.core.user

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
                userRepository.updateMinecraftUsername(existingUserWithTheSameUsername, null)
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

    override suspend fun findByMinecraftIdNoCache(minecraftId: UUID): User? {
        return transactionManager.transactional {
            userRepository.findByMinecraftId(minecraftId)
        }
    }

    override suspend fun findByMinecraftUsername(name: String): User? {
        val cached = userCache.findByMinecraftUsername(name)
        if (cached != null) {
            logger.debug("Found user in cache: {}, minecraftUsername={}", cached, name)
            return cached
        }
        return transactionManager.transactional {
            val user = userRepository.findByMinecraftUsername(name) ?: return@transactional null
            withTransaction {
                cacheUser(user)
            }
            return@transactional user
        }
    }

    override suspend fun findByName(name: String): User? {
        val cached = userCache.findByName(name)
        if (cached != null) {
            logger.debug("Found user in cache: {}, name={}", cached, name)
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

    override suspend fun search(name: String): User? {
        return transactionManager.transactional {
            val findCachedByName = userCache.findByName(name)
            if (findCachedByName != null) {
                return@transactional findCachedByName
            }
            val findCachedByMinecraftUsername = userCache.findByMinecraftUsername(name)
            if (findCachedByMinecraftUsername != null) {
                return@transactional findCachedByMinecraftUsername
            }
            val findPersistedByName = userRepository.findByName(name)
            if (findPersistedByName != null) {
                withTransaction {
                    cacheUser(findPersistedByName)
                }
                return@transactional findPersistedByName
            }
            val findPersistedByMinecraftUsername = userRepository.findByMinecraftUsername(name)
            if (findPersistedByMinecraftUsername != null) {
                withTransaction {
                    cacheUser(findPersistedByMinecraftUsername)
                }
                return@transactional findPersistedByMinecraftUsername
            }
            return@transactional null
        }
    }

    override suspend fun updateName(user: User, name: String): User {
        return transactionManager.transactional {
            userRepository.updateName(user, name)
            val updatedUser = user.copy(name = name)
            withTransaction {
                cacheUser(updatedUser)
            }
            return@transactional updatedUser
        }
    }

    override suspend fun updateMinecraftUsername(user: User, minecraftUsername: String): User {
        return transactionManager.transactional {
            userRepository.updateMinecraftUsername(user, minecraftUsername)
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
            userRepository.updateLastLoginAt(user, now)
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
            userRepository.updateLastLogoutAt(user, now)
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