package dev.kosmx.randomizer

import dev.kosmx.randomizer.inverter.InverterConfig
import kotlinx.serialization.Serializable

@Serializable
data class RandomizerConfig(
    val inverter: InverterConfig = InverterConfig()
)
