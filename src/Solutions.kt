@file:Suppress("unused")

import java.io.File

fun day1Part1(): Int {
    return File("input/Day1Part1.txt").readLines()
        .map { it.toInt() }
        .map { it / 3 - 2 }
        .sum()
}

fun day1Part2(): Int {
    return File("input/Day1Part1.txt").readLines()
        .map { it.toInt() }
        .map { calculateFuel(it) }
        .sum()
}

private fun calculateFuel(moduleMass: Int): Int {
    var fuelMass = moduleMass / 3 - 2
    var totalFuel = if (fuelMass > 0) fuelMass else 0

    while (fuelMass > 0) {
        fuelMass = fuelMass / 3 - 2
        totalFuel += if (fuelMass > 0) fuelMass else 0
    }

    return totalFuel
}

fun day2Part1(): Int {
    val program = File("input/Day2Part1.txt").readText().split(',').map { it.toInt() }.toMutableList()
    program[1] = 12
    program[2] = 2

    return getProgramOutput(program)
}

fun day2Part2(): Int {
    val program = File("input/Day2Part1.txt").readText().split(',').map { it.toInt() }.toMutableList()

    for (noun in 0..99) {
        for (verb in 0..99) {
            program[1] = noun
            program[2] = verb

            if (getProgramOutput(program.toMutableList()) == 19690720) return 100 * noun + verb
        }
    }

    return -1
}

private fun getProgramOutput(program: MutableList<Int>): Int {
    var pointer = 0
    var opCode = program[pointer]

    while (opCode != 99) {
        val noun = program[pointer + 1]
        val verb = program[pointer + 2]
        val outputPosition = program[pointer + 3]

        if (opCode == 1) {
            program[outputPosition] = program[noun] + program[verb]
        } else {
            program[outputPosition] = program[noun] * program[verb]
        }

        pointer += 4
        opCode = program[pointer]
    }

    return program[0]
}

fun day3Part1(): Int {
    val (firstWireCoordinates, secondWireCoordinates) = getBothWireCoordinates()

    return firstWireCoordinates.intersect(secondWireCoordinates)
        .map { getManhattanDistance(Point(0, 0), it) }
        .min()!!
}

fun day3Part2(): Int {
    val (firstWireCoordinates, secondWireCoordinates) = getBothWireCoordinates()

    return firstWireCoordinates.intersect(secondWireCoordinates).map { intersection ->
        firstWireCoordinates.slice(0 .. firstWireCoordinates.indexOfFirst { it == intersection }).size +
            secondWireCoordinates.slice(0 .. secondWireCoordinates.indexOfFirst { it == intersection }).size }
        .min()!!
}

data class Point(val x: Int, val y: Int)
enum class WireDirection { U, D, L, R }
data class WireSegment(val direction: WireDirection, val length: Int)
data class BothWireCoordinates(val firstWireCoordinates: List<Point>, val secondWireCoordinates: List<Point>)

private fun getCoordinatesForWirePath(wirePath: List<WireSegment>): List<Point> {
    val coordinates = mutableListOf<Point>()
    var currentPoint = Point(0, 0)

    for (segment in wirePath) {
        when (segment.direction) {
            WireDirection.U -> {
                (1 .. segment.length).forEach { coordinates.add(Point(currentPoint.x, currentPoint.y + it)) }
                currentPoint = Point(currentPoint.x, currentPoint.y + segment.length)
            }
            WireDirection.D -> {
                (1 .. segment.length).forEach { coordinates.add(Point(currentPoint.x, currentPoint.y - it)) }
                currentPoint = Point(currentPoint.x, currentPoint.y - segment.length)
            }
            WireDirection.L -> {
                (1 .. segment.length).forEach { coordinates.add(Point(currentPoint.x - it, currentPoint.y)) }
                currentPoint = Point(currentPoint.x - segment.length, currentPoint.y)
            }
            WireDirection.R -> {
                (1 .. segment.length).forEach { coordinates.add(Point(currentPoint.x + it, currentPoint.y)) }
                currentPoint = Point(currentPoint.x + segment.length, currentPoint.y)
            }
        }
    }

    return coordinates
}

private fun getBothWireCoordinates(): BothWireCoordinates {
    val firstWirePath = File("input/Day3Part1.txt").readLines()[0]
        .split(',')
        .map { WireSegment(WireDirection.valueOf(it.slice(0..0)), it.slice(1 until it.length).toInt()) }

    val secondWirePath = File("input/Day3Part1.txt").readLines()[1]
        .split(',')
        .map { WireSegment(WireDirection.valueOf(it.slice(0..0)), it.slice(1 until it.length).toInt()) }

    return BothWireCoordinates(getCoordinatesForWirePath(firstWirePath), getCoordinatesForWirePath(secondWirePath))
}

private fun getManhattanDistance(firstPoint: Point, secondPoint: Point): Int {
    return kotlin.math.abs(firstPoint.x - secondPoint.x) + kotlin.math.abs(firstPoint.y - secondPoint.y)
}

fun day4Part1(): Int {
    return (125730 .. 579381).filter { isValidPasswordPart1(getDigits(it)) }.count()
}

fun day4Part2(): Int {
    return (125730 .. 579381).filter { isValidPasswordPart2(getDigits(it)) }.count()
}

private fun getDigits(n: Int): List<Int> {
    var num = n
    val digits = mutableListOf<Int>()

    while (num > 0) {
        digits.add(num % 10)
        num /= 10
    }

    return digits.reversed()
}

private fun isValidPasswordPart1(password: List<Int>): Boolean {
    var hasAdjacentDigits = false

    val iter = password.listIterator()
    var firstDigit = iter.next()

    while(iter.hasNext()) {
        val secondDigit = iter.next()
        if (secondDigit == firstDigit) hasAdjacentDigits = true
        if (secondDigit < firstDigit) return false

        firstDigit = secondDigit
    }

    return hasAdjacentDigits
}

private fun isValidPasswordPart2(password: List<Int>): Boolean {
    var hasTwoAdjacentDigits = false

    val iter = password.listIterator()
    var firstDigit = iter.next()
    var currentGroupingLength = 1

    while(iter.hasNext()) {
        val secondDigit = iter.next()

        if (secondDigit == firstDigit) {
            currentGroupingLength++
        } else if (secondDigit < firstDigit) {
            return false
        } else {
            if (currentGroupingLength == 2) {
                hasTwoAdjacentDigits = true
            }

            currentGroupingLength = 1
        }

        firstDigit = secondDigit
    }

    if (currentGroupingLength == 2) {
        hasTwoAdjacentDigits = true
    }

    return hasTwoAdjacentDigits
}

fun main() {
    print(day4Part2())
}