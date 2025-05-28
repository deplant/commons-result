plugins {
    id("java-library")
    id("maven-publish")
    id("signing")
}

group = "tech.deplant.commons"
version = "0.2.0-SNAPSHOT"

val jdkVersion = JavaLanguageVersion.of("21")

repositories {
    mavenLocal()
    maven {
        name = "GitHubPackages"
        url = uri("https://maven.pkg.github.com/deplant/maven-artifactory")
        credentials {
            username = project.properties["githubUsername"] as String?
            password = project.properties["githubUploadToken"] as String?
        }
    }
    mavenCentral()
}

dependencies {
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.junit.api)
    testRuntimeOnly(libs.junit.launcher)
}

tasks.test {
    useJUnitPlatform()
}

publishing {
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/deplant/maven-artifactory")
            credentials {
                username = project.properties["githubUsername"] as String?
                password = project.properties["githubUploadToken"] as String?
            }
        }
        maven {
            name = "Sonatype"
            url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
            credentials {
                username = project.properties["ossrhUsername"] as String?
                password = project.properties["ossrhPassword"] as String?
            }
        }
    }
    publications {
        register("commons-result",MavenPublication::class) {
            groupId = group.toString()
            artifactId = "result"
            version = version.toString()
            from(components["java"])
            pom {
                name = "result"
                description = "Result API for Java"
                url = "https://github.com/deplant/commons"
                licenses {
                    license {
                        name = "The Apache License, Version 2.0"
                        url = "http://www.apache.org/licenses/LICENSE-2.0.txt"
                    }
                }
                developers {
                    developer {
                        id = project.properties["developerId"] as String?
                        name = project.properties["developerName"] as String?
                        email = project.properties["developerEmail"] as String?
                        organization = project.properties["developerOrg"] as String?
                        organizationUrl = project.properties["developerUrl"] as String?
                    }
                }
                scm {
                    connection = "scm:git:git://github.com/deplant/commons.git"
                    developerConnection = "scm:git:ssh://github.com/deplant/commons.git"
                    url = "http://github.com/deplant/commons"
                }
            }
        }
    }
}

signing {
    sign(publishing.publications["commons-result"])
}

java {
    withJavadocJar()
    withSourcesJar()
    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
    toolchain {
        languageVersion = jdkVersion
    }
}

tasks.withType<JavaExec> {
    javaLauncher = javaToolchains.launcherFor {
        languageVersion = jdkVersion
    }
}

tasks.withType<Test> {
    javaLauncher = javaToolchains.launcherFor {
        languageVersion = jdkVersion
    }
    useJUnitPlatform()

    filter {
//include specific method in any of the tests
//includeTestsMatching("*UiCheck")

//include all tests from package
//includeTestsMatching("org.gradle.internal.*")

//include all integration tests
//excludeTestsMatching("*IntegrationTest")
    }
}
