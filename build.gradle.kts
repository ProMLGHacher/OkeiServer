val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project
val exposedVersion: String by project

plugins {
    application
    kotlin("jvm") version "1.6.0"
}

group = "ru.krea"
version = "0.0.1"
application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("io.ktor:ktor-server-core:$ktor_version")
    implementation("io.ktor:ktor-server-netty:$ktor_version")
    implementation("ch.qos.logback:logback-classic:$logback_version")
    testImplementation("io.ktor:ktor-server-tests:$ktor_version")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")

    //auth
    implementation("io.ktor:ktor-auth:$ktor_version")
    implementation("io.ktor:ktor-auth-jwt:$ktor_version")

    //DB
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-dao:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-jdbc:$exposedVersion")
    implementation("mysql:mysql-connector-java:8.0.25")

    //serialize
    implementation("io.ktor:ktor-gson:$ktor_version")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.1")

    implementation("org.apache.poi:poi:3.9")
    implementation("org.apache.poi:poi-ooxml:5.0.0")
    implementation("org.apache.xmlbeans:xmlbeans:2.3.0")

    implementation("com.github.doyaaaaaken:kotlin-csv-jvm:1.2.0")

}