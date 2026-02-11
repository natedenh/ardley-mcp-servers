plugins {
    java
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
}

allprojects {
    group = "com.ardley.mcp"
    version = "1.0.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

subprojects {
    apply(plugin = "java")

    java {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    val jacksonVersion = "2.16.1"
    val okhttpVersion = "4.12.0"
    val slf4jVersion = "2.0.11"
    val logbackVersion = "1.4.14"

    dependencies {
        implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
        implementation("com.squareup.okhttp3:okhttp:$okhttpVersion")
        implementation("org.slf4j:slf4j-api:$slf4jVersion")
        implementation("ch.qos.logback:logback-classic:$logbackVersion")
    }
}
