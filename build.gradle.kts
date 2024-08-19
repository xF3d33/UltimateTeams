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
    compileOnly("dev.folia:folia-api:1.20.1-R0.1-SNAPSHOT")
    compileOnly("io.papermc:paperlib:1.0.8")
    compileOnly("com.fatboyindustrial.gson-javatime-serialisers:gson-javatime-serialisers:1.1.2")
    compileOnly("com.google.code.gson:gson:2.10.1")
    compileOnly("space.arim.morepaperlib:morepaperlib:0.4.2")
    compileOnly("dev.triumphteam:triumph-gui:3.1.5")
    compileOnly("org.apache.commons:commons-lang3:3.16.0")
    compileOnly("dev.dejvokep:boosted-yaml:1.3.1")
    compileOnly("org.bstats:bstats-bukkit:3.0.2")
    compileOnly("co.aikar:acf-paper:0.5.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.3")
    compileOnly("org.geysermc.floodgate:api:2.2.0-SNAPSHOT")
    compileOnly("com.zaxxer:HikariCP:5.0.1")
    compileOnly("org.xerial:sqlite-jdbc:3.42.0.0")

    compileOnly("net.william278:annotaml:2.0.2")
    compileOnly("net.william278:DesertWell:2.0.4")
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