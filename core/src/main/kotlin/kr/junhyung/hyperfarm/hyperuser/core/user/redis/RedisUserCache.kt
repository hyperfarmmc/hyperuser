package kr.junhyung.hyperfarm.hyperuser.core.user.redis

import kotlinx.coroutines.Job
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kr.junhyung.hyperfarm.hyperuser.core.user.User
import kr.junhyung.hyperfarm.hyperuser.core.user.UserCache
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

    private val keyByMinecraftId = UUIDKeyProvider("hyperuser:user:uuid:")
    private val keyByMinecraftUsername = StringKeyProvider("hyperuser:user:minecraft_username:")
    private val keyByName = StringKeyProvider("hyperuser:user:name:")

    override suspend fun put(user: User) {
        coroutineScope {
            val jobs = mutableListOf<Job>()
            val cacheByIdJob = launch {
                valueOps.setAndAwait(keyByMinecraftId.getKey(user.minecraftId), user)
            }
            jobs.add(cacheByIdJob)
            if (user.minecraftUsername != null) {
                val cacheByUsernameJob = launch {
                    valueOps.setAndAwait(keyByMinecraftUsername.getKey(user.minecraftUsername), user)
                }
                jobs.add(cacheByUsernameJob)
            }
            if (user.name != null) {
                val cacheByNameJob = launch {
                    valueOps.setAndAwait(keyByName.getKey(user.name), user)
                }
                jobs.add(cacheByNameJob)
            }
            jobs.joinAll()
        }
    }

    override suspend fun findByMinecraftId(minecraftId: UUID): User? {
        return valueOps.getAndAwait(keyByMinecraftId.getKey(minecraftId))
    }

    override suspend fun findByMinecraftUsername(name: String): User? {
        return valueOps.getAndAwait(keyByMinecraftUsername.getKey(name))
    }

    override suspend fun findByName(name: String): User? {
        return valueOps.getAndAwait(keyByName.getKey(name))
    }

}