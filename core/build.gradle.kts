plugins {
    id("kr.junhyung.mainframe")
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