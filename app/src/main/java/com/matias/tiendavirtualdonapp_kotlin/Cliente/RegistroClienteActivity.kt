package com.matias.tiendavirtualdonapp_kotlin.Cliente

import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.matias.tiendavirtualdonapp_kotlin.Constantes
import com.matias.tiendavirtualdonapp_kotlin.R
import com.matias.tiendavirtualdonapp_kotlin.databinding.ActivityRegistroClienteBinding

class RegistroClienteActivity : AppCompatActivity() {


    private lateinit var binding : ActivityRegistroClienteBinding

    private lateinit var firebaseAuth: FirebaseAuth

    private lateinit var progressDialog: ProgressDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroClienteBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firebaseAuth = FirebaseAuth.getInstance()

        progressDialog = ProgressDialog(this)
        progressDialog.setTitle("Espere por favor...")
        progressDialog.setCanceledOnTouchOutside(false)

        /*Evento*/
        binding.btnRegistrarC.setOnClickListener {
            validarInformacion()
        }
    }

    /*Funcion*/
    private var nombres = ""
    private var email = ""
    private var password = ""
    private var cpassword = ""
    private fun validarInformacion() {

        nombres = binding.etNombresC.text.toString().trim()
        email = binding.etEmail.text.toString().trim()
        password = binding.etPassword.text.toString().trim()
        cpassword = binding.etCPassword.text.toString().trim()



        /*Validacion*/
        if (nombres.isEmpty()){
            binding.etNombresC.error = "Ingrese nombres"
            binding.etNombresC.requestFocus()




        }   else if (email.isEmpty()){
            binding.etEmail.error = "Ingrese email"
            binding.etEmail.requestFocus()

        }   else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            binding.etEmail.error = "Email no válido"
            binding.etEmail.requestFocus()




        }   else if (password.isEmpty()){
            binding.etPassword.error = "Ingrese password"
            binding.etPassword.requestFocus()

        }   else if (password.length < 6){
            binding.etPassword.error = "Necesita más de 6 carácteres"
            binding.etPassword.requestFocus()




        }   else if (cpassword.isEmpty()){
            binding.etCPassword.error = "Confirme password"
            binding.etCPassword.requestFocus()

        }   else if (password!=cpassword){
            binding.etCPassword.error = "No coinciden las password"
            binding.etCPassword.requestFocus()



        /*Si_todo_lo_anterior_se_cumple_entonces_se_ejecuta_la_siguente_funcion*/
        } else {
            registrarCliente()
        }
    }




    /*Crear esa funcion de arriba*/
    private fun registrarCliente(){
        progressDialog.setMessage("Creando cuenta")
        progressDialog.show()


        firebaseAuth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                insertarInfoBD()
            }
            .addOnFailureListener { e->
                Toast.makeText(this, "Falló el registro debido a ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }




    /*Crear funcion de arriba*/
    private fun insertarInfoBD() {
    progressDialog.setMessage("Guardando información")

    val uid = firebaseAuth.uid
    val nombresC = nombres
    val emailC = email
    val tiempoRegistro = Constantes().obtenerTiempoD()

    /*Hacer hashmap*/
    val datosCliente = HashMap<String, Any>()

    datosCliente["uid"] = "$uid"
    datosCliente["nombres"] ="$nombresC"
    datosCliente["email"] = "$emailC"
    datosCliente["tRegistro"] = "$tiempoRegistro"
    datosCliente["imagen"] = ""
    datosCliente["tipoUsuario"] = "cliente"



        /*guardamos esa info en la base de datos*/

    val reference = FirebaseDatabase.getInstance().getReference("Usuarios")
    reference.child(uid!!)
        .setValue(datosCliente)
        .addOnSuccessListener {
            progressDialog.dismiss()
            startActivity(Intent(this@RegistroClienteActivity, MainActivityCliente::class.java))
            finishAffinity()
        }
        .addOnFailureListener { e->
            progressDialog.dismiss()
            Toast.makeText(this, "Falló el registro debido a ${e.message}", Toast.LENGTH_SHORT).show()

        }
    }
}