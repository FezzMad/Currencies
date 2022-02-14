package com.example.cool_app.model.entities

import com.google.gson.annotations.SerializedName

data class FullAnswer(
    @SerializedName("ValCurs") var valCurs: ValCurs? = ValCurs()
)

data class ValCurs(
    @SerializedName("Date") var date: String? = null,
    @SerializedName("Valute") var fullCurrencies: MutableList<FullCurrency> = mutableListOf(),
    @SerializedName("name") var name: String? = null
)

data class FullCurrency(
    @SerializedName("CharCode") var charCode: String? = null,
    @SerializedName("ID") var id: String? = null,
    @SerializedName("Name") var name: String? = null,
    @SerializedName("Nominal") var nominal: String? = null,
    @SerializedName("NumCode") var numCode: String? = null,
    @SerializedName("Value") var value: String? = null,
)

