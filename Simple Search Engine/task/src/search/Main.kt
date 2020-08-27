package search

import java.io.File
import java.util.Scanner


fun printMenu() {
    println("=== Menu ===\n" +
            "1. Search information.\n" +
            "2. Print all data.\n" +
            "0. Exit.\n")
}

fun searchPeople(file: File, invertIndex: MutableMap<String, MutableList<Int>>, scan: Scanner) {
    println("Select a matching strategy: ALL, ANY, NONE")
    val mode = scan.nextLine().toLowerCase()
    println()
    println("Enter a name or email to search all suitable people.")
    val searchQuery = scan.nextLine().toLowerCase().split(" ").filter { it in invertIndex.keys }

    val lines: Set<Int> = when (mode) {
        "all" -> {
            if (searchQuery.isNotEmpty()) searchQuery.fold(invertIndex[searchQuery[0]]!!.toSet()) { res, item -> res intersect invertIndex[item]!!.toSet() }
            else setOf()
        }
        "any" -> {
            if (searchQuery.isNotEmpty()) searchQuery.fold(setOf()) { res, item -> res union invertIndex[item]!!.toSet() }
            else setOf()
        }
        "none" -> {
            val allIndexes = invertIndex.map{ it -> it.value }.fold(setOf<Int>()) { res, item -> res union item.toSet() }
            allIndexes subtract searchQuery.fold(setOf()) { res, item -> res union invertIndex[item]!!.toSet() }
        }
        else -> setOf()
    }
    if (lines.isNotEmpty()) {
        println("${lines.count()} persons found:")
        var count = 0
        file.forEachLine {
            if (count in lines) println(it)
            count++
        }
    } else {
        println("No matching people found.")
    }
}

fun printPeopleData(file: File) {
    println("=== List of people ===")
    file.forEachLine { println(it) }
}

fun createInvertedIndex(file: File): MutableMap<String, MutableList<Int>> {
    val invertIndex = mutableMapOf<String, MutableList<Int>>()
    var count = 0
    file.forEachLine {
        val wordsInLine = it.toLowerCase().split(" ")
        for (word in wordsInLine) {
            if (word !in invertIndex.keys) invertIndex[word] = mutableListOf(count)
            else invertIndex[word]?.add(count)
        }
        count++
    }
    return invertIndex
}

fun main(args: Array<String>) {
    val scan = Scanner(System.`in`)
    val file = File(args[1])
    val invertIndex = createInvertedIndex(file = file)
    whenBreak@ do {
        println()
        printMenu()
        when (scan.nextLine().toInt()) {
            1 -> searchPeople(file = file, invertIndex = invertIndex, scan = scan)
            2 -> printPeopleData(file = file)
            0 -> break@whenBreak
            else -> println("Incorrect option! Try again.")
        }
    } while (true)
    println("Bye!")
}
