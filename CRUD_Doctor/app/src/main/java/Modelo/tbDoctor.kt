package Modelo

import java.util.UUID

data class tbDoctor(
    val UUID_Doctor: String,
    var Nombre_Doctor: String,
    val Edad_Doctor: Int,
    val Peso_Doctor: Double,
    val Correo_Doctor: String
)
