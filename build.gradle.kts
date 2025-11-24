plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.kotlinxSerialization)
    alias(libs.plugins.mavenPublish)
}

group = "me.silent.graphics"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}


kotlin {

    linuxX64 {
        compilations.getByName("main") {
            cinterops {
                val libXbc by creating {
                    definitionFile.set(project.file("nativeInterop/cinterop/xcb.def"))
                    compilerOpts("-I/usr/include/xcb/xcb")

                }
                val libX11 by creating {
                    definitionFile.set(project.file("nativeInterop/cinterop/x11.def"))
                    compilerOpts()
                }
            }
        }
        binaries {
            executable {
                runTaskProvider?.configure {
                    entryPoint = "main"
                    linkerOpts("-lX11 -lxcb")
                }
            }
        }
    }
    mingwX64 {
        compilations.getByName("main") {
            cinterops {
                val libXbc by creating {
                    definitionFile.set(project.file("nativeInterop/cinterop/xcb.def"))
                    compilerOpts()

                }
                val libX11 by creating {
                    definitionFile.set(project.file("nativeInterop/cinterop/x11.def"))
                    compilerOpts()
                }
            }
        }
        binaries {
            executable {
                runTaskProvider?.configure {
                    entryPoint = "main"
                    linkerOpts("-Wl,--subsystem,windows")
                }
            }
        }
    }
    macosX64 {
    }
    // license 10k,- + mac 20k,- + Xstudio 2k,- a year + jetbrains 3k,- a year
    // 20 * 35k = 700k,-
    // 20 * 15k = 300k,- a year
    sourceSets {
        commonMain {
            dependencies {
                implementation(libs.bundles.kotlinxEcosystem)
            }
        }
    }

}

mavenPublishing {
    coordinates(group.toString(), "silentGraphics", version.toString())
    pom {
        name = "Silent Graphics"
        description = "Native kotlin graphics library"
        inceptionYear = "2025"
    }
}

tasks {
    build {
        dependsOn(clean)
    }
}