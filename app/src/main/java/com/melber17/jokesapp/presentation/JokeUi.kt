package com.melber17.jokesapp.presentation

import com.melber17.jokesapp.R


abstract class JokeUi(
    private val text: String,
    private val punchline: String,
    private val iconResId: Int
) {

    fun show(callback: JokeUICallback) = with(callback) {
        provideText("$text\n$punchline")
        provideIconResId(iconResId)
    }


    class Base(text: String, punchline: String) : JokeUi(
        text, punchline,
        R.drawable.ic_favorite_empty_48
    ) {
    }

    class Favorite(text: String, punchline: String) : JokeUi(
        text, punchline,
        R.drawable.ic_favorite_filled_48
    ) {
    }

    class Failed(text: String) : JokeUi(text, "", 0) {
    }
}

