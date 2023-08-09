package com.melber17.jokesapp

class Joke(private val text: String, private val punchline: String) {

    fun toUi() = "$text\n$punchline"
}