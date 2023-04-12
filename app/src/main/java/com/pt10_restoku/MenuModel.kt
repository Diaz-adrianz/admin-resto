package com.pt10_restoku

data class MenuModel(
    val nama: String,
    val deskripsi: String,
    val poster: String,
    var tersedia: Boolean,
    val harga: Long,
    val tags: List<String>
)