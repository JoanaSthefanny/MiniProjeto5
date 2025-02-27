package com.example.myapplication

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.models.Atividade

class DetalhesActivity : AppCompatActivity() {

    private lateinit var txtTitulo: TextView
    private lateinit var txtDescricao: TextView
    private lateinit var txtResponsavel: TextView
    private lateinit var txtData: TextView
    private lateinit var btnEditar: Button
    private lateinit var btnExcluir: Button
    private var atividade: Atividade? = null
    private lateinit var dbHelper: DatabaseHelper

    // Registra o ActivityResultLauncher para a CadastroActivity
    private val editarActivityLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                // Recupera a atividade editada do resultado
                val atividadeAtualizada = result.data?.getParcelableExtra<Atividade>("ATIVIDADE")
                atividadeAtualizada?.let {
                    dbHelper.atualizarAtividade(it) // Atualiza a atividade no banco de dados
                    // Cria um Intent para indicar que a atividade foi editada
                    val resultIntent = Intent()
                    resultIntent.putExtra("ATIVIDADE_EDITADA", true) // Indica que houve edição
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish()
                }
            }
        }

    // Método chamado quando a atividade é criada
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detalhes)

        dbHelper = DatabaseHelper(this) // Inicializa o DatabaseHelper
        txtTitulo = findViewById(R.id.txtTitulo)
        txtDescricao = findViewById(R.id.txtDescricao)
        txtResponsavel = findViewById(R.id.txtResponsavel)
        txtData = findViewById(R.id.txtData)
        btnEditar = findViewById(R.id.btnEditar)
        btnExcluir = findViewById(R.id.btnExcluir)

        // Configura o botão de voltar
        val btnVoltar = findViewById<ImageButton>(R.id.btnVoltar)
        btnVoltar.setOnClickListener {
            finish()
        }

        // Recupera os dados da atividade passados através do Intent
        val idAtividade = intent.getIntExtra("id", -1)
        val titulo = intent.getStringExtra("titulo")
        val responsavel = intent.getStringExtra("responsavel")
        val descricao = intent.getStringExtra("descricao")
        val data = intent.getStringExtra("data")

        // Cria a instância da Atividade
        atividade = Atividade(idAtividade, titulo ?: "", responsavel ?: "", descricao ?: "", data ?: "")

        // Define os textos dos campos da interface
        txtTitulo.text = titulo
        txtDescricao.text = descricao
        txtResponsavel.text = "Responsável: $responsavel"
        txtData.text = "Data: $data"

        // Botão para editar a atividade
        btnEditar.setOnClickListener {
            val intent = Intent(this, CadastroActivity::class.java)
            intent.putExtra("ATIVIDADE_EDITAR", atividade)
            editarActivityLauncher.launch(intent)
        }

        // Botão para excluir a atividade
        btnExcluir.setOnClickListener {
            // Verifica se a atividade não é nula e exibe o diálogo de confirmação de exclusão
            atividade?.let { atividade ->
                mostrarDialogoConfirmacaoExclusao(atividade)
            }
        }
    }

    // Método para exibir um diálogo de confirmação de exclusão
    private fun mostrarDialogoConfirmacaoExclusao(atividade: Atividade) {
        // Cria um AlertDialog para confirmar a exclusão
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Confirmar Exclusão")
            .setMessage("Tem certeza que deseja excluir a atividade '${atividade.nome}'?")
            .setPositiveButton("Excluir") { _, _ ->
                // Ação de exclusão
                dbHelper.excluirAtividade(atividade.id)
                // Cria um Intent para indicar que a atividade foi excluída
                val resultIntent = Intent()
                resultIntent.putExtra("idExcluido", atividade.id)
                setResult(Activity.RESULT_OK, resultIntent)
                finish()
            }
            .setNegativeButton("Cancelar", null)
            .create()

        dialog.show()
    }
}
