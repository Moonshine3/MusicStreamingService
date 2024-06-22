package com.example.musicstreamingservice

import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.musicstreamingservice.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import java.util.regex.Pattern


class LoginActivity : AppCompatActivity() {

    lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Додаємо слухача на кнопку логіну
        binding.loginBtn.setOnClickListener {
            val email = binding.emailEdittext.text.toString()
            val password = binding.passwordEdittext.text.toString()

            // Перевірка, чи коректний email
            if (!Pattern.matches(Patterns.EMAIL_ADDRESS.pattern(), email)) {
                binding.emailEdittext.setError("Недійсна електронна пошта")
                return@setOnClickListener
            }

            // Перевірка, чи пароль не менший за 6 символів
            if (password.length < 6) {
                binding.passwordEdittext.setError("Довжина повинна бути не менше 6 символів")
                return@setOnClickListener
            }

            // Виклик функції для входу через Firebase
            loginWithFirebase(email, password)
        }

        // Додаємо слухача на кнопку переходу до реєстрації
        binding.gotoSignupBtn.setOnClickListener {
            startActivity(Intent(this, SignupActivity::class.java))
        }
    }

    // Функція входу через Firebase
    fun loginWithFirebase(email: String, password: String) {
        setInProgress(true) // Показуємо прогрес бар
        FirebaseAuth.getInstance().signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                setInProgress(false) // Приховуємо прогрес бар
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))
                finish()
            }.addOnFailureListener {
                setInProgress(false) // Приховуємо прогрес бар
                Toast.makeText(applicationContext, "Вхід не вдався", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onResume() {
        super.onResume()
        // Перевіряємо, чи користувач вже авторизований
        FirebaseAuth.getInstance().currentUser?.apply {
            startActivity(Intent(this@LoginActivity, MainActivity::class.java))
            finish()
        }
    }

    // Функція для відображення/приховування прогрес бару
    fun setInProgress(inProgress: Boolean) {
        if (inProgress) {
            binding.loginBtn.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.loginBtn.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        }
    }
}
