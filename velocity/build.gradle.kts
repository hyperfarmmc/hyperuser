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