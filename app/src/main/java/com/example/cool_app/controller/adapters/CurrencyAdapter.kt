package com.example.cool_app.controller.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.cool_app.R
import com.example.cool_app.model.entities.Currency

class CurrencyAdapter(private val currencies: List<Currency>): RecyclerView.Adapter<CurrencyAdapter.CurrencyViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CurrencyAdapter.CurrencyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_currency, parent, false)
        return CurrencyAdapter.CurrencyViewHolder(itemView)
    }

    class CurrencyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        var tvName: TextView? = null
        var tvValue: TextView? = null
        var tvCode: TextView? = null


        init {
            tvName = itemView.findViewById(R.id.tvName)
            tvValue = itemView.findViewById(R.id.tvValue)
            tvCode = itemView.findViewById(R.id.tvCode)
        }

    }

    override fun getItemCount(): Int = currencies.size


    override fun onBindViewHolder(holder: CurrencyAdapter.CurrencyViewHolder, position: Int) {
        holder.tvName?.text = currencies[position].name
        holder.tvValue?.text = "${currencies[position].value.toString()}"
        holder.tvCode?.text = "[${currencies[position].charCode}]"
    }
}