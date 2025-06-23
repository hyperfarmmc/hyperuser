plugins {
    id("kr.junhyung.mainframe")
    `maven-publish`
}

kotlin {
    explicitApi()
}

dependencies {
    implementation(mainframe.core)
    implementation(mainframe.exposed)
    implementation("com.mysql:mysql-connector-j")

    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
}

publishing {
    repositories {
        maven {
            name = "nexus"
            if (project.version.toString().endsWith("-SNAPSHOT")) {
                url = uri("https://nexus.junhyung.kr/repository/maven-snapshots/")
            } else {
                url = uri("https://nexus.junhyung.kr/repository/maven-releases/")
            }
            credentials {
                username = System.getenv("NEXUS_USERNAME").toString()
                password = System.getenv("NEXUS_PASSWORD").toString()
            }
        }
    }
    publications {
        create<MavenPublication>("default") {
            group = "kr.junhyung"
            artifactId = "hyperuser"
            version = project.version.toString()
            from(components["java"])
        }
    }
}