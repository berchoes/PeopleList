package com.example.peoplelist.util.extensions


import android.view.View


/**
 * Created by Berk Ç. on 9/13/21.
 */

fun View.visible(){
    this.visibility = View.VISIBLE
}

fun View.gone(){
    this.visibility = View.GONE
}