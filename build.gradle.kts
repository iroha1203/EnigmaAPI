val ktor_version: String by project
val kotlin_version: String by project
val logback_version: String by project

plugins {
  kotlin("jvm") version "1.9.10"
  id("io.ktor.plugin") version "2.3.4"
}

group = "app.enigma"
version = "0.0.1"

application {
  mainClass.set("app.enigma.ApplicationKt")

  val isDevelopment: Boolean = project.ext.has("development")
  applicationDefaultJvmArgs = listOf("-Dio.ktor.development=$isDevelopment")
}

repositories {
  mavenCentral()
}

dependencies {
  implementation("io.ktor:ktor-server-core-jvm")
  implementation("io.ktor:ktor-server-auth-jvm")
  implementation("io.ktor:ktor-server-auth-jwt-jvm")
  implementation("io.ktor:ktor-server-host-common-jvm")
  implementation("io.ktor:ktor-server-status-pages-jvm")
  implementation("io.ktor:ktor-server-call-logging-jvm")
  implementation("io.ktor:ktor-server-content-negotiation-jvm")
  implementation("io.ktor:ktor-serialization-gson-jvm")
  implementation("io.ktor:ktor-server-netty-jvm")
  implementation("ch.qos.logback:logback-classic:$logback_version")
  implementation("com.zaxxer:HikariCP:5.0.1")
  implementation("org.postgresql:postgresql:42.6.0")
  testImplementation("io.ktor:ktor-server-tests-jvm")
  testImplementation("org.jetbrains.kotlin:kotlin-test-junit:$kotlin_version")
}
