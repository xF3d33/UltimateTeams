plugins {
    kotlin("jvm") version "1.8.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("java")
}

group = "dev.xf3d3"
version = "4.0.6-BETA"

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

    compileOnly("org.projectlombok:lombok:1.18.34")
    annotationProcessor("org.projectlombok:lombok:1.18.34")

    testCompileOnly("org.projectlombok:lombok:1.18.34")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.34")

    // Folia and Spigot
    compileOnly("dev.folia:folia-api:1.20.1-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("org.geysermc.floodgate:api:2.2.0-SNAPSHOT")
    implementation("org.bstats:bstats-bukkit:3.1.0")
    implementation("dev.triumphteam:triumph-gui:3.1.11")
    
    // PaperLib
    implementation("io.papermc:paperlib:1.0.8")
    implementation("com.github.technicallycoded:FoliaLib:main-SNAPSHOT")

    // Libs
    implementation("co.aikar:acf-bukkit:0.5.1-SNAPSHOT")
    implementation("com.google.code.gson:gson:2.11.0")
    implementation("dev.dejvokep:boosted-yaml:1.3.1")

    compileOnly("net.luckperms:api:5.4")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")

    // Database
    compileOnly("org.xerial:sqlite-jdbc:3.46.1.0")
    compileOnly("com.mysql:mysql-connector-j:9.2.0")
    compileOnly("com.zaxxer:HikariCP:6.2.1")
    compileOnly("com.h2database:h2:2.3.232")
    compileOnly("org.postgresql:postgresql:42.7.3")

    compileOnly("redis.clients:jedis:5.2.0")

    implementation("net.william278:annotaml:2.0.2")
    implementation("net.william278:DesertWell:2.0.4")
    compileOnly("net.william278.huskhomes:huskhomes-bukkit:4.7")
}

tasks {
    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        archiveFileName.set("${rootProject.name}-${rootProject.version}.jar")
        archiveClassifier.set("main")

        relocate("org.bstats", "dev.xf3d3.ultimateteams.libraries.bstats")
        relocate("com.tcoded.folialib", "dev.xf3d3.ultimateteams.libraries.folialib")
        relocate("co.aikar", "dev.xf3d3.ultimateteams.libraries.aikar")
        relocate("com.google.gson", "dev.xf3d3.ultimateteams.libraries.gson")
        relocate("org.jetbrains", "dev.xf3d3.ultimateteams.libraries.jetbrains")
        relocate("org.intellij", "dev.xf3d3.ultimateteams.libraries.intellij")
        relocate("org.json", "dev.xf3d3.ultimateteams.libraries.json")
        relocate("dev.triumphteam", "dev.xf3d3.ultimateteams.libraries.triumphteam")
        relocate("dev.dejvokep", "dev.xf3d3.ultimateteams.libraries.boostedyaml")
        relocate("io.papermc", "dev.xf3d3.ultimateteams.libraries.papermc")
        relocate("net.kyori", "dev.xf3d3.ultimateteams.libraries.kyori")
        relocate("net.william278.desertwell", "dev.xf3d3.libraries.ultimateteams.william278.desertwell")
        relocate("net.william278.annotaml", "dev.xf3d3.libraries.ultimateteams.william278.annotaml")
    }
}