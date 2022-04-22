
plugins {
    id("org.springframework.boot") version "2.6.7"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.spring") version "1.6.21"
    kotlin("plugin.jpa") version "1.6.21"
}

repositories {
    mavenCentral() // <3>
}

dependencies {
    implementation(platform("org.jetbrains.kotlin:kotlin-bom")) // <4>
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8") // <5>

    implementation("com.netflix.graphql.dgs:graphql-dgs-spring-boot-starter:4.9.22")
    implementation("com.netflix.graphql.dgs:graphql-dgs-pagination")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
}
