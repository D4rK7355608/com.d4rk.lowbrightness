package com.d4rk.lowbrightness.ui.language
import android.content.Context
import android.os.Bundle
import com.d4rk.lowbrightness.databinding.ActivityLanguageBinding
import com.kieronquinn.monetcompat.app.MonetCompatActivity
class LanguageActivity : MonetCompatActivity() {
    private lateinit var binding: ActivityLanguageBinding
    var context: Context? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sharedPreferences = getSharedPreferences("save", MODE_PRIVATE)
        binding.defaultLanguage.isChecked = sharedPreferences.getBoolean("value", true)
        binding.defaultLanguage.setOnCheckedChangeListener { _, _ ->
           if (binding.defaultLanguage.isChecked) {
               for (index in 0 until binding.languageGroup.childCount) {
                   binding.languageGroup.getChildAt(index).isEnabled = false
               }
               val editor = getSharedPreferences("save", MODE_PRIVATE).edit()
               editor.putBoolean("value", true)
               editor.apply()
           } else {
               for (index in 0 until binding.languageGroup.childCount) {
                   binding.languageGroup.getChildAt(index).isEnabled = true
               }
               val editor = getSharedPreferences("save", MODE_PRIVATE).edit()
               editor.putBoolean("value", false)
               editor.apply()
           }
        }
        if(binding.defaultLanguage.isChecked) {
            for (index in 0 until binding.languageGroup.childCount) {
                binding.languageGroup.getChildAt(index).isEnabled = false
            }
        } else {
            for (index in 0 until binding.languageGroup.childCount) {
                binding.languageGroup.getChildAt(index).isEnabled = true
            }
        }
    }
}