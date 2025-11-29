package com.example.shoptimize.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.shoptimize.data.Usuario

interface UsuarioDao {
    //obtener todos los usuarios
    @Query("Select * From usuarios")
    fun getUsuarios(): LiveData<List<Usuario>>
    //obtener usuario por id
    @Query("SELECT * FROM usuarios WHERE id = :id")
    fun getUsuarioById(id: Int): LiveData<Usuario>
    //insertar usuario
    @Insert
    fun insertUsuario(vararg usuario: Usuario)
    //modificar nombre usuario
    @Query("UPDATE usuarios SET nombre = :nombre WHERE id = :id")
    fun updateUsuario(id: Int, nombre: String)
}