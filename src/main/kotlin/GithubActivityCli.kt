package com.codehunter.github_activity_kotlin

import java.util.*

class GithubActivityCli {

    fun start() {
        try {
            print("github-activity ")
            val input = Scanner(System.`in`).nextLine()
            requireNotNull(input) { "Invalid input" }

            val command = input.split(" ")[0]
            if (command == "exit") {
                println("Exiting...")
                return
            } else if (input.split(" ").size > 1) {
                println("Invalid command")
            } else {
                val username = input
                val events = GhEventService().getEvents(username)
                println("Output:")
                events.forEach {
                    println(" - ${it.buildMessage()}")
                }
            }
        } catch (e: IllegalArgumentException) {
            println(e.message)
        }
        start()
    }
}