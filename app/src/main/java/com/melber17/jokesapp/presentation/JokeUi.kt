package com.melber17.jokesapp.presentation

import com.melber17.jokesapp.R

 interface JokeUi {
     fun show(callback: JokeUICallback)
     abstract  class Abstract(
        private val text: String,
        private val punchline: String,
        private val iconResId: Int
    ): JokeUi {
         override fun show(callback: JokeUICallback) = with(callback) {
             provideText("$text\n$punchline")
             provideIconResId(iconResId)
         }
     }

        class Base(text: String, punchline: String) : Abstract(
            text, punchline,
            R.drawable.ic_favorite_empty_48
        ) {
        }

        class Favorite(text: String, punchline: String) : Abstract(
            text, punchline,
            R.drawable.ic_favorite_filled_48
        ) {
        }

        class Failed(text: String) : Abstract(text, "", 0) {
        }
}

