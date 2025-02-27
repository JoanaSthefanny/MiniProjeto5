package com.example.myapplication

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.myapplication.models.Atividade
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CadastroActivity : AppCompatActivity() {

    private lateinit var btnSelecionarData: Button
    private lateinit var edtNome: EditText
    private lateinit var edtResponsavel: EditText
    private lateinit var edtDescricao: EditText
    private val calendar = Calendar.getInstance() // Para armazenar a data selecionada

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cadastro)

        edtNome = findViewById(R.id.edtNome)
        edtResponsavel = findViewById(R.id.edtResponsavel)
        edtDescricao = findViewById(R.id.edtDescricao)
        btnSelecionarData = findViewById(R.id.btnSelecionarData)
        val btnSalvar = findViewById<Button>(R.id.btnSalvar)
        val btnVoltar = findViewById<ImageButton>(R.id.btnVoltar)
        btnVoltar.setOnClickListener {
            mostrarDialogoConfirmacaoSaida()
        }

        // Recupera a atividade enviada de DetalhesActivity
        val atividadeRecebida = intent.getParcelableExtra<Atividade>("ATIVIDADE_EDITAR")

        // Preenche os campos com os dados da atividade
        if (atividadeRecebida != null) {
            edtNome.setText(atividadeRecebida.nome)
            edtResponsavel.setText(atividadeRecebida.responsavel)
            edtDescricao.setText(atividadeRecebida.descricao)
            btnSelecionarData.text = atividadeRecebida.data // Preenche a data no botão
        }

        // Configura o DatePickerDialog para selecionar a data
        btnSelecionarData.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                this,
                { _, year, month, dayOfMonth ->
                    // Atualiza o calendário com a data selecionada
                    calendar.set(Calendar.YEAR, year)
                    calendar.set(Calendar.MONTH, month)
                    calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)
                    // Formata a data para exibir no botão
                    val formatoData = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                    val dataFormatada = formatoData.format(calendar.time)
                    btnSelecionarData.text = dataFormatada
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
            datePickerDialog.show()
        }

        // Botão Salvar
        btnSalvar.setOnClickListener {
            // Captura os valores dos campos
            val nome = edtNome.text.toString()
            val responsavel = edtResponsavel.text.toString()
            val descricao = edtDescricao.text.toString()
            val data = btnSelecionarData.text.toString() // Captura a data do botão

            // Validação dos campos
            var camposValidos = true

            if (nome.isEmpty()) {
                edtNome.error = "Campo obrigatório"
                camposValidos = false
            }

            if (responsavel.isEmpty()) {
                edtResponsavel.error = "Campo obrigatório"
                camposValidos = false
            }

            if (descricao.isEmpty()) {
                edtDescricao.error = "Campo obrigatório"
                camposValidos = false
            }

            if (data == "Selecionar Data" || data.isEmpty()) {
                btnSelecionarData.error = "Campo obrigatório"
                camposValidos = false
            }

            // Se todos os campos estiverem preenchidos, salva a atividade
            if (camposValidos) {
                // Cria a atividade com os dados do formulário
                val atividade = atividadeRecebida?.copy(
                    nome = nome,
                    responsavel = responsavel,
                    data = data,
                    descricao = descricao
                ) ?: Atividade(nome = nome, responsavel = responsavel, data = data, descricao = descricao)
                // Cria um Intent para retornar a atividade
                val intent = Intent()
                intent.putExtra("ATIVIDADE", atividade)
                setResult(Activity.RESULT_OK, intent)
                finish()
            }
        }
    }

    // Exibe um diálogo de confirmação ao sair sem salvar
    private fun mostrarDialogoConfirmacaoSaida() {
        val dialog = androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Atenção")
            .setMessage("Tem certeza que deseja voltar? Seu progresso não será salvo.")
            .setPositiveButton("Sim") { _, _ ->
                // Fecha a atividade e volta para a tela anterior
                finish()
            }
            .setNegativeButton("Cancelar", null) // Não faz nada, apenas fecha o diálogo
            .create()

        dialog.show()
    }
}
