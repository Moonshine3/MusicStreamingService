package com.example.musicstreamingservice

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.musicstreamingservice.adapter.SongsListAdapter
import com.example.musicstreamingservice.databinding.ActivitySongsListBinding
import com.example.musicstreamingservice.models.CategoryModel

class SongsListActivity : AppCompatActivity() {

    // Зберігаємо інформацію про обрану категорію
    companion object {
        lateinit var category: CategoryModel
    }

    // Змінна для прив'язки до елементів інтерфейсу
    lateinit var binding: ActivitySongsListBinding

    // Адаптер для списку пісень
    lateinit var songsListAdapter: SongsListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Прив'язка до елементів інтерфейсу
        binding = ActivitySongsListBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Назва категорії
        binding.nameTextView.text = category.name

        // Обкладинка категорії з округленням кутів
        Glide.with(binding.coverImageView)
            .load(category.coverUrl)
            .apply(
                RequestOptions().transform(RoundedCorners(32))
            )
            .into(binding.coverImageView)

        // Анімований GIF-фон за допомогою Glide
        Glide.with(this)
            .asGif()
            .load(R.drawable.animback) // Замінити на ваш реальний GIF-файл у res/drawable
            .into(binding.gifBackground)

        // Налаштувати список пісень
        setupSongsListRecyclerView()
    }

    // Налаштування списку пісень
    fun setupSongsListRecyclerView() {
        // Адаптер для списку пісень з списком пісень з обраної категорії
        songsListAdapter = SongsListAdapter(category.songs)

        //LinearLayoutManager для списку пісень
        binding.songsListRecyclerView.layoutManager = LinearLayoutManager(this)

        // Адаптер для списку пісень
        binding.songsListRecyclerView.adapter = songsListAdapter
    }
}

