plugins {
    java
    id("kr.junhyung.mainframe") version "0.0.1-SNAPSHOT" apply false
}

allprojects {
    group = "kr.junhyung"
    version = "1.0-SNAPSHOT"
    description = "A masterpiece."

    pluginManager.apply(JavaPlugin::class)

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }

}