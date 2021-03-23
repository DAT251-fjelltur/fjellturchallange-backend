import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
  java
  idea
  id("org.springframework.boot") version "2.3.4.RELEASE"
  id("io.spring.dependency-management") version "1.0.10.RELEASE"
  id("com.diffplug.spotless") version "5.6.1"
  kotlin("jvm") version "1.4.31"
  kotlin("plugin.spring") version "1.4.31"
  kotlin("plugin.jpa") version "1.4.31"
  kotlin("plugin.allopen") version "1.4.31"
}

group = "no.hvl.dat251"
version = "0.0.1-SNAPSHOT"

java.sourceCompatibility = JavaVersion.VERSION_11
java.targetCompatibility = java.sourceCompatibility

repositories {
  mavenCentral()
}

dependencies {
  implementation("org.springframework.boot:spring-boot-starter-validation")
  implementation("org.springframework.boot:spring-boot-starter-data-jpa")
  implementation("org.springframework.boot:spring-boot-starter-security")
  implementation("org.springframework.boot:spring-boot-starter-amqp")
  implementation("org.springframework.boot:spring-boot-starter-web")

  implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
  implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
  implementation("org.jetbrains.kotlin:kotlin-reflect")
  implementation("org.postgresql:postgresql:42.2.18")
  implementation("com.auth0:java-jwt:3.13.0")

  implementation("org.springdoc:springdoc-openapi-ui:1.5.5")
  implementation("org.springdoc:springdoc-openapi-security:1.5.5")
  implementation("org.springdoc:springdoc-openapi-kotlin:1.5.5")
  implementation("org.springdoc:springdoc-openapi-data-rest:1.5.5")

  runtimeOnly("org.springframework.boot:spring-boot-devtools")

  testImplementation("org.springframework.boot:spring-boot-starter-test") {
    exclude(group = "org.junit.vintage", module = "junit-vintage-engine")
  }
  testImplementation("org.springframework.security:spring-security-test")
  testImplementation("org.jetbrains.kotlin:kotlin-test")
  testImplementation("org.mock-server:mockserver-netty:5.11.2")
  testImplementation("org.mock-server:mockserver-client-java:5.11.2")
  testRuntimeOnly("com.h2database:h2")


  annotationProcessor("org.springframework.boot:spring-boot-configuration-processor")
}

tasks.withType<Test> {
  useJUnitPlatform()
}

kotlin {
  sourceSets.all {
    languageSettings.enableLanguageFeature("InlineClasses")
  }
}

tasks.withType<KotlinCompile> {
  kotlinOptions {
    freeCompilerArgs = listOf("-Xjsr305=strict")
    jvmTarget = "${java.sourceCompatibility}"
    useIR = true
  }
}

spotless {
  format("misc") {
    // define the files to apply `misc` to
    target("*.gradle", "*.md", ".gitignore")

    // define the steps to apply to those files
    trimTrailingWhitespace()
    indentWithSpaces(2) // or spaces. Takes an integer argument if you don't like 4
    endWithNewline()
  }
  kotlin {
    ktlint("0.40.0").userData(
      mapOf(
        "indent_size" to "2",
        "continuation_indent_size" to "2",
        "max_line_length" to "160"
      )
    )
  }
}

tasks.withType<Test>() {
  dependsOn("spotlessApply")
}

tasks.withType<JavaExec>() {
  dependsOn("spotlessApply")
}
allOpen{
  annotation("javax.persistence.Entity")
}