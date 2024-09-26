package RecycleViewHelpers

import android.media.Image
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.crud_doctor.R

class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

    //En el ViewHolder mando a llamar los elementos
    val txtNombreCard = view.findViewById<TextView>(R.id.txtNombreCard)
    val imgEditar = view.findViewById<ImageView>(R.id.imgEditar)
    val imgBorrar = view.findViewById<ImageView>(R.id.imgEliminar)
}