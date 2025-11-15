plugins {
    kotlin("jvm") version "2.2.20"
    id("com.gradleup.shadow") version "9.2.2"
    id("java")
    id("maven-publish")
}

group = "dev.xF3d3"
version = "4.7.6"

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
    maven ("https://repo.tcoded.com/releases")
    maven ("https://repo.minebench.de/")
}

dependencies {
    compileOnly("com.zaxxer:HikariCP:7.0.2")
    implementation("net.william278:annotaml:2.0.7")
    implementation("net.william278:DesertWell:2.0.4")
    implementation("com.google.code.gson:gson:2.13.2")

    // Kotlin
    compileOnly("org.jetbrains.kotlin:kotlin-stdlib:2.2.20")

    compileOnly("org.projectlombok:lombok:1.18.40")
    annotationProcessor("org.projectlombok:lombok:1.18.40")

    testCompileOnly("org.projectlombok:lombok:1.18.40")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.40")

    // Folia and Spigot
    compileOnly("io.papermc.paper:paper-api:1.19.4-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.6")
    compileOnly("org.geysermc.floodgate:api:2.2.4-SNAPSHOT")
    implementation("org.bstats:bstats-bukkit:3.1.0")
    implementation("de.themoep:inventorygui:1.6.5-SNAPSHOT")
    
    // PaperLib
    implementation("io.papermc:paperlib:1.0.8")
    implementation("com.tcoded:FoliaLib:0.5.1")

    // Libs
    implementation("co.aikar:acf-paper:0.5.1-SNAPSHOT")
    implementation("dev.dejvokep:boosted-yaml:1.3.1")

    implementation("de.themoep:minedown-adventure:1.7.5")

    compileOnly("net.luckperms:api:5.4")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7.1")

    // Database
    compileOnly("org.xerial:sqlite-jdbc:3.46.1.0")
    compileOnly("com.mysql:mysql-connector-j:9.2.0")
    compileOnly("com.h2database:h2:2.3.232")
    compileOnly("org.postgresql:postgresql:42.7.3")

    compileOnly("redis.clients:jedis:5.2.0")

    compileOnly("net.william278.huskhomes:huskhomes-bukkit:4.7")
}

tasks {
    build {
        dependsOn(shadowJar)
    }

    shadowJar {
        archiveFileName.set("${rootProject.name}-${rootProject.version}.jar")
        //archiveClassifier.set("main")

        exclude("com/google/errorprone/annotations/**")

        relocate("org.bstats", "dev.xf3d3.ultimateteams.libraries.bstats")
        relocate("de.themoep.minedown", "dev.xf3d3.ultimateteams.libraries.minedown")
        relocate("com.tcoded.folialib", "dev.xf3d3.ultimateteams.libraries.folialib")
        relocate("co.aikar", "dev.xf3d3.ultimateteams.libraries.aikar")
        relocate("com.google.gson", "dev.xf3d3.ultimateteams.libraries.gson")
        relocate("org.jetbrains", "dev.xf3d3.ultimateteams.libraries.jetbrains")
        relocate("org.intellij", "dev.xf3d3.ultimateteams.libraries.intellij")
        relocate("org.json", "dev.xf3d3.ultimateteams.libraries.json")
        relocate("de.themoep", "dev.xf3d3.ultimateteams.libraries.inventorygui")
        relocate("dev.dejvokep", "dev.xf3d3.ultimateteams.libraries.boostedyaml")
        relocate("net.william278.desertwell", "dev.xf3d3.ultimateteams.libraries.william278.desertwell")
        relocate("net.william278.annotaml", "dev.xf3d3.ultimateteams.libraries.william278.annotaml")
    }

    compileJava {
        options.encoding = "UTF-8"
    }
    compileTestJava {
        options.encoding = "UTF-8"
    }
}

tasks.withType<Javadoc>().configureEach {
    options.encoding = "UTF-8" // You can also put the encoding here
    if (options is StandardJavadocDocletOptions) {
        // You need to add these as two separate options
        (options as StandardJavadocDocletOptions).addStringOption("Xdoclint:none")
        (options as StandardJavadocDocletOptions).addStringOption("-quiet")
    }
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }

    withJavadocJar()
    withSourcesJar()
}


publishing {
    publications {
        create<MavenPublication>("mavenJava") {
            //artifact(tasks.named("sourcesJar"))
            from(components["java"])

            groupId = project.group.toString()
            artifactId = rootProject.name
            version = project.version.toString()
        }
    }
}

tasks.named<Jar>("jar") {
    archiveClassifier.set("api")
}