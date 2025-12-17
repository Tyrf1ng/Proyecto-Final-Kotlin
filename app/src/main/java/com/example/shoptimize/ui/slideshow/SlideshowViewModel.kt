package com.example.shoptimize.ui.slideshow

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.shoptimize.R
import com.example.shoptimize.data.Usuario
import com.example.shoptimize.data.database.ShoptimizeDatabase
import com.example.shoptimize.data.repository.UsuarioRepository
import kotlinx.coroutines.launch

class SlideshowViewModel(application: Application) : AndroidViewModel(application) {

    private val database = ShoptimizeDatabase.getInstance(application)
    private val repository = UsuarioRepository(database.usuarioDao())

    private val _profileName = MutableLiveData<String>().apply {
        value = "Mi Nombre"
    }
    val profileName: LiveData<String> = _profileName

    private val _selectedAvatarResId = MutableLiveData<Int>().apply {
        value = R.drawable.avatar_1
    }
    val selectedAvatarResId: LiveData<Int> = _selectedAvatarResId
    
    private var currentUsuarioId: Int = 0

    init {
        viewModelScope.launch {
            val usuario = repository.getUsuarioById(1)
            if (usuario != null) {
                currentUsuarioId = usuario.id
                _profileName.value = usuario.nombre
                _selectedAvatarResId.value = usuario.avatarResId
            } else {
                // Crear usuario por defecto
                val nuevoUsuario = Usuario(nombre = "Mi Nombre", avatarResId = R.drawable.avatar_1)
                currentUsuarioId = repository.insertUsuario(nuevoUsuario).toInt()
            }
        }
    }

    fun updateProfileName(newName: String) {
        _profileName.value = newName
    }

    fun updateAvatarResId(resId: Int) {
        _selectedAvatarResId.value = resId
    }

    fun saveProfile(): String {
        viewModelScope.launch {
            val nombre = _profileName.value ?: "Mi Nombre"
            val avatarResId = _selectedAvatarResId.value ?: R.drawable.avatar_1
            
            if (currentUsuarioId > 0) {
                repository.updateUsuarioNombre(currentUsuarioId, nombre)
                repository.updateUsuarioAvatar(currentUsuarioId, avatarResId)
            } else {
                val nuevoUsuario = Usuario(nombre = nombre, avatarResId = avatarResId)
                currentUsuarioId = repository.insertUsuario(nuevoUsuario).toInt()
            }
        }
        return "Perfil guardado: ${_profileName.value}"
    }
}