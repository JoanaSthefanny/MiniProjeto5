package com.example.myapplication.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myapplication.R
import com.example.myapplication.models.Atividade

// A Classe AtividadeAdapter é um adaptador para o RecyclerView, em que exibe a lista de atividades
class AtividadeAdapter(private val listaAtividades: MutableList<Atividade>) : RecyclerView.Adapter<AtividadeAdapter.AtividadeViewHolder>() {

    // Variável para armazenar um listener de clique em um item da lista
    private var onItemClickListener: ((Atividade) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AtividadeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_atividade, parent, false)
        return AtividadeViewHolder(view)
    }

    // O método onBindViewHolder vai vincular os dados de uma atividade a um ViewHolder
    override fun onBindViewHolder(holder: AtividadeViewHolder, position: Int) {
        val atividade = listaAtividades[position]
        holder.bind(atividade)
    }

    override fun getItemCount(): Int = listaAtividades.size

    // Serve pra atualizar a lista de atividades no App
    fun atualizarLista(novaLista: List<Atividade>) {
        listaAtividades.clear()
        listaAtividades.addAll(novaLista)
        notifyDataSetChanged()
    }
    // Define um listener de clique em um item da lista
    fun setOnItemClickListener(listener: (Atividade) -> Unit) {
        onItemClickListener = listener
    }

    // A classe interna AtividadeViewHolder representa cada item da lista no RecyclerView
    inner class AtividadeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nomeAtividade: TextView = itemView.findViewById(R.id.txtNomeAtividade)
        private val dataAtividade: TextView = itemView.findViewById(R.id.txtDataAtividade)

        init {
            itemView.setOnClickListener {
                onItemClickListener?.invoke(listaAtividades[adapterPosition])
            }
        }
        // Vincula os dados de uma atividade aos componentes da interface
        fun bind(atividade: Atividade) {
            nomeAtividade.text = atividade.nome
            dataAtividade.text = atividade.data
        }
    }
}