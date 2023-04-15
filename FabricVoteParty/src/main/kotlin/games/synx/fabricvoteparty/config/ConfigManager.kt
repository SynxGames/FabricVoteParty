package games.synx.fabricvoteparty.config

import com.google.common.collect.Maps
import org.slf4j.LoggerFactory
import org.spongepowered.configurate.ConfigurateException
import org.spongepowered.configurate.reactive.Subscriber
import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.WatchEvent
import kotlin.reflect.KClass

fun <T : Any> getConfig(clazz: KClass<T>): T = ConfigManager.getConfig(clazz.java)
fun <T : Any> getConfig(clazz: Class<T>): T = ConfigManager.getConfig(clazz)
inline fun <reified T: Any> getConfig(): T = getConfig(T::class.java)

private val pluginDir = Paths.get("config" + File.separator + "voteparty")
fun pluginDir(): Path {
    if (!pluginDir.toFile().exists()) {
        pluginDir.toFile().mkdirs()
    }
    return pluginDir
}

object ConfigManager : AutoCloseable {

    private val LOGGER = LoggerFactory.getLogger("VoteParty/ConfigManager")
    private val CONFIGS: MutableMap<Class<*>, ConfigHandler<*>> = Maps.newConcurrentMap()


    fun getFilePath(fileName: String): Path {
        return Paths.get(pluginDir().toString() + File.separator + fileName)
    }

    override fun close() {
        for (configHandler in CONFIGS.values) {
            try {
                configHandler.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun saveConfig(config: Class<*>) {
        try {
            CONFIGS[config]!!.saveToFile()
        } catch (e: ConfigurateException) {
            e.printStackTrace()
        }
    }

    private fun initConfig(dir: Path, config: Class<*>) {
        LOGGER.info("Initialising Configuration: {}", config.simpleName)
        val fileName = config.simpleName.lowercase() + ".json"
        CONFIGS[config] = ConfigHandler(dir, fileName, config)
    }

    private fun initConfig(dir: Path, config: KClass<*>) {
        initConfig(dir, config.java)
    }

    fun initConfigs(vararg configs: KClass<*>) = configs.forEach { initConfig(pluginDir(), it) }

    fun <T> getConfig(config: Class<T>): T {
        return CONFIGS[config]!!.getConfig() as T
    }

    fun <T : Any> addListener(config: KClass<T>, listener: Subscriber<WatchEvent<*>>) {
        CONFIGS[config.java]!!.addListener(listener)
    }

    fun <T> addListener(config: Class<T>, listener: Subscriber<WatchEvent<*>>) {
        CONFIGS[config]!!.addListener(listener)
    }

}