package com.example.peoplelist.ui.main

import android.content.res.Resources
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.peoplelist.R
import com.example.peoplelist.databinding.ActivityMainBinding
import com.example.peoplelist.entity.Person
import com.example.peoplelist.util.extensions.gone
import com.example.peoplelist.util.extensions.visible
import org.koin.android.ext.android.inject


class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by inject()
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDataBinding()
        initViews()
        observeEvents()
        initListeners()
        viewModel.fetchPeople(viewModel.nextValue)
    }

    private fun initDataBinding() {
        binding = DataBindingUtil.setContentView(this@MainActivity, R.layout.activity_main)
        binding.lifecycleOwner = this@MainActivity
    }

    private fun setPeopleList(list: List<Person>) {
        val oldCount = viewModel.peoplePagedList.size
        list.forEach {
            if (viewModel.peoplePagedList.none { p -> p.id == it.id }) viewModel.peoplePagedList.add(
                it
            )
        }
        binding.rvPeople.adapter?.notifyItemRangeInserted(oldCount, viewModel.peoplePagedList.size)
        if(viewModel.peoplePagedList.size < 14){
            binding.srMain.isRefreshing = true
            viewModel.fetchPeople(viewModel.nextValue)
        }
    }

    private fun initViews() {
        binding.rvPeople.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            val decoration =
                DividerItemDecoration(applicationContext, DividerItemDecoration.VERTICAL)
            addItemDecoration(decoration)
            adapter = PeopleListAdapter(viewModel.peoplePagedList)
        }
        setSwipeRefreshLayout()
    }

    private fun setSwipeRefreshLayout() {
        binding.srMain.setOnRefreshListener {
            viewModel.nextValue = null
            viewModel.peoplePagedList.clear()
            binding.rvPeople.adapter?.notifyDataSetChanged()
            viewModel.fetchPeople(viewModel.nextValue)
        }
    }

    private fun observeEvents() {
        viewModel.peopleListLiveData.observe(this, {
            if (it.people.isNotEmpty()) {
                emptyListViewSetter(false)
                setPeopleList(it.people)
            } else {
                if (viewModel.peoplePagedList.isEmpty()) {
                   emptyListViewSetter(true)
                }
            }
            binding.srMain.isRefreshing = false
        })
        viewModel.eventOnError.observe(this, {

            binding.srMain.isRefreshing = false
            Toast.makeText(this, it.errorDescription, Toast.LENGTH_LONG).show()

        })
    }

    private fun emptyListViewSetter(isEmptyList: Boolean){
        if(isEmptyList){
            binding.rvPeople.gone()
            binding.tvNobody.visible()
            binding.tvTryAgain.visible()
            binding.ivRefresh.visible()
        }else{
            binding.rvPeople.visible()
            binding.tvNobody.gone()
            binding.tvTryAgain.gone()
            binding.ivRefresh.gone()
        }
    }
    private fun initListeners() {
        binding.rvPeople.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recyclerView, newState)
                if (!recyclerView.canScrollVertically(1)) {
                    binding.srMain.isRefreshing = true
                    viewModel.fetchPeople(viewModel.nextValue)
                }
            }
        })

        binding.tvTryAgain.setOnClickListener{
            binding.srMain.isRefreshing = true
            viewModel.fetchPeople(viewModel.nextValue)
        }

        binding.ivRefresh.setOnClickListener {
            binding.srMain.isRefreshing = true
            viewModel.fetchPeople(viewModel.nextValue)
        }
    }
}