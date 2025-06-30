package kr.junhyung.hyperfarm.hyperuser.core.user.redis

import com.fasterxml.jackson.databind.ObjectMapper
import kr.junhyung.hyperfarm.hyperuser.core.user.User
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
internal class RedisUserConfiguration {

    @Bean
    fun reactiveUserRedisTemplate(
        reactiveRedisConnectionFactory: ReactiveRedisConnectionFactory,
        objectMapper: ObjectMapper
    ): ReactiveRedisTemplate<String, User> {
        val stringSerializer = StringRedisSerializer()
        val jacksonSerializer = Jackson2JsonRedisSerializer(objectMapper, User::class.java)
        val context = RedisSerializationContext
            .newSerializationContext<String, User>()
            .key(stringSerializer)
            .value(jacksonSerializer)
            .hashKey(stringSerializer)
            .hashValue(jacksonSerializer)
            .build()
        return ReactiveRedisTemplate(reactiveRedisConnectionFactory, context)
    }

}