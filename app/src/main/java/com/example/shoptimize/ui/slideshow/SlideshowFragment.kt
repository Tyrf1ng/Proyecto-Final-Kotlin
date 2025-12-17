package com.example.shoptimize.ui.slideshow

import android.os.Bundle
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.example.shoptimize.R
import com.example.shoptimize.databinding.FragmentSlideshowBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder

class SlideshowFragment : Fragment() {

    private var _binding: FragmentSlideshowBinding? = null
    private val binding get() = _binding!!
    private lateinit var viewModel: SlideshowViewModel

    private val avatarsList = listOf(
        R.drawable.avatar_1, R.drawable.avatar_2, R.drawable.avatar_3,
        R.drawable.avatar_4, R.drawable.avatar_5, R.drawable.avatar_6,
        R.drawable.avatar_7, R.drawable.avatar_8, R.drawable.avatar_9,
        R.drawable.avatar_10, R.drawable.avatar_11, R.drawable.avatar_12,
        R.drawable.avatar_13, R.drawable.avatar_14, R.drawable.avatar_15,
        R.drawable.avatar_16
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = ViewModelProvider(this).get(SlideshowViewModel::class.java)
        _binding = FragmentSlideshowBinding.inflate(inflater, container, false)
        val root: View = binding.root

        setupObservers()
        setupListeners()

        return root
    }

    private fun setupObservers() {
        viewModel.profileName.observe(viewLifecycleOwner) { name ->
            binding.tietProfileName.setText(name)
        }

        viewModel.selectedAvatarResId.observe(viewLifecycleOwner) { resId ->
            binding.ivProfileImage.setImageResource(resId)
        }
    }

    private fun setupListeners() {
        binding.btnChangeAvatar.setOnClickListener {
            showAvatarSelectionDialog()
        }

        binding.btnSaveProfile.setOnClickListener {
            val newName = binding.tietProfileName.text.toString().trim()
            if (newName.isEmpty()) {
                Toast.makeText(
                    requireContext(),
                    "Por favor ingrese un nombre",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }
            viewModel.updateProfileName(newName)
            val message = viewModel.saveProfile()
            Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
        }

        binding.btnToggleTheme.setOnClickListener {
            toggleTheme()
        }

        updateThemeButtonLabel(getSavedTheme())
    }

    private fun showAvatarSelectionDialog() {
        val avatarTitles = (1..16).map { "Avatar $it" }.toTypedArray()

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Seleccionar Avatar")
            .setItems(avatarTitles) { _, which ->
                val selectedAvatar = avatarsList[which]
                viewModel.updateAvatarResId(selectedAvatar)
            }
            .show()
    }

    private fun toggleTheme() {
        val prefs = requireContext().getSharedPreferences("shoptimize_prefs", Context.MODE_PRIVATE)
        val current = getSavedTheme()
        val next = if (current == "night") "day" else "night"
        applyTheme(next)
        prefs.edit().putString("theme_mode", next).apply()
        updateThemeButtonLabel(next)
        Toast.makeText(requireContext(), if (next == "night") "Modo oscuro activado" else "Modo claro activado", Toast.LENGTH_SHORT).show()
    }

    private fun applyTheme(mode: String) {
        when (mode) {
            "night" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            "day" -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            else -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
        }
    }

    private fun getSavedTheme(): String {
        val prefs = requireContext().getSharedPreferences("shoptimize_prefs", Context.MODE_PRIVATE)
        return prefs.getString("theme_mode", "system") ?: "system"
    }

    private fun updateThemeButtonLabel(mode: String) {
        binding.btnToggleTheme.text = if (mode == "night") "Usar modo claro" else "Usar modo oscuro"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}