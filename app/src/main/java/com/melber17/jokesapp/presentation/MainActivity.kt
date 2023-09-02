package com.melber17.jokesapp.presentation

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.melber17.jokesapp.JokeApp
import com.melber17.jokesapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var viewModel: MainViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)

        viewModel = (application as JokeApp).viewModel

        binding.showFavoriteCheckBox.setOnCheckedChangeListener { _, isChecked ->
           viewModel.changeFavorite(isChecked)
        }

        binding.favoriteButton.setOnClickListener {
           viewModel.changeJokeStatus()
        }

        binding.actionButton.setOnClickListener {
            binding.actionButton.isEnabled = false
            binding.progressBar.visibility = View.VISIBLE
            viewModel.getJoke()
        }

        viewModel.init(object: JokeUICallback {
            override fun provideText(text: String) = runOnUiThread() {
                binding.actionButton.isEnabled = true
                binding.progressBar.visibility = View.INVISIBLE
                binding.textViewJoke.text = text
            }

            override fun provideIconResId(iconResId: Int) = runOnUiThread() {
                binding.favoriteButton.setImageResource(iconResId)
            }

        })
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.clear()
    }
}