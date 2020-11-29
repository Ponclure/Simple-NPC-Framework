import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("com.github.johnrengelman.shadow") version "5.2.0"
}

tasks.named("shadowJar", ShadowJar::class) {
    archiveFileName.set("${project.name}-${project.version}.jar")

    exclude("examples/*.*")
}

dependencies {
    implementation(project(":SimpleNPCFramework-Internal"))
    implementation(project(":SimpleNPCFramework-NMS:SimpleNPCFramework-NMS-v1_15_R1"))
    implementation(project(":SimpleNPCFramework-NMS:SimpleNPCFramework-NMS-v1_16_R3"))
}
