package com.matias.tiendavirtualdonapp_kotlin.Vendedor.Productos

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.github.dhaval2404.imagepicker.ImagePicker
import com.matias.tiendavirtualdonapp_kotlin.Adaptadores.AdaptadorImagenSeleccionada
import com.matias.tiendavirtualdonapp_kotlin.Constantes
import com.matias.tiendavirtualdonapp_kotlin.Modelos.ModeloImagenSeleccionada
import com.matias.tiendavirtualdonapp_kotlin.R
import com.matias.tiendavirtualdonapp_kotlin.databinding.ActivityAgregarProductoBinding

class AgregarProductoActivity : AppCompatActivity() {


    private lateinit var binding : ActivityAgregarProductoBinding
    private var imagenUri : Uri?= null

    private lateinit var imagenSelecArrayList : ArrayList<ModeloImagenSeleccionada>
    private lateinit var adaptadorImagenSel : AdaptadorImagenSeleccionada


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgregarProductoBinding.inflate(layoutInflater)
        setContentView(binding.root)


        imagenSelecArrayList = ArrayList()


        binding.imgAgregarProducto.setOnClickListener {
            seleccionarImg()
        }


        cargarImagenes()


    }

    private fun cargarImagenes() {
        adaptadorImagenSel = AdaptadorImagenSeleccionada(this, imagenSelecArrayList)
        binding.RVImagenesProducto.adapter = adaptadorImagenSel
    }


    private fun seleccionarImg(){
        ImagePicker.with(this)
            .crop()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .createIntent { intent->
                resultadoImg.launch(intent)
            }
    }

    private val resultadoImg =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()){resultado->
            if (resultado.resultCode == Activity.RESULT_OK){
                val data = resultado.data
                imagenUri = data!!.data
                val tiempo = "${Constantes().obtenerTiempoD()}"

                val modeloImgSel = ModeloImagenSeleccionada(tiempo, imagenUri, null, false)
                imagenSelecArrayList.add(modeloImgSel)
                cargarImagenes()

            } else {
                Toast.makeText(this, "Acción cancelada", Toast.LENGTH_SHORT).show()
            }
        }

}