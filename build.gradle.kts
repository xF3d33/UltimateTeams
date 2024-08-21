plugins {
    kotlin("jvm") version "1.8.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("java")
}

group = "dev.xf3d3"
version = "2.0.5"

repositories {
    mavenLocal()
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.william278.net/releases")
    maven("https://repo.opencollab.dev/main/")
    maven("https://mvn-repo.arim.space/lesser-gpl3/")
    maven("https://repo.aikar.co/content/groups/aikar/")
}

tasks {
    javadoc {
        options.encoding = "UTF-8"
    }
    compileJava {
        options.encoding = "UTF-8"
    }
    compileTestJava {
        options.encoding = "UTF-8"
    }
}

dependencies {
    // Kotlin
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib:1.8.0")

    // Folia and Spigot
    compileOnly("dev.folia:folia-api:1.20.1-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.3")
    compileOnly("org.geysermc.floodgate:api:2.2.0-SNAPSHOT")
    implementation("org.bstats:bstats-bukkit:3.0.2")
    implementation("dev.triumphteam:triumph-gui:3.1.5")
    
    // PaperLib
    implementation("io.papermc:paperlib:1.0.8")
    implementation("space.arim.morepaperlib:morepaperlib:0.4.2")

    // Libs
    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("dev.dejvokep:boosted-yaml:1.3.1")

    // Database
    implementation("com.j256.ormlite:ormlite-jdbc:6.1")
    implementation("org.mariadb.jdbc:mariadb-java-client:3.0.3")

    implementation("net.william278:annotaml:2.0.2")
    implementation("net.william278:DesertWell:2.0.4")
    compileOnly("net.william278:huskhomes:4.4")
}

tasks {
    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        archiveFileName.set("${rootProject.name}-${rootProject.version}.jar")
        archiveClassifier.set("main")
    }
}