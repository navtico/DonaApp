package com.matias.tiendavirtualdonapp_kotlin.Modelos

class ModeloCategoria {

    var id : String = ""
    var categoria : String = ""


    constructor()
    constructor(id: String, categoria: String) {
        this.id = id
        this.categoria = categoria
    }


}