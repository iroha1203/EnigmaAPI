package app.enigma

import app.enigma.services.*
import app.enigma.models.*
import io.ktor.serialization.gson.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.server.plugins.contentnegotiation.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun main() {
  embeddedServer(Netty, port = 8000, host = "0.0.0.0", module = Application::module)
    .start(wait = true)
}

fun Application.module() {
  install(ContentNegotiation) {
    gson {
    }
  }

  routing {
    post ("/api/enigma") {
      val body = call.receive<EnigmaRequest>()

      val enigma = body.setting.run {
        val rotor1 = Rotor(scramblers[rotors[0].scramblerId], rotors[0].initialRotate, 0)
        val rotor2 = Rotor(scramblers[rotors[1].scramblerId], rotors[1].initialRotate, 1)
        val rotor3 = Rotor(scramblers[rotors[2].scramblerId], rotors[2].initialRotate, 2)
        val reflector = Reflector()
        val plugBoard = PlugBoard(plugBoard.associate { it.from to it.to })
        Enigma(rotor1, rotor2, rotor3, reflector, plugBoard)
      }

      val cypher = body.message
        .split(" ")
        .joinToString(" ") { m ->
          m.toCharArray()
            .map { encrypt(it, enigma) }
            .toCharArray()
            .concatToString()
        }

      call.respond(cypher)
    }
  }
}

data class EnigmaRequest(
  val message: String,
  val setting: EnigmaSetting
)

data class EnigmaSetting(
  val rotors: List<RotorSetting>,
  val plugBoard: List<PlugBoardConnect>
)

data class RotorSetting(
  val scramblerId: Int,
  val initialRotate: Int
)

data class PlugBoardConnect(
  val from: Char,
  val to: Char
)
