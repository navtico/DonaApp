package com.matias.tiendavirtualdonapp_kotlin.Vendedor.Productos

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.matias.tiendavirtualdonapp_kotlin.Adaptadores.AdaptadorImagenSeleccionada
import com.matias.tiendavirtualdonapp_kotlin.Constantes
import com.matias.tiendavirtualdonapp_kotlin.Modelos.ModeloCategoria
import com.matias.tiendavirtualdonapp_kotlin.Modelos.ModeloImagenSeleccionada
import com.matias.tiendavirtualdonapp_kotlin.R
import com.matias.tiendavirtualdonapp_kotlin.databinding.ActivityAgregarProductoBinding

class AgregarProductoActivity : AppCompatActivity() {


    private lateinit var binding : ActivityAgregarProductoBinding
    private var imagenUri : Uri?= null



    private lateinit var imagenSelecArrayList : ArrayList<ModeloImagenSeleccionada>
    private lateinit var adaptadorImagenSel : AdaptadorImagenSeleccionada


    private lateinit var categoriasArrayList : ArrayList<ModeloCategoria>


    private lateinit var progressDialog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAgregarProductoBinding.inflate(layoutInflater)
        setContentView(binding.root)


        cargarCategorias()


        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor")
        progressDialog.setCanceledOnTouchOutside(false)


        binding.etPrecioConDescuentoP.visibility = View.GONE
        binding.etNotaDescuentoP.visibility = View.GONE


        binding.descuentoSwitch.setOnCheckedChangeListener{buttonView, isCheked->
            if (isCheked){
                //switch esta habilitado
                binding.etPrecioConDescuentoP.visibility = View.VISIBLE
                binding.etNotaDescuentoP.visibility = View.VISIBLE
            } else {
                //switch esta deshabilitado
                binding.etPrecioConDescuentoP.visibility = View.GONE
                binding.etNotaDescuentoP.visibility = View.GONE
            }
        }


        imagenSelecArrayList = ArrayList()


        binding.imgAgregarProducto.setOnClickListener {
            seleccionarImg()
        }


        binding.Categoria.setOnClickListener {
            selecCategorias()
        }


        binding.btnAgregarProducto.setOnClickListener {
            validarInfo()
        }



        cargarImagenes()


    }


    private var nombreP = ""
    private var descripcionP = ""
    private var categoriaP = ""
    private var precioP = ""
    private var descuentoHab = false
    private var precioDescP = ""
    private var notaDescP = ""
    private fun validarInfo() {
        nombreP = binding.etNombresP.text.toString().trim()
        descripcionP = binding.etDescripcionP.text.toString().trim()
        categoriaP = binding.Categoria.text.toString().trim()
        precioP = binding.etPrecioP.text.toString().trim()
        descuentoHab = binding.descuentoSwitch.isChecked

        if (nombreP.isEmpty()) {
            binding.etNombresP.error = "Ingrese nombre"
            binding.etNombresP.requestFocus()


        } else if (descripcionP.isEmpty()) {
            binding.etDescripcionP.error = "Ingrese descripcion"
            binding.etDescripcionP.requestFocus()


        } else if (categoriaP.isEmpty()) {
            binding.Categoria.error = "Seleccione una categoria"
            binding.Categoria.requestFocus()


        } else if (precioP.isEmpty()) {
            binding.etPrecioP.error = "Ingrese precio"
            binding.etPrecioP.requestFocus()


        } else if (imagenUri == null){
            Toast.makeText(this, "Seleccione al menos una categoría", Toast.LENGTH_SHORT).show()
        } else {
            //descuentoHab = true
            if (descuentoHab){
                precioDescP = binding.etPrecioConDescuentoP.text.toString().trim()
                notaDescP = binding.etNotaDescuentoP.text.toString().trim()



                if (precioDescP.isEmpty()){
                    binding.etPrecioConDescuentoP.error = "Ingrese precio con descuento"
                    binding.etPrecioConDescuentoP.requestFocus()



                } else if (notaDescP.isEmpty()){
                    binding.etNotaDescuentoP.text.toString().trim()
                    binding.etNotaDescuentoP.requestFocus()
                } else {
                    agregarProducto()
                }
            }
            //descuentoHab = false
            else{
                precioDescP = "0"
                notaDescP = ""
                agregarProducto()
            }
        }

    }

    private fun agregarProducto() {
        progressDialog.setMessage("Agregando producto")
        progressDialog.show()

        var ref = FirebaseDatabase.getInstance().getReference("Productos")
        val keiId = ref.push().key

        val hashMap = HashMap<String, Any>()
        hashMap["id"] = "${keiId}"
        hashMap["nombre"] = "${nombreP}"
        hashMap["descripcion"] = "${descripcionP}"
        hashMap["categoria"] = "${categoriaP}"
        hashMap["precio"] = "${precioP}"
        hashMap["precioDesc"] = "${precioDescP}"
        hashMap["notaDesc"] = "${notaDescP}"

        ref.child(keiId!!)
            .setValue(hashMap)
            .addOnSuccessListener {
                subirImgsStorage(keiId)
            }
            .addOnFailureListener {e->
                Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun subirImgsStorage(keiId: String) {
        for (i in imagenSelecArrayList.indices){
            val modeloImagenSel = imagenSelecArrayList[i]
            val nombreImagen = modeloImagenSel.id
            val rutaImagen = "Productos/$nombreImagen"

            val storageRef = FirebaseStorage.getInstance().getReference(rutaImagen)
            storageRef.putFile(modeloImagenSel.imageUri!!)
                .addOnSuccessListener {taskSnapshot ->
                    val uriTask = taskSnapshot.storage.downloadUrl
                    while (!uriTask.isSuccessful);
                    val urlImgCargada = uriTask.result

                    if (uriTask.isSuccessful){
                        val hashMap = HashMap<String, Any>()
                        hashMap["id"] = "${modeloImagenSel.id}"
                        hashMap["imagenUrl"] = "${urlImgCargada}"


                        val ref = FirebaseDatabase.getInstance().getReference("Productos")
                        ref.child(keiId).child("Imagenes")
                            .child(nombreImagen)
                            .updateChildren(hashMap)
                        progressDialog.dismiss()
                        Toast.makeText(this, "Se agregó el producto", Toast.LENGTH_SHORT).show()
                        limpiarCampos()
                    }

                }
                .addOnFailureListener {e->
                    progressDialog.dismiss()
                    Toast.makeText(this, "${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    private fun limpiarCampos() {
        imagenSelecArrayList.clear()
        adaptadorImagenSel.notifyDataSetChanged()
        binding.etNombresP.setText("")
        binding.etDescripcionP.setText("")
        binding.etPrecioP.setText("")
        binding.Categoria.setText("")
        binding.descuentoSwitch.isChecked = false
        binding.etPrecioConDescuentoP.setText("")
        binding.etNotaDescuentoP.setText("")

    }

    private fun cargarCategorias() {
        categoriasArrayList = ArrayList()
        val ref = FirebaseDatabase.getInstance().getReference("Categorias").orderByChild("categoria")
        ref.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                categoriasArrayList.clear()
                for (ds in snapshot.children){
                    val modelo = ds.getValue(ModeloCategoria::class.java)
                    categoriasArrayList.add(modelo!!)
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }



    private var idCat = ""
    private var tituloCat = ""
    private fun selecCategorias(){
        val categoriasArray = arrayOfNulls<String>(categoriasArrayList.size)
        for (i in categoriasArray.indices){
            categoriasArray[i] = categoriasArrayList[i].categoria
        }


        val builder = AlertDialog.Builder(this)
        builder.setTitle("Seleccione una categoria")
            .setItems(categoriasArray){dialog, witch->

                idCat = categoriasArrayList[witch].id
                tituloCat = categoriasArrayList[witch].categoria
                binding.Categoria.text = tituloCat
            }
            .show()

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