package com.matias.tiendavirtualdonapp_kotlin

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.matias.tiendavirtualdonapp_kotlin.Cliente.LoginClienteActivity
import com.matias.tiendavirtualdonapp_kotlin.Vendedor.LoginVendedorActivity
import com.matias.tiendavirtualdonapp_kotlin.databinding.ActivitySeleccionarTipoBinding


class SeleccionarTipoActivity : AppCompatActivity() {


    private lateinit var binding : ActivitySeleccionarTipoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySeleccionarTipoBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.TipoVendedor.setOnClickListener {
            startActivity(Intent(this@SeleccionarTipoActivity, LoginVendedorActivity::class.java))
        }


        binding.TipoCliente.setOnClickListener {
            startActivity(Intent(this@SeleccionarTipoActivity, LoginClienteActivity::class.java))
        }
    }
}