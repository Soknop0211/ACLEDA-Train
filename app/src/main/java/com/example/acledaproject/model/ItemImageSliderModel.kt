package com.example.acledaproject.model

import java.io.Serializable

data class ItemImageSliderModel(
    val id : String,
    val name : String,
    val description : String,
    val logo : Int
) : Serializable