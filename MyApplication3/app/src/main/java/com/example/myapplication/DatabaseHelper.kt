package com.example.myapplication

import android.content.ContentValues
import android.content.Context
import android.util.Log
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.example.myapplication.models.Atividade


// Responsável por gerenciar o banco de dados SQLite
class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    // Define as constantes que irão ser utilizzadas
    companion object {
        private const val DATABASE_NAME = "atividades.db"
        private const val DATABASE_VERSION = 2

        const val TABLE_ATIVIDADES = "atividades"
        const val COLUMN_ID = "id"
        const val COLUMN_NOME = "nome"
        const val COLUMN_RESPONSAVEL = "responsavel"
        const val COLUMN_DESCRICAO = "descricao"
        const val COLUMN_DATA = "data"
    }

    // O método onCreate vai ser chamado quando o banco de dados é criado pela primeira vez
    override fun onCreate(db: SQLiteDatabase) {
        val createTableQuery = """
            CREATE TABLE $TABLE_ATIVIDADES (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT, 
                $COLUMN_NOME TEXT NOT NULL,
                $COLUMN_RESPONSAVEL TEXT NOT NULL,
                $COLUMN_DESCRICAO TEXT NOT NULL,
                $COLUMN_DATA TEXT NOT NULL
            )
        """.trimIndent()
        db.execSQL(createTableQuery)
    }

    // O Método onUpgrade vai ser chamado quando o banco de dados precisa ser atualizado
    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE $TABLE_ATIVIDADES ADD COLUMN $COLUMN_RESPONSAVEL TEXT NOT NULL DEFAULT ''")
        }
    }

    // Adiciona uma nova atividade ao banco de dados
    fun adicionarAtividade(atividade: Atividade): Long {
        val db = this.writableDatabase
        // Cria um ContentValues para armazenar os dados da atividade
        val values = ContentValues().apply {
            put(COLUMN_NOME, atividade.nome)
            put(COLUMN_RESPONSAVEL, atividade.responsavel)
            put(COLUMN_DESCRICAO, atividade.descricao)
            put(COLUMN_DATA, atividade.data)
        }
        // Insere os valores na tabela e retorna o ID da nova linha
        val id = db.insert(TABLE_ATIVIDADES, null, values)
        db.close()
        return id
    }
    // Retorna uma lista de todas as atividades do banco de dados
    fun buscarAtividades(): List<Atividade> {
        val listaAtividades = mutableListOf<Atividade>()
        val db = this.readableDatabase
        val cursor = db.rawQuery("SELECT * FROM $TABLE_ATIVIDADES", null)

        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID))
            val nome = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_NOME))
            val responsavel = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_RESPONSAVEL))
            val descricao = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DESCRICAO))
            val data = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DATA))
            // Cria um objeto Atividade e adiciona à lista
            listaAtividades.add(Atividade(id, nome, responsavel, descricao, data))
        }
        cursor.close()
        db.close()
        return listaAtividades
    }
    // Remove uma atividade do banco de dados com base no ID
    fun excluirAtividade(id: Int) {
        val db = this.writableDatabase
        db.delete(TABLE_ATIVIDADES, "$COLUMN_ID=?", arrayOf(id.toString()))
        db.close()
    }
    // Atualiza os dados de uma atividade existente
    fun atualizarAtividade(atividade: Atividade) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_NOME, atividade.nome)
            put(COLUMN_RESPONSAVEL, atividade.responsavel)
            put(COLUMN_DESCRICAO, atividade.descricao)
            put(COLUMN_DATA, atividade.data)
        }
        val rowsAffected = db.update(TABLE_ATIVIDADES, values, "$COLUMN_ID=?", arrayOf(atividade.id.toString()))
        Log.d("DatabaseHelper", "Linhas atualizadas: $rowsAffected")
        db.close()
    }
}