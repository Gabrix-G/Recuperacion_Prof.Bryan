package RecycleViewHelpers

import Modelo.ClaseConexion
import Modelo.tbDoctor
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.crud_doctor.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.UUID

class Adaptador(private var datos: List<tbDoctor>) : RecyclerView.Adapter<Adaptador.ViewHolder>() {

    fun actualizarpantalla(uuid: String, nuevoNombre: String){
        val index = datos.indexOfFirst { it.UUID_Doctor == uuid }
        datos[index].Nombre_Doctor = nuevoNombre
        notifyDataSetChanged()
    }


    fun eliminarRegistro(nombreDoctor: String, position: Int) {
        GlobalScope.launch(Dispatchers.IO) {
            val objConexion = ClaseConexion().cadenaConexion()

            // Preparar la consulta de eliminación
            val deleteDoc = objConexion?.prepareStatement("DELETE FROM tbDoctor WHERE Nombre_Doctor = ?")
            deleteDoc?.setString(1, nombreDoctor)

            try {
                deleteDoc?.executeUpdate()
                // Actualiza la lista después de eliminar
                val listaDatos = datos.toMutableList()
                listaDatos.removeAt(position)

                // Actualizar en el hilo principal
                launch(Dispatchers.Main) {
                    datos = listaDatos.toList()
                    notifyItemRemoved(position)
                }
            } catch (e: Exception) {
                e.printStackTrace() // Manejo de errores
            } finally {
                deleteDoc?.close()
                objConexion?.close()
            }
        }
    }

    // Función para editar
    fun editarDoctor(nombreDoctor: String, uuid: String) {
        GlobalScope.launch(Dispatchers.IO) {
            val objConexion = ClaseConexion().cadenaConexion()

            // Prepara la consulta de actualización
            val updateProducto = objConexion?.prepareStatement("UPDATE tbDoctor SET Nombre_Doctor = ? WHERE UUID_Doctor = ?")
            updateProducto?.setString(1, nombreDoctor)  // Usar el parámetro correcto
            updateProducto?.setString(2, uuid)           // Usar el parámetro correcto

            try {
                updateProducto?.executeUpdate()
                objConexion?.commit()
            } catch (e: Exception) {
                e.printStackTrace() // Manejo de errores
            } finally {
                updateProducto?.close()
                objConexion?.close()
            }
            withContext(Dispatchers.Main){
                actualizarpantalla(uuid, nombreDoctor)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vista = LayoutInflater.from(parent.context).inflate(R.layout.activity_item_card, parent, false)
        return ViewHolder(vista)
    }

    override fun getItemCount() = datos.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = datos[position]
        holder.txtNombreCard.text = item.Nombre_Doctor

        holder.imgEliminar.setOnClickListener {
            val contexto = holder.txtNombreCard.context
            val builder = AlertDialog.Builder(contexto)
            builder.setTitle("Eliminar")
            builder.setMessage("¿Estás seguro de que deseas eliminar?")

            // Botones de la alerta
            builder.setPositiveButton("Sí") { dialog, which ->
                eliminarRegistro(item.Nombre_Doctor, position)
            }

            builder.setNegativeButton("No") { dialog, which ->
                dialog.dismiss()
            }
            builder.show()
        }

        // Agregar el listener para editar
        holder.imgEditar.setOnClickListener {
            // Creamos un AlertDialog
            val context = holder.itemView.context
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Actualizar")
            builder.setMessage("¿Desea actualizar el nombre del doctor?")

            // Agregar un cuadro de texto
            val cuadroTexto = EditText(context)
            cuadroTexto.setHint(item.Nombre_Doctor)
            builder.setView(cuadroTexto)

            builder.setPositiveButton("Actualizar") { dialog, which ->
                editarDoctor(cuadroTexto.text.toString(), item.UUID_Doctor)
            }

            builder.setNegativeButton("Cancelar") { dialog, which ->
                dialog.dismiss()
            }
            builder.show()
        }
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val txtNombreCard: TextView = view.findViewById(R.id.txtNombreCard) // Asegúrate de que este ID sea correcto
        val imgEliminar: ImageView = view.findViewById(R.id.imgEliminar) // Asegúrate de tener un ID correcto para la imagen
        val imgEditar: ImageView = view.findViewById(R.id.imgEditar) // Asegúrate de tener un ID correcto para la imagen de editar
    }
}