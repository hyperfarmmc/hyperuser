package kr.junhyung.hyperuser.velocity.user

import com.velocitypowered.api.event.Subscribe
import com.velocitypowered.api.event.connection.DisconnectEvent
import com.velocitypowered.api.event.connection.LoginEvent
import kr.junhyung.hyperuser.core.user.UserService
import kr.junhyung.mainframe.core.event.Listener
import org.slf4j.LoggerFactory

@Listener
class UserLogListener(
    private val userService: UserService
) {

    private val logger = LoggerFactory.getLogger(UserLogListener::class.java)

    @Subscribe
    suspend fun onLogin(event: LoginEvent) {
        val player = event.player
        val user = userService.findByMinecraftId(player.uniqueId)
        if (user == null) {
            logger.debug("Creating new user for player: {}", player.username)
            userService.new(player.uniqueId, player.username)
            return
        }
        if (user.minecraftUsername != player.username) {
            logger.debug("Updating Minecraft username for user {}: {} -> {}", user.id, user.minecraftUsername, player.username)
            userService.updateMinecraftUsername(user, player.username)
        }
        userService.updateLoginTime(user)
    }

    @Subscribe
    suspend fun onLogout(event: DisconnectEvent) {
        val player = event.player
        val user = userService.findByMinecraftId(player.uniqueId)
        if (user == null) {
            error("User not found for player: ${player.username}")
        }
        userService.updateLogoutTime(user)
    }

}