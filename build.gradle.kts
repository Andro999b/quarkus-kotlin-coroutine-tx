plugins {
    kotlin("jvm") version "1.4.30"
    kotlin("plugin.allopen") version "1.4.30"
    id("io.quarkus")
    id("maven-publish")
}

repositories {
    mavenLocal()
    mavenCentral()
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/Andro999b/quarkus-kotlin-coroutine-tx")
        credentials {
            username = project.findProperty("gpr.user") as String? ?: System.getenv("GITPKG_USER")
            password = project.findProperty("gpr.key") as String? ?: System.getenv("GITPKG_TOKEN")
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("gpr") {
            from(components["java"])
        }
    }
}


val quarkusPlatformGroupId: String by project
val quarkusPlatformArtifactId: String by project
val quarkusPlatformVersion: String by project

val testcontainersVersion = "1.15.1"
val coroutineVersion = "1.4.2"
val mutinyVersion = "0.13.0"

dependencies {
    implementation(platform("org.testcontainers:testcontainers-bom:${testcontainersVersion}"))
    implementation(enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))

    implementation("io.quarkus:quarkus-kotlin")
    implementation("io.quarkus:quarkus-arc")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:${coroutineVersion}")
    implementation("io.smallrye.reactive:mutiny-kotlin:${mutinyVersion}")

    implementation("io.quarkus:quarkus-reactive-datasource")
    implementation("io.smallrye.reactive:smallrye-mutiny-vertx-sql-client")

    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("io.quarkus:quarkus-jdbc-postgresql")
    testImplementation("io.quarkus:quarkus-flyway")
    testImplementation("io.quarkus:quarkus-reactive-pg-client")
    testImplementation("io.quarkus:quarkus-junit5")
}

group = "hatis"
version = "0.0.1"

java {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
    withSourcesJar()
}

allOpen {
    annotation("javax.ws.rs.Path")
    annotation("javax.enterprise.context.ApplicationScoped")
    annotation("io.quarkus.test.junit.QuarkusTest")
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = JavaVersion.VERSION_11.toString()
    kotlinOptions.javaParameters = true
    kotlinOptions.freeCompilerArgs += "-Xopt-in=kotlinx.coroutines.ExperimentalCoroutinesApi"
}
