package com.example.peoplelist.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.peoplelist.base.BaseResult
import com.example.peoplelist.entity.FetchError
import com.example.peoplelist.entity.Person
import com.example.peoplelist.ui.dialog.InvisibleProgressDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by Berk Ç. on 9/13/21.
 */
class MainViewModel(private val repository: MainRepository): ViewModel() {

    private val _peopleListLiveData = MutableLiveData<List<Person>>()
    val peopleListLiveData: LiveData<List<Person>> get() = _peopleListLiveData
    private val _eventOnError = MutableLiveData<FetchError>()
    val eventOnError: LiveData<FetchError> get() = _eventOnError

    var progressDialog: InvisibleProgressDialog? = null
    var isProgressShown = false
    var nextValue: String? = null
    var peoplePagedList = mutableListOf<Person>()
    var totalPeopleCount : Int? = null


    fun fetchPeople(next: String?){
        viewModelScope.launch {
            val response = withContext(Dispatchers.IO){
                repository.fetchPeople(next)
            }
            when(response){
                is BaseResult.Success -> {
                    response.body.people.distinctBy { it.id } //prevents id duplicates in currently loaded group.
                    nextValue = response.body.next // sets the required value for loading the next page.
                    if (totalPeopleCount == null) totalPeopleCount = response.body.totalSize
                    _peopleListLiveData.value = response.body.people

                }
                is BaseResult.Error -> {
                    _eventOnError.value = response.error
                }
            }
        }
    }

}