package dev.gabrielecappellaro.deviceinfo.model

data class Mount(
    val device: String,
    val mountPoint: String,
    val fileSystem: String,
    val prop: String,
    val useless1: String,
    val useless2: String
)