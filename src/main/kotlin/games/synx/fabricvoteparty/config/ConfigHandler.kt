package games.synx.fabricvoteparty.config

import org.slf4j.LoggerFactory
import org.spongepowered.configurate.BasicConfigurationNode
import org.spongepowered.configurate.ConfigurateException
import org.spongepowered.configurate.gson.GsonConfigurationLoader
import org.spongepowered.configurate.reactive.Subscriber
import org.spongepowered.configurate.reference.ConfigurationReference
import org.spongepowered.configurate.reference.ValueReference
import org.spongepowered.configurate.reference.WatchServiceListener
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.WatchEvent

class ConfigHandler<T>(applicationFolder: Path, configName: String, private val clazz: Class<T>) : AutoCloseable {

    companion object {
        private val LOGGER = LoggerFactory.getLogger("Lanayru/ConfigHandler")

    }

    val listener = WatchServiceListener.create()
    lateinit var base: ConfigurationReference<BasicConfigurationNode>
    lateinit var config: ValueReference<T, BasicConfigurationNode>

    val configFile: Path = Paths.get(applicationFolder.toString() + File.separator + configName)

    init {

        try {
            base = this.listener.listenToConfiguration(
                {
                        file -> GsonConfigurationLoader.builder()
                    .defaultOptions{ it.shouldCopyDefaults(true) }
                    .path(file)
                    .build()
                }, configFile
            )

            this.listener.listenToFile(configFile) { event ->
                LOGGER.info("Updated ConfigFile ${configFile.fileName}")
            }

            this.config = this.base.referenceTo(clazz)
            this.base.save()

        } catch (throwable: Throwable) {
            LOGGER.error("Error loading config file ${configFile.fileName}", throwable)
        }

    }

    fun getConfig(): T {
        return this.config.get()!!
    }

    @kotlin.jvm.Throws(ConfigurateException::class)
    fun saveToFile() {
        this.base.node().set(clazz, clazz.cast(getConfig()))
        this.base.loader().save(this.base.node())
    }

    fun addListener(listener: Subscriber<WatchEvent<*>>) {
        this.listener.listenToFile(this.configFile, listener)
    }

    override fun close() {
        this.listener.close()
        this.base.close()
    }


}
