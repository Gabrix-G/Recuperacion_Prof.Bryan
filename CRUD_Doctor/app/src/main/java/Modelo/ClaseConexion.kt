package Modelo

import java.sql.Connection
import java.sql.DriverManager

class ClaseConexion {
    fun cadenaConexion(): Connection? {
        return try {
            val url = "jdbc:oracle:thin:@192.168.1.10:1521:xe"
            val user = "SYSTEM"
            val password = "ITR2024"
            DriverManager.getConnection(url, user, password)
        } catch (e: Exception) {
            e.printStackTrace() // Imprimir el stack trace completo del error.
            null
        }
    }

    fun testConexion() {
        val conexion = cadenaConexion()
        if (conexion != null) {
            println("Conexión exitosa")
            conexion.close()
        } else {
            println("Error en la conexión")
        }
    }
}