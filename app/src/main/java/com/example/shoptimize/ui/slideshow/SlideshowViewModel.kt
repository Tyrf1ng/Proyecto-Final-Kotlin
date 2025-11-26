package com.example.shoptimize.ui.slideshow

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.shoptimize.R

class SlideshowViewModel : ViewModel() {

    private val _profileName = MutableLiveData<String>().apply {
        value = "Mi Nombre"
    }
    val profileName: LiveData<String> = _profileName

    private val _selectedAvatarResId = MutableLiveData<Int>().apply {
        value = R.drawable.avatar_1
    }
    val selectedAvatarResId: LiveData<Int> = _selectedAvatarResId

    fun updateProfileName(newName: String) {
        _profileName.value = newName
    }

    fun updateAvatarResId(resId: Int) {
        _selectedAvatarResId.value = resId
    }

    fun saveProfile(): String {
        return "Perfil guardado: ${_profileName.value}"
    }
}