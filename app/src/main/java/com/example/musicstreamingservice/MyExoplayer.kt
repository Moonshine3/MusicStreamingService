package com.example.musicstreamingservice

import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.example.musicstreamingservice.models.SongModel
import com.google.firebase.firestore.FirebaseFirestore

object MyExoplayer {

    // Змінні для зберігання інстанції ExoPlayer та поточної пісні
    private var exoPlayer: ExoPlayer? = null
    private var currentSong: SongModel? = null

    // Функція для отримання поточної пісні
    fun getCurrentSong(): SongModel? {
        return currentSong
    }

    // Функція для отримання інстанції ExoPlayer
    fun getInstance(): ExoPlayer? {
        return exoPlayer
    }

    // Функція для запуску відтворення пісні
    fun startPlaying(context: Context, song: SongModel) {
        // Якщо ExoPlayer ще не створений, створюємо його
        if (exoPlayer == null)
            exoPlayer = ExoPlayer.Builder(context).build()

        // Якщо нова пісня відрізняється від поточної, починаємо відтворення
        if (currentSong != song) {
            currentSong = song
            updateCount() //

            // Отримуємо URL пісні та починаємо відтворення
            currentSong?.url?.apply {
                val mediaItem = MediaItem.fromUri(this)
                exoPlayer?.setMediaItem(mediaItem)
                exoPlayer?.prepare()
                exoPlayer?.play()
            }
        }
    }

    // Функція для оновлення лічильника відтворень
    fun updateCount() {
        currentSong?.id?.let { id ->
            FirebaseFirestore.getInstance().collection("songs")
                .document(id)
                .get().addOnSuccessListener {
                    var latestCount = it.getLong("count")
                    if (latestCount == null) {
                        latestCount = 1L
                    } else {
                        latestCount = latestCount + 1
                    }

                    FirebaseFirestore.getInstance().collection("songs")
                        .document(id)
                        .update(mapOf("count" to latestCount))
                }
        }
    }
}
