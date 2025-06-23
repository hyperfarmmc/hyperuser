package kr.junhyung.hyperuser.velocity

import kr.junhyung.hyperuser.core.HyperuserCoreModule
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.Import

@Import(HyperuserCoreModule::class)
@SpringBootApplication
class HyperuserVelocityApplication