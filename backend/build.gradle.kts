plugins {
    java
    id("org.springframework.boot") version "3.5.4"
    id("io.spring.dependency-management") version "1.1.7"
}


group = "com.xingci"
version = "1.0.0"


java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}


repositories {
    mavenCentral()
}


dependencies {

    implementation(
        "org.springframework.boot:spring-boot-starter-web"
    )

    implementation(
        "org.springframework.boot:spring-boot-starter-validation"
    )

    implementation(
        "org.springframework.boot:spring-boot-starter-websocket"
    )


    implementation(
        "com.fasterxml.jackson.core:jackson-databind"
    )


    implementation(
        "com.github.oshi:oshi-core:6.6.5"
    )


    implementation(
        "commons-io:commons-io:2.16.1"
    )


    compileOnly(
        "org.projectlombok:lombok:1.18.38"
    )

    annotationProcessor(
        "org.projectlombok:lombok:1.18.38"
    )


    testImplementation(
        "org.springframework.boot:spring-boot-starter-test"
    )

}


tasks.test {
    useJUnitPlatform()
}