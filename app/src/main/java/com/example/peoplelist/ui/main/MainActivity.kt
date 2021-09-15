package com.example.peoplelist.ui.main

import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.peoplelist.R
import com.example.peoplelist.databinding.ActivityMainBinding
import com.example.peoplelist.entity.Person
import com.example.peoplelist.ui.dialog.InvisibleProgressDialog
import com.example.peoplelist.util.extensions.gone
import com.example.peoplelist.util.extensions.visible
import com.google.android.material.bottomsheet.BottomSheetDialog
import org.koin.android.ext.android.inject


class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by inject()
    private lateinit var binding: ActivityMainBinding
    private var isProgressShown = false
    private var progressDialog: InvisibleProgressDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDataBinding()
        initViews()
        observeEvents()
        initListeners()
        fetchPeople()
    }

    private fun initDataBinding() {
        binding = DataBindingUtil.setContentView(this@MainActivity, R.layout.activity_main)
        binding.lifecycleOwner = this@MainActivity
    }

    private fun setPeopleList(list: List<Person>) {
        val oldCount = viewModel.peoplePagedList.size

        list.forEach {
            if (viewModel.peoplePagedList.none { p -> it.id == p.id }) viewModel.peoplePagedList.add(it)  //preventing id duplicates.
        }
        binding.rvPeople.adapter?.notifyItemRangeInserted(oldCount, viewModel.peoplePagedList.size)

        tryHardDetector(oldCount)

        //after the initial load of data, call fetchPeople() again if the recyclerView height < screen height.
        binding.rvPeople.measure(
            View.MeasureSpec.makeMeasureSpec(
                binding.rvPeople.width,
                View.MeasureSpec.EXACTLY
            ), View.MeasureSpec.UNSPECIFIED
        )
        val recyclerHeight = binding.rvPeople.measuredHeight
        val screenHeight = Resources.getSystem().displayMetrics.heightPixels
        if (recyclerHeight < screenHeight) {
            fetchPeople()
        }
    }

    //when there is no possible unique id to fetch.
    private fun tryHardDetector(oldSize: Int){
        if (oldSize == viewModel.peoplePagedList.size) viewModel.persistenceCounter++ else viewModel.persistenceCounter = 0
        if (viewModel.persistenceCounter > 2) Toast.makeText(
            this,
            "Chill... We ran out of people.",
            Toast.LENGTH_SHORT
        ).show()
    }

    private fun initViews() {
        progressDialog = InvisibleProgressDialog(this)
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
            showProgress(false)
        })
        viewModel.eventOnError.observe(this, {
            binding.srMain.isRefreshing = false
            showProgress(false)
            showErrorBottomSheet(errorDescription = it.errorDescription)
        })
    }

    private fun emptyListViewSetter(isEmptyList: Boolean) {
        if (isEmptyList) {
            binding.rvPeople.gone()
            binding.tvNobody.visible()
            binding.tvTryAgain.visible()
            binding.ivRefresh.visible()
        } else {
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
                if (!recyclerView.canScrollVertically(RecyclerView.FOCUS_DOWN) && newState == RecyclerView.SCROLL_STATE_IDLE) {
                    fetchPeople()
                }
            }
        })

        binding.tvTryAgain.setOnClickListener {
            fetchPeople()
            binding.tvTryAgain.gone()
            binding.ivRefresh.gone()
            binding.tvNobody.gone()
        }

        binding.ivRefresh.setOnClickListener {
            fetchPeople()
            binding.tvTryAgain.gone()
            binding.tvNobody.gone()
            binding.ivRefresh.gone()
        }
    }

    private fun fetchPeople() {
        binding.srMain.isRefreshing = true
        showProgress(true)
        viewModel.fetchPeople(viewModel.nextValue)
    }

    private fun showErrorBottomSheet(errorDescription: String) {
        val errorBottomSheet = BottomSheetDialog(this, R.style.BottomSheetDialogTheme)
        val bottomSheetView = LayoutInflater.from(applicationContext).inflate(
            R.layout.bottom_sheet,
            findViewById<ConstraintLayout>(R.id.bottomSheet)
        )
        bottomSheetView.findViewById<TextView>(R.id.tvErrorDescription).text = errorDescription
        bottomSheetView.findViewById<Button>(R.id.btRetry).setOnClickListener {
            fetchPeople()
            errorBottomSheet.dismiss()
        }
        errorBottomSheet.apply {
            setCancelable(false)
            setContentView(bottomSheetView)
            show()
        }
    }

    private fun showProgress(show: Boolean) {
        if (show) {
            if (!isProgressShown) {  //prevents crash caused by dialog duplicates.
                progressDialog?.show(supportFragmentManager, "ProgressDialog")
                isProgressShown = true
            }
        } else {
            if (isProgressShown) {
                progressDialog?.dismiss()
                isProgressShown = false
            }
        }
    }
}