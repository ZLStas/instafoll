plugins {
    id("org.springframework.boot") version "2.4.3"
    id("io.spring.dependency-management") version "1.0.10.RELEASE"
    java
}

group = "com.crane"
version = "0.0.1-SNAPSHOT"

configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_11
    targetCompatibility = JavaVersion.VERSION_11
}

repositories {
    mavenCentral()
    jcenter()
    maven { url = uri("https://jitpack.io") }
}

dependencyManagement {
    dependencies {
        dependency("com.squareup.okhttp3:okhttp:4.7.2")
        dependency("com.squareup.okhttp3:okhttp-urlconnection:4.7.2")
    }
}

dependencies {

    implementation("org.telegram:telegrambots:4.9.1")
    implementation("com.github.instagram4j:instagram4j:develop-SNAPSHOT")

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-quartz")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    implementation("org.apache.commons:commons-lang3:3.0")
    implementation("org.projectlombok:lombok:1.18.16")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    annotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test") {
        exclude("org.junit.vintage:junit-vintage-engine")
    }

}
