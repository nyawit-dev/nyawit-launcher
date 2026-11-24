plugins {
    `kotlin-dsl`
}

gradlePlugin {
    plugins {
        create("nyawitBuildConfig") {
            id = "dev.nyawit.buildconfig"
            implementationClass = "dev.nyawit.gradle.BuildConfigPlugin"
        }
    }
}
