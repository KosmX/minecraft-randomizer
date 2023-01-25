package dev.kosmx.randomizer

import dev.kosmx.randomizer.inverter.Inverter
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.decodeFromStream
import kotlinx.serialization.json.encodeToStream
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.loader.api.FabricLoader
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import net.minecraft.server.command.CommandManager.*
import net.minecraft.text.Text


object RandomizerInit : ModInitializer {
	val logger: Logger = LoggerFactory.getLogger("minecraft-randomizer")
	private var config: RandomizerConfig = RandomizerConfig()

	private val jsonConfig = Json {
		prettyPrint = true
		encodeDefaults = true
	}

	override fun onInitialize() {
		// This code runs as soon as Minecraft is in a mod-load-ready state.
		// However, some things (like resources) may still be uninitialized.
		// Proceed with mild caution.
		logger.info("Hello Fabric world!")

		loadConfig()

		CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
			dispatcher.register(literal("randomizer").then(
				literal("reload").requires { it.hasPermissionLevel(4) }
					.executes { ctx ->
						loadConfig()
						ctx.source.sendFeedback(Text.literal("Randomizer config reloaded"), true)
						1
					}
			))
		}
	}


	@OptIn(ExperimentalSerializationApi::class)
	private fun loadConfig () {

		val configFile = FabricLoader.getInstance().configDir.resolve("randomizer.json").toFile()

		if (configFile.isFile) {
			configFile.inputStream().use { input ->
				config = Json.decodeFromStream(input)
			}
		} else {
			configFile.outputStream().use { output ->
				jsonConfig.encodeToStream(config, output)
			}
		}

		Inverter(config.inverter)
	}
}