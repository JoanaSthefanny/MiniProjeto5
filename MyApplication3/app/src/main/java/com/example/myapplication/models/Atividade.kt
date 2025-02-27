package com.example.myapplication.models

import android.os.Parcel
import android.os.Parcelable

//Representa uma atividade do app
data class Atividade(
    var id: Int = 0,
    var nome: String,
    var responsavel: String,
    var descricao: String,
    var data: String
) : Parcelable {
    // O Construtor secundário é utilizado para criar uma Atividade a partir de um Parcel
    constructor(parcel: Parcel) : this(
        parcel.readInt(),
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: "",
        parcel.readString() ?: ""

        )
    // O Método writeToParcel escreve os dados da Atividade em um Parcel
    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeInt(id)
        parcel.writeString(nome)
        parcel.writeString(responsavel)
        parcel.writeString(descricao)
        parcel.writeString(data)
    }

    override fun describeContents(): Int {
        return 0
    }
    // O Companion object CREATOR é responsável por criar instâncias de Atividade a partir de um Parcel
    companion object CREATOR : Parcelable.Creator<Atividade> {
        // Cria uma instância de Atividade a partir de um Parcel
        override fun createFromParcel(parcel: Parcel): Atividade {
            return Atividade(parcel)
        }

        override fun newArray(size: Int): Array<Atividade?> {
            return arrayOfNulls(size)
        }
    }
}