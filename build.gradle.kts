import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

buildscript {
    repositories {
        mavenLocal()
        flatDir {
            dirs("libs")
        }
    }
}

plugins {
    kotlin("jvm") version "1.6.20"
    id("maven-publish")
    id("signing")
}

group = "net.appsynth.danger"
version = "0.1.0-SNAPSHOT"

val isReleaseVersion = !version.toString().endsWith("SNAPSHOT")

repositories {
    mavenCentral()
}

dependencies {
    implementation("systems.danger:danger-kotlin-sdk:1.2")

    testImplementation("org.junit.jupiter:junit-jupiter:5.8.2")
}

java {
    withJavadocJar()
    withSourcesJar()
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "1.8"
}

publishing {
    publications {
        create<MavenPublication>("main") {
            artifactId = "danger-kotlin-jacoco"
            from(components["java"])
            versionMapping {
                usage("java-api") {
                    fromResolutionOf("runtimeClasspath")
                }
                usage("java-runtime") {
                    fromResolutionResult()
                }
            }
            pom {
                name.set("danger-kotlin-jacoco")
                description.set("Plugin for danger-kotlin that reads JaCoCo code coverage reports")
                url.set("https://github.com/appsynth-org/danger-kotlin-jacoco")
                inceptionYear.set("2022")
                licenses {
                    license {
                        name.set("The MIT License (MIT)")
                        url.set("http://opensource.org/licenses/MIT")
                        distribution.set("repo")
                    }
                }
                scm {
                    url.set("https://github.com/appsynth-org/danger-kotlin-jacoco")
                    connection.set("scm:git:git@github.com:appsynth-org/danger-kotlin-jacoco.git")
                    developerConnection.set("scm:git:ssh://github.com/appsynth-org/danger-kotlin-jacoco.git")
                }
                developers {
                    developer {
                        id.set("apolatynski")
                        name.set("Andrzej Polatynski")
                    }
                }
            }
        }
    }

    repositories {
        maven {
            name ="SonatypeOSS"
            credentials {
                username = if (project.hasProperty("ossrhUsername")) (project.property("ossrhUsername") as String) else "N/A"
                password = if (project.hasProperty("ossrhPassword")) (project.property("ossrhPassword") as String) else "N/A"
            }

            val releasesUrl = "https://oss.sonatype.org/service/local/staging/deploy/maven2/"
            val snapshotsUrl = "https://oss.sonatype.org/content/repositories/snapshots/"
            url = uri(if (isReleaseVersion) releasesUrl else snapshotsUrl)
        }
    }
}

signing {
    useGpgCmd()
    sign(publishing.publications["main"])
}
