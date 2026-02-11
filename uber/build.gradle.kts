plugins {
    id("com.github.johnrengelman.shadow")
}

dependencies {
    implementation(project(":core"))
}

tasks.shadowJar {
    archiveClassifier.set("")
    manifest {
        attributes("Main-Class" to "com.ardley.mcp.uber.UberMcpServer")
    }
    mergeServiceFiles()
}

tasks.build {
    dependsOn(tasks.shadowJar)
}
