package com.example.crud_doctor

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import java.sql.PreparedStatement

lateinit var txtNombre: EditText
lateinit var txtEdad: EditText
lateinit var txtPeso: EditText
lateinit var txtCorreo: EditText
lateinit var btnAgregar: Button
lateinit var btnActualizar: Button
lateinit var btnEliminar: Button
lateinit var btnLimpiar: Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        txtNombre = findViewById(R.id.txtNombre)
        txtEdad = findViewById(R.id.txtEdad)
        txtPeso = findViewById(R.id.txtPeso)
        txtCorreo = findViewById(R.id.txtCorreo)
        btnAgregar = findViewById(R.id.btnAgregar)
        btnActualizar = findViewById(R.id.btnActualizar)
        btnEliminar = findViewById(R.id.btnEliminar)
        btnLimpiar = findViewById(R.id.btnLimpiar)




    }
}