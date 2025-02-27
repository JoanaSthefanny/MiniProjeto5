package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.adapter.AtividadeAdapter
import com.example.myapplication.models.Atividade

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private val listaAtividades = mutableListOf<Atividade>()
    private val adapter = AtividadeAdapter(listaAtividades)
    private lateinit var dbHelper: DatabaseHelper

    // Registra o ActivityResultLauncher para a CadastroActivity
    private val cadastroActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Recupera a atividade retornada pela CadastroActivity
                val atividade = result.data?.getParcelableExtra<Atividade>("ATIVIDADE")
                atividade?.let {
                    // Adiciona a atividade ao banco de dados e obtém o ID gerado
                    val id = dbHelper.adicionarAtividade(it)
                    it.id = id.toInt()
                    listaAtividades.add(it)
                    adapter.notifyDataSetChanged()
                }
            }
        }

    // Registra o ActivityResultLauncher para a DetalhesActivity
    private val detalhesActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Recupera o ID da atividade excluída
                val idExcluido = result.data?.getIntExtra("idExcluido", -1)
                if (idExcluido != null && idExcluido != -1) {
                    // Remove a atividade da lista com base no ID
                    listaAtividades.removeAll { it.id == idExcluido }
                    adapter.notifyDataSetChanged()
                } else {
                    // Recarrega a lista completa do banco (em caso de edição)
                    atualizarListaDeAtividades()
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Inicializa o DatabaseHelper para interagir com o banco de dados
        dbHelper = DatabaseHelper(this)
        // Configura o RecyclerView
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Carrega as atividades do banco de dados e atualiza a lista
        listaAtividades.addAll(dbHelper.buscarAtividades())
        adapter.notifyDataSetChanged()

        // Botão para cadastrar nova atividade
        val btnCadastrar = findViewById<Button>(R.id.btnCadastrar)
        btnCadastrar.setOnClickListener {
            // Cria um Intent para abrir a CadastroActivity
            val intent = Intent(this, CadastroActivity::class.java)
            cadastroActivityLauncher.launch(intent)
        }

        // Configura o clique em um item da lista
        adapter.setOnItemClickListener { atividade ->
            // Cria um Intent para abrir a DetalhesActivity
            val intent = Intent(this, DetalhesActivity::class.java)
            intent.putExtra("id", atividade.id)
            intent.putExtra("titulo", atividade.nome)
            intent.putExtra("responsavel", atividade.responsavel)
            intent.putExtra("descricao", atividade.descricao)
            intent.putExtra("data", atividade.data)
            // Inicia a DetalhesActivity e espera um resultado
            detalhesActivityLauncher.launch(intent)
        }
    }

    // Método para atualizar a lista de atividades com os dados do banco
    private fun atualizarListaDeAtividades() {
        listaAtividades.clear() // Limpa a lista atual
        listaAtividades.addAll(dbHelper.buscarAtividades()) // Recarrega as atividades do banco
        adapter.notifyDataSetChanged() // Notifica o adapter para atualizar a RecyclerView
    }
}