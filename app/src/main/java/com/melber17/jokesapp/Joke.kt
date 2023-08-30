package com.melber17.jokesapp


abstract class Joke(private val text: String, private val punchline: String, private val iconResId: Int) {


    fun show(callback: TextCallback) = with(callback) {
        provideText("$text\n$punchline")
        provideIconResId(iconResId)
    }


    class Base(text: String, punchline: String) : Joke(text, punchline, R.drawable.ic_favorite_empty_48) {
    }

    class Favorite(text: String, punchline: String) : Joke(text, punchline, R.drawable.ic_favorite_filled_48) {
    }

    class Failed(text: String) : Joke(text, "", 0) {
    }
}

