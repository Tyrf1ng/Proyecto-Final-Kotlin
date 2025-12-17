package com.example.shoptimize.data.repository

import androidx.lifecycle.LiveData
import com.example.shoptimize.data.Usuario
import com.example.shoptimize.data.dao.UsuarioDao
import kotlinx.coroutines.flow.Flow

class UsuarioRepository(private val usuarioDao: UsuarioDao) {
    
    val allUsuarios: Flow<List<Usuario>> = usuarioDao.getAllUsuarios()
    val allUsuariosLiveData: LiveData<List<Usuario>> = usuarioDao.getAllUsuariosLiveData()
    
    suspend fun getUsuarioById(id: Int): Usuario? {
        return usuarioDao.getUsuarioById(id)
    }
    
    fun getUsuarioByIdFlow(id: Int): Flow<Usuario?> {
        return usuarioDao.getUsuarioByIdFlow(id)
    }
    
    suspend fun insertUsuario(usuario: Usuario): Long {
        return usuarioDao.insertUsuario(usuario)
    }
    
    suspend fun insertUsuarios(vararg usuarios: Usuario) {
        usuarioDao.insertUsuarios(*usuarios)
    }
    
    suspend fun updateUsuario(usuario: Usuario) {
        usuarioDao.updateUsuario(usuario)
    }
    
    suspend fun updateUsuarioNombre(id: Int, nombre: String) {
        usuarioDao.updateUsuarioNombre(id, nombre)
    }
    
    suspend fun updateUsuarioAvatar(id: Int, avatarResId: Int) {
        usuarioDao.updateUsuarioAvatar(id, avatarResId)
    }
    
    suspend fun deleteUsuario(usuario: Usuario) {
        usuarioDao.deleteUsuario(usuario)
    }
    
    suspend fun deleteUsuarioById(id: Int) {
        usuarioDao.deleteUsuarioById(id)
    }
    
    suspend fun deleteAllUsuarios() {
        usuarioDao.deleteAllUsuarios()
    }
}
