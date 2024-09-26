package com.example.crud_doctor

import Modelo.tbDoctor
import RecycleViewHelpers.Adaptador
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.*
import Modelo.ClaseConexion
import java.sql.Connection
import java.util.UUID

class MainActivity : AppCompatActivity() {
    private var connection: Connection? = null
    private lateinit var txtNombre: EditText
    private lateinit var txtEdad: EditText
    private lateinit var txtPeso: EditText
    private lateinit var txtCorreo: EditText
    private lateinit var txtUUID: EditText
    private lateinit var btnAgregar: Button
    private lateinit var btnActualizar: Button
    private lateinit var btnLimpiar: Button
    private lateinit var rcvDoctor: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        initViews()
        establecerConexion()
        setupListeners()
        obtenerDoctores() // Cargar doctores al iniciar
    }

    private fun initViews() {
        txtNombre = findViewById(R.id.txtNombre)
        txtEdad = findViewById(R.id.txtEdad)
        txtPeso = findViewById(R.id.txtPeso)
        txtCorreo = findViewById(R.id.txtCorreo)
        txtUUID = findViewById(R.id.txtUUID)
        btnAgregar = findViewById(R.id.btnAgregar)
        btnActualizar = findViewById(R.id.btnActualizar)
        btnLimpiar = findViewById(R.id.btnLimpiar)
        rcvDoctor = findViewById(R.id.rcvDoctor)

        rcvDoctor.layoutManager = LinearLayoutManager(this)
    }

    private fun establecerConexion() {
        CoroutineScope(Dispatchers.IO).launch {
            connection = ClaseConexion().cadenaConexion()
            connection?.autoCommit = false
            withContext(Dispatchers.Main) {
                showToast(if (connection != null) "Conexión exitosa" else "Error al conectar")
                obtenerDoctores() // Obtener doctores después de la conexión
            }
        }
    }

    private fun setupListeners() {
        btnAgregar.setOnClickListener { agregarDoctor() }
        btnActualizar.setOnClickListener { actualizarDoctor() }
        btnLimpiar.setOnClickListener { limpiarCampos() }
    }

    private fun obtenerDoctores() {
        CoroutineScope(Dispatchers.IO).launch {
            connection?.let {
                val statement = it.createStatement()
                val resultSet = statement.executeQuery("SELECT * FROM tbDoctor")

                val listaDoctor = mutableListOf<tbDoctor>()
                while (resultSet.next()) {
                    val uuid = resultSet.getString("UUID_Doctor") ?: ""
                    val nombre = resultSet.getString("Nombre_Doctor") ?: ""
                    val edad = resultSet.getInt("Edad_Doctor")
                    val peso = resultSet.getDouble("Peso_Doctor")
                    val correo = resultSet.getString("Correo_Doctor") ?: ""

                    listaDoctor.add(tbDoctor(uuid, nombre, edad, peso, correo))
                }

                withContext(Dispatchers.Main) {
                    val adapter = Adaptador(listaDoctor)
                    rcvDoctor.adapter = adapter
                }
            } ?: run {
                withContext(Dispatchers.Main) {
                    showToast("Error: conexión a la base de datos no establecida")
                }
            }
        }
    }
    private fun agregarDoctor() {
        val nombre = txtNombre.text.toString().trim()
        val edadStr = txtEdad.text.toString().trim()
        val pesoStr = txtPeso.text.toString().trim()
        val correo = txtCorreo.text.toString().trim()

        // Validaciones
        if (nombre.isEmpty()) {
            showToast("El nombre es obligatorio")
            return
        }

        if (edadStr.isEmpty() || edadStr.toIntOrNull() == null) {
            showToast("La edad debe ser un número válido")
            return
        }

        // No se valida el peso, se asignará un valor por defecto si está vacío
        btnAgregar.isEnabled = false // Deshabilitar el botón

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val sql = "INSERT INTO tbDoctor (UUID_Doctor, Nombre_Doctor, Edad_Doctor, Peso_Doctor, Correo_Doctor) VALUES (?, ?, ?, ?, ?)"
                connection?.prepareStatement(sql)?.apply {
                    setString(1, UUID.randomUUID().toString())
                    setString(2, nombre)
                    setInt(3, edadStr.toInt()) // Convertir directamente a Int
                    setDouble(4, if (pesoStr.isNotEmpty()) pesoStr.toDouble() else 0.0) // Asignar 0.0 si está vacío
                    setString(5, correo)
                    executeUpdate()
                    connection?.commit()
                    withContext(Dispatchers.Main) {
                        showToast("Doctor agregado")
                        obtenerDoctores() // Actualizar la lista de doctores
                        btnAgregar.isEnabled = true // Volver a habilitar el botón
                    }
                }
            } catch (e: Exception) {
                Log.e("DBError", "Error: ${e.message}", e)
                connection?.rollback()
                withContext(Dispatchers.Main) {
                    showToast("Error: ${e.message}")
                    btnAgregar.isEnabled = true // Volver a habilitar el botón
                }
            }
        }
    }

    private fun actualizarDoctor() {
        val uuid = txtUUID.text.toString().trim()
        val nombre = txtNombre.text.toString().trim()
        val edad = txtEdad.text.toString().toIntOrNull()
        val pesoStr = txtPeso.text.toString().trim()
        val peso = if (pesoStr.isNotEmpty()) pesoStr.toDouble() else null // Asignar null si está vacío
        val correo = txtCorreo.text.toString().trim()

        if (uuid.isEmpty()) {
            showToast("Por favor, ingresa el UUID del doctor")
            return
        }

        CoroutineScope(Dispatchers.IO).launch {
            try {
                val sql = "UPDATE tbDoctor SET Nombre_Doctor = ?, Edad_Doctor = ?, Peso_Doctor = ?, Correo_Doctor = ? WHERE UUID_Doctor = ?"
                connection?.prepareStatement(sql)?.apply {
                    setString(1, if (nombre.isNotEmpty()) nombre else null)
                    setInt(2, edad ?: 0)
                    // Si el peso es null, se puede manejar según la lógica de tu base de datos
                    setDouble(3, peso ?: 0.0) // Asignar 0.0 si el peso es null
                    setString(4, if (correo.isNotEmpty()) correo else null)
                    setString(5, uuid)
                    val rowsUpdated = executeUpdate()
                    withContext(Dispatchers.Main) {
                        showToast(if (rowsUpdated > 0) "Datos actualizados" else "No se encontró el doctor")
                        obtenerDoctores() // Actualizar la lista de doctores
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    showToast("Error: ${e.message}")
                }
            }
        }
    }

    private fun limpiarCampos() {
        txtNombre.text.clear()
        txtEdad.text.clear()
        txtPeso.text.clear()
        txtCorreo.text.clear()
        txtUUID.text.clear()
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroy() {
        super.onDestroy()
        connection?.close() // Cerrar la conexión al destruir la actividad
    }
}