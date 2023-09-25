package app.enigma.services

import kotlin.math.pow
import app.enigma.models.*


fun encrypt(char: Char, enigma: Enigma): Char = enigma.run {
  char
    .shift(plugBoard.getShifts())
    .shift(rotor1.getForwardShifts())
    .shift(rotor2.getForwardShifts())
    .shift(rotor3.getForwardShifts())
    .shift(reflector.getShifts())
    .shift(rotor3.getReflectShifts())
    .shift(rotor2.getReflectShifts())
    .shift(rotor1.getReflectShifts())
    .shift(plugBoard.getShifts())
    .also {
      rotor1.rotate()
      rotor2.rotate()
      rotor3.rotate()
    }
}

fun Char.shift(shifts: List<Int>): Char = let {
  val position = it.uppercaseChar().code - 65
  ((position + shifts[position]) % 26 + 65).toChar()
}

data class Enigma(
  val rotor1: Rotor,
  val rotor2: Rotor,
  val rotor3: Rotor,
  val reflector: Reflector,
  val plugBoard: PlugBoard
)

class Rotor(scrambler: Map<Char, Char>, initialRotate: Int, rotateBias: Int) {
  private val rotateBias: Int
  private var scrambler: List<Pair<Int, Int>>
  private var stepCount = 0

  init {
    this.rotateBias = rotateBias

    val forward = scrambler.map { calcShift(it.key, it.value) }

    val reflect = scrambler
      .toSortedMap { a, b -> scrambler[a]!! - scrambler[b]!! }
      .map { calcShift(it.value, it.key) }

    this.scrambler = forward.zip(reflect) { a, b -> Pair(a, b) }

    for (i in 1..initialRotate) {
      _rotate()
    }
  }

  fun getForwardShifts(): List<Int> = scrambler.map { it.first }

  fun getReflectShifts(): List<Int> = scrambler.map { it.second }

  private fun _rotate() {
    scrambler = scrambler.drop(1) + scrambler[0]
  }

  fun rotate() {
    stepCount++
    if ((stepCount / 26.0.pow(rotateBias.toDouble())) >= 1.0) {
      _rotate()
      stepCount = 0
    }
  }
}

class Reflector {
  private val shifts: List<Int> = reflector.map { calcShift(it.key, it.value) }

  fun getShifts(): List<Int> = shifts
}

class PlugBoard(shiftMap: Map<Char, Char>) {
  private val shifts: List<Int>

  init {
    val tmpMap = plane.toMutableMap()
    for (map in shiftMap) {
      tmpMap[map.key] = map.value
      tmpMap[map.value] = map.key
    }
    shifts = tmpMap.map { calcShift(it.key, it.value) }
  }

  fun getShifts(): List<Int> = shifts
}

fun calcShift(a: Char, b: Char): Int {
  val diff = b.code - a.code
  return when (diff >= 0) {
    true -> diff
    false -> diff + 26
  }
}
