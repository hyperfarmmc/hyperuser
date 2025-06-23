package kr.junhyung.hyperuser.core.user.redis

import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kr.junhyung.hyperuser.core.user.User
import kr.junhyung.hyperuser.core.user.UserCache
import kr.junhyung.mainframe.core.util.StringKeyProvider
import kr.junhyung.mainframe.core.util.UUIDKeyProvider
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.core.getAndAwait
import org.springframework.data.redis.core.setAndAwait
import org.springframework.stereotype.Component
import java.util.UUID

@Component
internal class RedisUserCache(
    reactiveUserRedisTemplate: ReactiveRedisTemplate<String, User>,
) : UserCache {

    private val valueOps = reactiveUserRedisTemplate.opsForValue()

    private val keyByUUID = UUIDKeyProvider("user:uuid:")
    private val keyByName = StringKeyProvider("user:name:")

    override suspend fun put(user: User) {
        coroutineScope {
            val cacheByIdJob = launch {
                valueOps.setAndAwait(keyByUUID.getKey(user.minecraftId), user)
            }
            if (user.minecraftUsername != null) {
                val cacheByUsernameJob = launch {
                    valueOps.setAndAwait(keyByName.getKey(user.minecraftUsername), user)
                }
                joinAll(cacheByIdJob, cacheByUsernameJob)
            } else {
                cacheByIdJob.join()
            }
        }
    }

    override suspend fun findByMinecraftId(minecraftId: UUID): User? {
        return valueOps.getAndAwait(keyByUUID.getKey(minecraftId))
    }

    override suspend fun findByMinecraftUsername(name: String): User? {
        return valueOps.getAndAwait(keyByName.getKey(name))
    }

}