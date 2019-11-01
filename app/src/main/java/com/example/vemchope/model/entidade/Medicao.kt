package com.example.vemchope.model.entidade

import com.example.vemchope.model.enums.STATUS

class Medicao(
    var pesoAtual: Float = 0.0F,
    var pesoMaximo: Float = 0.0F,
    var porcentagem: Int = 0,
    var status: STATUS,
    var mensagem:String? = null
)