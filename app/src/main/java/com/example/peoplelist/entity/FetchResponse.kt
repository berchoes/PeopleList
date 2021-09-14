package com.example.peoplelist.entity

/**
 * Created by Berk Ç. on 9/13/21.
 */
data class FetchResponse(var people: MutableList<Person>, val next: String?)
