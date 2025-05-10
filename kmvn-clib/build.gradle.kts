/*
 * Copyright © 2025 RTAkland
 * Date: 2025/5/10 14:07
 * Open Source Under Apache-2.0 License
 * https://www.apache.org/licenses/LICENSE-2.0
 */

plugins {
    id("dev.guillermo.gradle.c-library") version "0.5.0"
}

library {
    linkage.set(listOf(Linkage.STATIC))
    targetMachines.set(listOf(machines.linux.x86_64, machines.windows.x86_64))
    source.from(file("src"))
    publicHeaders.from(file("headers"))
}