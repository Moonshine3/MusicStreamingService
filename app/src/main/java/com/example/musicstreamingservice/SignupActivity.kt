package com.example.musicstreamingservice

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Patterns
import android.view.View
import android.widget.Toast
import com.example.musicstreamingservice.databinding.ActivitySignupBinding
import com.google.firebase.auth.FirebaseAuth
import java.util.regex.Pattern

class SignupActivity : AppCompatActivity() {

    // Змінна для прив'язки до макету
    lateinit var binding: ActivitySignupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Налаштування кнопки створення облікового запису
        binding.createAccountBtn.setOnClickListener {

            val email = binding.emailEdittext.text.toString()
            val password = binding.passwordEdittext.text.toString()
            val confirmPassword = binding.confirmPasswordEdittext.text.toString()

            // Перевірка валідності email
            if (!Pattern.matches(Patterns.EMAIL_ADDRESS.pattern(), email)) {
                binding.emailEdittext.setError("Invalid Email, Enter The Real One 😛")
                return@setOnClickListener
            }

            // Перевірка довжини пароля
            if (password.length < 6) {
                binding.passwordEdittext.setError("The Length Of Password Need To Be > 6 Char 🙂")
                return@setOnClickListener
            }

            // Перевірка підтвердження пароля
            if (!password.equals(confirmPassword)) {
                binding.confirmPasswordEdittext.setError("Passwords Does Not Match 🙃")
                return@setOnClickListener
            }

            // Створення облікового запису за допомогою Firebase
            createAccountWithFirebase(email, password)
        }

        // Налаштування кнопки переходу на екран входу
        binding.gotoLoginBtn.setOnClickListener {
            finish() // Повертаємось на екран входу
        }
    }

    // Створення облікового запису за допомогою Firebase
    fun createAccountWithFirebase(email: String, password: String) {
        setInProgress(true)
        FirebaseAuth.getInstance().createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                setInProgress(false)
                Toast.makeText(applicationContext, "User Created Successfully 😄", Toast.LENGTH_SHORT).show()
                finish()
            }.addOnFailureListener {
                setInProgress(false)
                Toast.makeText(applicationContext, "Unfortunately, Your Account Does Not Created 😢 ", Toast.LENGTH_SHORT).show()
            }
    }

    // Відображення процесу створення облікового запису
    fun setInProgress(inProgress: Boolean) {
        if (inProgress) {
            binding.createAccountBtn.visibility = View.GONE
            binding.progressBar.visibility = View.VISIBLE
        } else {
            binding.createAccountBtn.visibility = View.VISIBLE
            binding.progressBar.visibility = View.GONE
        }
    }
}
