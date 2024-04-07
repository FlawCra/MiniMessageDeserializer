package cc.flawcra.mmd

import io.javalin.Javalin
import io.javalin.http.staticfiles.Location
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer
import java.io.File
import java.util.*

/**
 * The `main` function is the entry point of the application. It loads the properties, retrieves the port from the properties or uses the default port,
 * creates a Javalin instance, configures static file serving, adds a route for deserialization, and starts the Javalin server.
 *
 * @see loadProperties
 * @see InputMessage
 * @see parseMessage
 * @see Response
 * @see preprocessForGradient
 */
fun main() {
    val configProps = loadProperties()

    val port = configProps.getProperty("server.port")?.toInt() ?: 7070

    Javalin.create { config ->
        config.staticFiles.add("/web", Location.CLASSPATH)
    }.apply {
        post("/deserialize") { ctx ->
            try {
                val input = ctx.bodyAsClass(InputMessage::class.java)
                val result = parseMessage(input.message)
                ctx.json(Response(success = true, data = mapOf("result" to result)))
            } catch (e: Exception) {
                ctx.json(Response(success = false, data = mapOf("msg" to "Error parsing message: ${e.message}")))
            }
        }
        post("/serialize") { ctx ->
            try {
                val input = ctx.bodyAsClass(InputMessage::class.java)
                val result = parseMessage(input.message, true)
                ctx.json(Response(success = true, data = mapOf("result" to result)))
            } catch (e: Exception) {
                ctx.json(Response(success = false, data = mapOf("msg" to "Error parsing message: ${e.message}")))
            }
        }
    }.start(port)
}

/**
 * Loads and retrieve properties.
 *
 * @return the loaded properties
 */
private fun loadProperties(): Properties {
    val properties = Properties()

    Thread.currentThread().contextClassLoader.getResourceAsStream("config.properties")?.use { inputStream ->
        properties.load(inputStream)
    }

    val configFile = File("config.properties")
    if (configFile.exists()) {
        configFile.inputStream().use { fileInputStream ->
            properties.load(fileInputStream)
        }
    }

    System.getProperties().forEach { (key, value) ->
        if (key is String && value is String) {
            properties.setProperty(key, value)
        }
    }

    return properties
}

/**
 * Preprocesses the input string by converting custom text color format to gradient format.
 *
 * Add support for <#C4E538>Text</#EE5A24> as DecentHolograms seems to use this format
 **/
private fun preprocessForGradient(input: String): String {
    return input.replace("<(#\\w{6})>(.*?)</(#\\w{6})>".toRegex(), "<gradient:$1:$3>$2</gradient>")
}

/**
 * Parses a given message by preprocessing it for gradient format, deserializing it using MiniMessage and then serializing it using Gson.
 *
 * @param input the input message string to be parsed
 * @return the serialized string representation of the parsed message
 */
fun parseMessage(input: String, serialize: Boolean = false): String {
    val mm = MiniMessage.miniMessage()
    if (serialize) {
        return mm.serialize(GsonComponentSerializer.gson().deserialize(input))
    }

    val serialized = preprocessForGradient(input)
    return GsonComponentSerializer.gson().serialize(mm.deserialize(serialized))
}

/**
 * Represents an input message string.
 *
 * @param message the input message string
 */
data class InputMessage(val message: String)
/**
 * Represents a response object containing the success status and data.
 *
 * @property success Boolean indicating the success status of the response
 * @property data Map of String keys and String values representing the response data
 */
data class Response(val success: Boolean, val data: Map<String, String>)