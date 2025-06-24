package kr.junhyung.hyperuser.core.user

import kr.junhyung.mainframe.core.transaction.TransactionManager
import org.springframework.stereotype.Service
import java.util.UUID

@Service
internal class UserQueryServiceImpl(
    private val transactionManager: TransactionManager,
    private val userRepository: UserRepository,
) : UserQueryService {

    override suspend fun findByMinecraftId(minecraftId: UUID): User? {

        return transactionManager.transactional {
            val user = userRepository.findByMinecraftId(minecraftId) ?: return@transactional null
            return@transactional user
        }
    }

}