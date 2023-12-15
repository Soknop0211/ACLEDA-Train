package com.example.acledaproject.model

import java.io.Serializable

data class HomeItemModel(
    val id : String,
    val name : String,
    val logo : Int
) : Serializable