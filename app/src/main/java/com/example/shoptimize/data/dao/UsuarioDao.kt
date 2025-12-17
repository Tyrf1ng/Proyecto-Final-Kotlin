package com.example.shoptimize.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.shoptimize.data.Usuario
import kotlinx.coroutines.flow.Flow

@Dao
interface UsuarioDao {
    // Obtener todos los usuarios
    @Query("SELECT * FROM usuarios")
    fun getAllUsuarios(): Flow<List<Usuario>>
    
    @Query("SELECT * FROM usuarios")
    fun getAllUsuariosLiveData(): LiveData<List<Usuario>>
    
    // Obtener usuario por id
    @Query("SELECT * FROM usuarios WHERE id = :id")
    suspend fun getUsuarioById(id: Int): Usuario?
    
    @Query("SELECT * FROM usuarios WHERE id = :id")
    fun getUsuarioByIdFlow(id: Int): Flow<Usuario?>
    
    // Insertar usuario
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsuario(usuario: Usuario): Long
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsuarios(vararg usuarios: Usuario)
    
    // Actualizar usuario
    @Update
    suspend fun updateUsuario(usuario: Usuario)
    
    @Query("UPDATE usuarios SET nombre = :nombre WHERE id = :id")
    suspend fun updateUsuarioNombre(id: Int, nombre: String)
    
    @Query("UPDATE usuarios SET avatarResId = :avatarResId WHERE id = :id")
    suspend fun updateUsuarioAvatar(id: Int, avatarResId: Int)
    
    // Eliminar usuario
    @Delete
    suspend fun deleteUsuario(usuario: Usuario)
    
    @Query("DELETE FROM usuarios WHERE id = :id")
    suspend fun deleteUsuarioById(id: Int)
    
    @Query("DELETE FROM usuarios")
    suspend fun deleteAllUsuarios()
}