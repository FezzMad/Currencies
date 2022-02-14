package com.example.cool_app.controller.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cool_app.model.entities.IncCurrency
import com.example.cool_app.R


// адаптер списка (ленты) новостей
class IncCurrencyAdapter(private val incCurrencies: List<IncCurrency>):
    RecyclerView.Adapter<IncCurrencyAdapter.IncExRateViewHolder>() {

    // иницилизация шаблона для item
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): IncExRateViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_inc_currency, parent, false)
        return IncExRateViewHolder(itemView)
    }

    // иницилизация view
    class IncExRateViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var tvName: TextView? = null
        var tvPercent: TextView? = null
        var tvCode: TextView? = null


        init {
            tvName = itemView.findViewById(R.id.tvName)
            tvPercent = itemView.findViewById(R.id.tvValue)
            tvCode = itemView.findViewById(R.id.tvCode)
        }

    }

    // заполнение view данными и обработка нажатий

    override fun onBindViewHolder(holder: IncExRateViewHolder, position: Int) {
        holder.tvName?.text = incCurrencies[position].name
        holder.tvPercent?.text = "+${incCurrencies[position].percentageInc.toString()}"
        holder.tvCode?.text = "[${incCurrencies[position].charCode}]"

    }

    override fun getItemCount(): Int = incCurrencies.size
}