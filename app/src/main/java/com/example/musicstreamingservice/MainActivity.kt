package com.example.musicstreamingservice

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.PopupMenu
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.example.musicstreamingservice.adapter.CategoryAdapter
import com.example.musicstreamingservice.adapter.SectionSongListAdapter
import com.example.musicstreamingservice.databinding.ActivityMainBinding
import com.example.musicstreamingservice.models.CategoryModel
import com.example.musicstreamingservice.models.SongModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.toObjects

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var categoryAdapter: CategoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Завантаження GIF зображення як фон
        Glide.with(this)
            .asGif()
            .load(R.drawable.animback) // Можна завантажити з URL використовуючи рядок URL
            .into(binding.gifBackground)

        // Отримання категорій з Firebase Firestore
        getCategories()

        // Налаштування секцій
        setupSection("section_1", binding.section1MainLayout, binding.section1Title, binding.section1RecyclerView)
        setupSection("section_2", binding.section2MainLayout, binding.section2Title, binding.section2RecyclerView)
        setupSection("section_3", binding.section3MainLayout, binding.section3Title, binding.section3RecyclerView)
        setupMostlyPlayed("mostly_played", binding.mostlyPlayedMainLayout, binding.mostlyPlayedTitle, binding.mostlyPlayedRecyclerView)

        // Обробник натискання на кнопку опцій
        binding.optionBtn.setOnClickListener {
            showPopupMenu()
        }
    }

    // Відображення контекстного меню
    fun showPopupMenu() {
        val popupMenu = PopupMenu(this, binding.optionBtn)
        val inflator = popupMenu.menuInflater
        inflator.inflate(R.menu.option_menu, popupMenu.menu)
        popupMenu.show()
        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.logout -> {
                    logout()
                    true
                }
            }
            false
        }
    }

    // Функція виходу з облікового запису
    fun logout() {
        MyExoplayer.getInstance()?.release()
        FirebaseAuth.getInstance().signOut()
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }

    override fun onResume() {
        super.onResume()
        showPlayerView()
    }

    // Відображення програвача
    fun showPlayerView() {
        binding.playerView.setOnClickListener {
            startActivity(Intent(this, PlayerActivity::class.java))
        }
        MyExoplayer.getCurrentSong()?.let {
            binding.playerView.visibility = View.VISIBLE
            binding.songTitleTextView.text = "Now is Playing: " + it.title
            Glide.with(binding.songCoverImageView).load(it.coverUrl)
                .apply(
                    RequestOptions().transform(RoundedCorners(32))
                ).into(binding.songCoverImageView)
        } ?: run {
            binding.playerView.visibility = View.GONE
        }
    }

    // Отримання категорій з Firebase Firestore
    private fun getCategories() {
        FirebaseFirestore.getInstance().collection("category")
            .get()
            .addOnSuccessListener { snapshot ->
                val categoryList = snapshot.toObjects<CategoryModel>()
                setupCategoryRecyclerView(categoryList)
            }
    }

    // Налаштування RecyclerView для категорій
    private fun setupCategoryRecyclerView(categoryList: List<CategoryModel>) {
        categoryAdapter = CategoryAdapter(categoryList)
        binding.categoriesRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.categoriesRecyclerView.adapter = categoryAdapter
    }

    // Налаштування секцій
    fun setupSection(id: String, mainLayout: RelativeLayout, titleView: TextView, recyclerView: RecyclerView) {
        FirebaseFirestore.getInstance().collection("sections")
            .document(id)
            .get().addOnSuccessListener {
                val section = it.toObject(CategoryModel::class.java)
                section?.apply {
                    mainLayout.visibility = View.VISIBLE
                    titleView.text = name
                    recyclerView.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
                    recyclerView.adapter = SectionSongListAdapter(songs)
                    mainLayout.setOnClickListener {
                        SongsListActivity.category = section
                        startActivity(Intent(this@MainActivity, SongsListActivity::class.java))
                    }
                }
            }
    }

    // Налаштування секції "Найбільш Проїграні Треки"
    fun setupMostlyPlayed(id: String, mainLayout: RelativeLayout, titleView: TextView, recyclerView: RecyclerView) {
        FirebaseFirestore.getInstance().collection("sections")
            .document(id)
            .get().addOnSuccessListener {
                // Отримання найбільш відтворюваних пісень
                FirebaseFirestore.getInstance().collection("songs")
                    .orderBy("count", Query.Direction.DESCENDING)
                    .limit(5)
                    .get().addOnSuccessListener { songListSnapshot ->
                        val songsModelList = songListSnapshot.toObjects<SongModel>()
                        val songsIdList = songsModelList.map { it.id }.toList()
                        val section = it.toObject(CategoryModel::class.java)
                        section?.apply {
                            section.songs = songsIdList
                            mainLayout.visibility = View.VISIBLE
                            titleView.text = name
                            recyclerView.layoutManager = LinearLayoutManager(this@MainActivity, LinearLayoutManager.HORIZONTAL, false)
                            recyclerView.adapter = SectionSongListAdapter(songs)
                            mainLayout.setOnClickListener {
                                SongsListActivity.category = section
                                startActivity(Intent(this@MainActivity, SongsListActivity::class.java))
                            }
                        }
                    }
            }
    }
}
