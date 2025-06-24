package kr.junhyung.hyperuser.core.user

import kr.junhyung.mainframe.core.transaction.TransactionManager
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Qualifier
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.*

@Qualifier("txUserService")
@Service
internal class TxUserService(
    private val transactionManager: TransactionManager,
    private val userRepository: UserRepository,
) : UserService {

    private val logger = LoggerFactory.getLogger(TxUserService::class.java)

    override suspend fun new(minecraftId: UUID, minecraftUsername: String): User {
        return transactionManager.transactional {
            val existingUserWithTheSameUsername = userRepository.findByName(minecraftUsername)
            if (existingUserWithTheSameUsername != null) {
                logger.debug("User with name $minecraftUsername already exists. overriding with new user.")
                userRepository.updateMinecraftUsername(existingUserWithTheSameUsername, null)
            }
            val user = User.new(minecraftId, minecraftUsername)
            userRepository.save(user)
        }
    }

    override suspend fun findByMinecraftId(minecraftId: UUID): User? {
        return transactionManager.transactional {
            userRepository.findByMinecraftId(minecraftId)
        }
    }

    override suspend fun findByMinecraftUsername(name: String): User? {
        return transactionManager.transactional {
            userRepository.findByName(name) ?: return@transactional null
        }
    }

    override suspend fun updateName(user: User, name: String): User {
        return transactionManager.transactional {
            userRepository.updateName(user, name)
            user.copy(name = name)
        }
    }

    override suspend fun updateMinecraftUsername(user: User, minecraftUsername: String): User {
        return transactionManager.transactional {
            userRepository.updateMinecraftUsername(user, minecraftUsername)
            user.copy(minecraftUsername = minecraftUsername)
        }
    }

    override suspend fun updateLoginTime(user: User): User {
        return transactionManager.transactional {
            val now = LocalDateTime.now()
            userRepository.updateLastLoginAt(user, now)
            user.copy(lastLoginAt = now)
        }
    }

    override suspend fun updateLogoutTime(user: User): User {
        return transactionManager.transactional {
            val now = LocalDateTime.now()
            userRepository.updateLastLogoutAt(user, now)
            user.copy(lastLogoutAt = now)
        }
    }

}