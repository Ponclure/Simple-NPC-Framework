plugins {
    `java-library`
    `maven-publish`
}

defaultTasks("clean", "build", "SimpleNPCFramework-API:shadowJar", "publishToMavenLocal")

allprojects {
    project.group = "com.github.ponclure"
    project.version = "2.11-SNAPSHOT"
}

subprojects {
    apply(plugin = "java-library")
    apply(plugin = "maven-publish")

    project.java {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    project.publishing {
        publications {
            create("mavenJava", MavenPublication::class) {
                from(project.components["java"])
            }
        }
    }

    tasks.compileJava {
        options.encoding = "UTF-8"
    }

    repositories {
        mavenCentral()
        maven { url = uri("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") }
        maven { url = uri("https://libraries.minecraft.net") }
        mavenLocal()
    }
}
