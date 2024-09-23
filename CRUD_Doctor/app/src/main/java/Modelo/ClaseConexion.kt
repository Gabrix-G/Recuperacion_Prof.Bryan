package com.example.crud_doctor

import java.lang.Exception
import java.sql.Connection
import java.sql.DriverManager

class ClaseConexion {

    fun cadenaConexion(): Connection? {
        return try {
            val url = "jdbc:oracle:thin:@192.168.1.10:1521:xe"
            val usuario = "SYSTEM"
            val contrasena = "ITR2024"

            DriverManager.getConnection(url, usuario, contrasena)
        } catch (e: Exception) {
            println("Error: ${e.message}")
            null  // Devuelve null si hay un error
        }
    }
}