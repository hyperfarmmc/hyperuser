rootProject.name = "hyperuser"

include(":core")
include(":velocity")

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    repositories {
        maven("https://junhyung.nexus/")
    }
}

pluginManagement {
    repositories {
        maven("https://junhyung.nexus/")
    }
    plugins {
        id("org.gradle.toolchains.foojay-resolver-convention") version "0.9.0"
    }
}

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")