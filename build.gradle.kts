plugins {
    java
    id("kr.junhyung.mainframe") version "0.0.1-SNAPSHOT" apply false
}

allprojects {
    group = "kr.junhyung"
    version = "1.0.0"
    description = "A masterpiece."

    pluginManager.apply(JavaPlugin::class)

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

    tasks.withType<AbstractArchiveTask> {
        archiveBaseName.set(rootProject.name)
    }

}