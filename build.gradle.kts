buildscript {
    dependencies {
        classpath("org.postgresql:postgresql:42.2.5") {
            because("for Flyway")
        }
    }
}
plugins {
    java
    id("org.flywaydb.flyway") version "5.2.4"
}

java {
    sourceCompatibility = JavaVersion.VERSION_11
}
tasks.withType<JavaCompile> {
    options.compilerArgs.addAll(arrayOf("--release", "11"))
}

repositories {
    mavenCentral()
}

val jooqCodegen by configurations.creating
dependencies {
    implementation("org.jooq:jooq:3.11.9")
    implementation("org.postgresql:postgresql:42.2.5")
    implementation("org.apache.logging.log4j:log4j-slf4j-impl:2.11.0")

    testImplementation("junit:junit:4.12")

    jooqCodegen("org.jooq:jooq-codegen:3.11.9")
    jooqCodegen("org.postgresql:postgresql:42.2.5")
}

flyway {
    url = "jdbc:postgresql:mcve"
    user = "mcve"
    password = "mcve"
    schemas = arrayOf("mcve")
}

val jooqOutputDir = file("src/main/jooq")

tasks {
    val flywayMigrate by existing

    register<JavaExec>("jooq") {
        val jooqConfigFile = file("src/jooq-codegen.xml")

        dependsOn(flywayMigrate)

        inputs.dir("src/main/resources/db/migration").withPathSensitivity(PathSensitivity.RELATIVE)
        inputs.file(jooqConfigFile).withPathSensitivity(PathSensitivity.NONE)
        outputs.dir(jooqOutputDir)

        doFirst {
            project.delete(jooqOutputDir)
        }

        classpath = jooqCodegen
        main = "org.jooq.codegen.GenerationTool"
        systemProperties = mapOf(
            "outputdir" to jooqOutputDir.path
        )
        args(jooqConfigFile)
    }
}
sourceSets {
    main {
        java {
            srcDir(jooqOutputDir)
        }
    }
}
