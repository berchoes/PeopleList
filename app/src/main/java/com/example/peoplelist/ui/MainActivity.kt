package com.example.peoplelist.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.peoplelist.R
import com.example.peoplelist.databinding.ActivityMainBinding
import com.example.peoplelist.entity.Person
import org.koin.android.ext.android.inject


class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by inject()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDataBinding()
        initViews()
        observeEvents()
        viewModel.fetchPeople(null)
    }

    private fun initDataBinding() {
        binding = DataBindingUtil.setContentView(this@MainActivity, R.layout.activity_main)
        binding.lifecycleOwner = this@MainActivity
    }

    private fun setPeopleList(list: List<Person>) {
        binding.rvPeople.apply {
            adapter = PeopleListAdapter(list)
        }
    }

    private fun initViews() {
        binding.rvPeople.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            val decoration =
                DividerItemDecoration(applicationContext, DividerItemDecoration.VERTICAL)
            addItemDecoration(decoration)
        }
        setSwipeRefreshLayout()
    }

    private fun setSwipeRefreshLayout() {
        binding.srMain.setOnRefreshListener {
            viewModel.fetchPeople(null)
        }
    }

    private fun observeEvents() {
        viewModel.peopleListLiveData.observe(this, {
            setPeopleList(it.people)
            binding.srMain.isRefreshing = false
        })

        viewModel.eventOnError.observe(this, {
            binding.srMain.isRefreshing = false
        })

    }
}