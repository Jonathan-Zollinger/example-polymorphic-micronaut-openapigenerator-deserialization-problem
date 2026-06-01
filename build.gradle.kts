plugins {
    id("groovy")
    id("io.micronaut.library") version "4.6.2"
    id("io.micronaut.openapi") version "4.5.3"
}

version = "0.1"
group = "com.example"



repositories {
    mavenCentral()
}

dependencies {
    annotationProcessor("org.projectlombok:lombok")
    annotationProcessor("io.micronaut.validation:micronaut-validation-processor")
    annotationProcessor("io.micronaut.openapi:micronaut-openapi")
    annotationProcessor("io.micronaut.serde:micronaut-serde-processor")

    implementation("io.micronaut.serde:micronaut-serde-jackson")

    compileOnly("org.projectlombok:lombok")
    compileOnly("io.micronaut.openapi:micronaut-openapi-annotations")

    runtimeOnly("org.yaml:snakeyaml")

    testImplementation("io.micronaut:micronaut-http-client")
}

java {
    sourceCompatibility = JavaVersion.toVersion("21")
    targetCompatibility = JavaVersion.toVersion("21")
}


micronaut {
    runtime("netty")
    testRuntime("spock2")
    processing {
        incremental(true)
        annotations("com.example.*")
    }
    openapi {
        client(file("src/main/resources/schema.yml")) {
            apiPackageName = "com.example.client"
            modelPackageName = "com.example.model"
            useReactive = true
            useAuth = false
            lombok.set(true)
            clientId = "example"
            apiNameSuffix = "Client"
        }
    }
}




