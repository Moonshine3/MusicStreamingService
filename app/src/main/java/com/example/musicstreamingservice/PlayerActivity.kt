package com.example.musicstreamingservice

import android.os.Bundle
import android.view.View
import androidx.annotation.OptIn
import androidx.appcompat.app.AppCompatActivity
import androidx.media3.common.Player
import androidx.media3.common.util.UnstableApi
import androidx.media3.exoplayer.ExoPlayer
import com.bumptech.glide.Glide
import com.example.musicstreamingservice.databinding.ActivityPlayerBinding

class PlayerActivity : AppCompatActivity() {

    // Змінна для прив'язки до макету
    lateinit var binding: ActivityPlayerBinding

    // Змінна для інстанції ExoPlayer
    lateinit var exoPlayer: ExoPlayer

    // Слухач змін стану плеєра
    var playerListener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            super.onIsPlayingChanged(isPlaying)
            showGif(isPlaying) // Показуємо або ховаємо GIF в залежності від стану відтворення
        }
    }

    @OptIn(UnstableApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPlayerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Отримуємо поточну пісню з MyExoplayer і оновлюємо UI
        MyExoplayer.getCurrentSong()?.apply {
            binding.songTitleTextView.text = title
            binding.songSubtitleTextView.text = subtitle

            // Завантажуємо обкладинку пісні за допомогою Glide
            Glide.with(binding.songCoverImageView).load(coverUrl)
                .circleCrop()
                .into(binding.songCoverImageView)

            // Завантажуємо GIF з медіа за допомогою Glide
            Glide.with(binding.songGifImageView).load(R.drawable.media)
                .circleCrop()
                .into(binding.songGifImageView)

            // Отримуємо інстанцію ExoPlayer з MyExoplayer
            exoPlayer = MyExoplayer.getInstance()!!
            binding.playerView.player = exoPlayer // Прив'язуємо ExoPlayer до PlayerView
            binding.playerView.showController() // Показуємо контролери плеєра

            // Додаємо слухача змін стану плеєра
            exoPlayer.addListener(playerListener)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        exoPlayer?.removeListener(playerListener) // Видаляємо слухача при знищенні активності
    }

    // Функція для показу або приховування GIF
    fun showGif(show: Boolean) {
        if (show)
            binding.songGifImageView.visibility = View.VISIBLE
        else
            binding.songGifImageView.visibility = View.INVISIBLE
    }

}
