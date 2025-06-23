import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("kr.junhyung.mainframe")
}

mainframe {
    shadowJar()
}

dependencies {
    implementation(projects.core)

    implementation(mainframe.velocity)
}

tasks.processResources {
    filesMatching("velocity-plugin.json") {
        expand("project" to project)
    }
}

tasks.withType<ShadowJar> {
    relocate("io.netty.resolver.dns", "kr.junhyung.hyperuser.shadow.io.netty.resolver.dns")
}