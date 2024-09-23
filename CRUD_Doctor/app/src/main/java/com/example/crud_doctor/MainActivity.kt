package com.example.crud_doctor

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.*
import Modelo.ClaseConexion
import java.sql.Connection
import java.sql.PreparedStatement

class MainActivity : AppCompatActivity() {
    private var connectSql = ConnectSql()
    private var connection: Connection? = null
    private lateinit var txtNombre: EditText
    private lateinit var txtEdad: EditText
    private lateinit var txtPeso: EditText
    private lateinit var txtCorreo: EditText
    private lateinit var txtUUID: EditText
    private lateinit var btnAgregar: Button
    private lateinit var btnActualizar: Button
    private lateinit var btnEliminar: Button
    private lateinit var btnLimpiar: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        initViews()
        establecerConexion()
        setupListeners()
    }

    private fun initViews() {
        txtNombre = findViewById(R.id.txtNombre)
        txtEdad = findViewById(R.id.txtEdad)
        txtPeso = findViewById(R.id.txtPeso)
        txtCorreo = findViewById(R.id.txtCorreo)
        txtUUID = findViewById(R.id.txtUUID)
        btnAgregar = findViewById(R.id.btnAgregar)
        btnActualizar = findViewById(R.id.btnActualizar)
        btnEliminar = findViewById(R.id.btnEliminar)
        btnLimpiar = findViewById(R.id.btnLimpiar)
    }
    private fun establecerConexion() {
        CoroutineScope(Dispatchers.IO).launch {
            connection = ClaseConexion().cadenaConexion()
            withContext(Dispatchers.Main) {
                if (connection != null) {
                    Toast.makeText(this@MainActivity, "Conexión exitosa", Toast.LENGTH_SHORT).show()
                    // Aquí puedes llamar al método de prueba de conexión si lo deseas
                    ClaseConexion().testConexion() // Llama al test de conexión
                } else {
                    Toast.makeText(this@MainActivity, "Error al conectar", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun setupListeners() {
        btnAgregar.setOnClickListener { agregarDoctor() }
        btnActualizar.setOnClickListener { actualizarDoctor() }
        btnEliminar.setOnClickListener { eliminarDoctor() }
        btnLimpiar.setOnClickListener { limpiarCampos() }
    }

    private fun agregarDoctor() {
        val nombre = txtNombre.text.toString()
        val edad = txtEdad.text.toString().toIntOrNull()
        val peso = txtPeso.text.toString().toDoubleOrNull()
        val correo = txtCorreo.text.toString()

        if (nombre.isNotEmpty() && edad != null && peso != null) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val sql = "INSERT INTO tbDoctor (Nombre_Doctor, Edad_Doctor, Peso_Doctor, Correo_Doctor) VALUES (?, ?, ?, ?)"
                    connection?.prepareStatement(sql)?.apply {
                        setString(1, nombre)
                        setInt(2, edad)
                        setDouble(3, peso)
                        setString(4, correo)
                        val rowsAffected = executeUpdate()
                        Log.d("DB", "Filas afectadas: $rowsAffected")
                        withContext(Dispatchers.Main) {
                            Toast.makeText(this@MainActivity, "Doctor agregado", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    Log.e("DBError", "Error: ${e.message}", e)
                    withContext(Dispatchers.Main) { Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show() }
                }
            }
        } else {
            Toast.makeText(this, "Por favor, completa todos los campos", Toast.LENGTH_SHORT).show()
        }
    }

    private fun actualizarDoctor() {
        val uuid = txtUUID.text.toString()
        val nombre = txtNombre.text.toString()
        val edad = txtEdad.text.toString().toIntOrNull()
        val peso = txtPeso.text.toString().toDoubleOrNull()
        val correo = txtCorreo.text.toString()

        if (uuid.isNotEmpty() && (nombre.isNotEmpty() || edad != null || peso != null || correo.isNotEmpty())) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val sql = "UPDATE tbDoctor SET Nombre_Doctor = ?, Edad_Doctor = ?, Peso_Doctor = ?, Correo_Doctor = ? WHERE UUID_Doctor = ?"
                    connection?.prepareStatement(sql)?.apply {
                        setString(1, nombre)
                        setObject(2, edad)
                        setObject(3, peso)
                        setString(4, correo)
                        setString(5, uuid)
                        val rowsUpdated = executeUpdate()
                        withContext(Dispatchers.Main) {
                            if (rowsUpdated > 0) {
                                Toast.makeText(this@MainActivity, "Datos actualizados", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@MainActivity, "No se encontró el doctor", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Toast.makeText(this, "Por favor, ingresa el UUID del doctor", Toast.LENGTH_SHORT).show()
        }
    }
    private fun obtenerDoctores() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val sql = "SELECT * FROM tbDoctor"
                val statement = connection?.createStatement()
                val resultSet = statement?.executeQuery(sql)

                val doctores = mutableListOf<String>()
                while (resultSet?.next() == true) {
                    val nombre = resultSet.getString("Nombre_Doctor")
                    doctores.add(nombre)
                }

                withContext(Dispatchers.Main) {
                    // Aquí puedes actualizar tu UI con la lista de doctores
                    Toast.makeText(this@MainActivity, "Doctores: ${doctores.joinToString(", ")}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    private fun eliminarDoctor() {
        val uuid = txtUUID.text.toString()
        if (uuid.isNotEmpty()) {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val sql = "DELETE FROM tbDoctor WHERE UUID_Doctor = ?"
                    connection?.prepareStatement(sql)?.apply {
                        setString(1, uuid)
                        val rowsDeleted = executeUpdate()
                        withContext(Dispatchers.Main) {
                            if (rowsDeleted > 0) {
                                Toast.makeText(this@MainActivity, "Doctor eliminado", Toast.LENGTH_SHORT).show()
                            } else {
                                Toast.makeText(this@MainActivity, "No se encontró el doctor", Toast.LENGTH_SHORT).show()
                            }
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@MainActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        } else {
            Toast.makeText(this, "Por favor, ingresa el UUID del doctor", Toast.LENGTH_SHORT).show()
        }
    }

    private fun limpiarCampos() {
        txtNombre.text.clear()
        txtEdad.text.clear()
        txtPeso.text.clear()
        txtCorreo.text.clear()
        txtUUID.text.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        connection?.close()
    }
}