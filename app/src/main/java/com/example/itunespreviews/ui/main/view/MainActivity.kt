package com.example.itunespreviews.ui.main.view

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.View
import androidx.appcompat.widget.SearchView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.itunespreviews.R
import com.example.itunespreviews.common.BaseActivity
import com.example.itunespreviews.common.BaseViewModel
import com.example.itunespreviews.ui.main.viewmodel.MainViewModel
import com.example.itunespreviews.ui.player.view.TrackPlayerActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : BaseActivity() {

    private lateinit var searchView: SearchView
    override var progressbarId: Int? = R.id.tracksProgressBar

    lateinit var mainViewModel : MainViewModel
    lateinit var tracksAdapter: TracksAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupViewModel()
        registerObserver(mainViewModel)
        initRecyclerView()


    }

    private fun setupViewModel() {
        mainViewModel = ViewModelProvider(this).get(MainViewModel::class.java)
    }

    private fun initRecyclerView() {
        Glide.with(this).load(R.drawable.search_placeholder).into(errorPageImageView)
        tracksRecyclerView.layoutManager = LinearLayoutManager(this)
        tracksAdapter = TracksAdapter(mainViewModel)
        tracksRecyclerView.adapter  = tracksAdapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main,menu)
        val searchItem = menu.findItem(R.id.action_search)
        searchView = searchItem.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextChange(newText: String): Boolean {

                if(newText.isEmpty())
                    mainViewModel.getRemoteTracks(newText)
                return true
            }

            override fun onQueryTextSubmit(query: String): Boolean {
                mainViewModel.getRemoteTracks(query)
                return true
            }
        })
        return true
    }
    override fun registerObserver(viewModel: BaseViewModel) {
        super.registerObserver(viewModel)

        mainViewModel.tracksList.observe(this, Observer {
            if (it.isNotEmpty())
                tracksAdapter.notifyDataSetChanged()
            else
                mainViewModel.mPlaceHolderObserver.value = true
        })
        mainViewModel.mPlaceHolderObserver.observe(this, Observer {
            if(it){
                errorPageImageView.visibility = View.VISIBLE
            }else{
                errorPageImageView.visibility = View.GONE
            }
        })
        mainViewModel.mNavigateObserver.observe(this, Observer {
            navigate(TrackPlayerActivity::class.java,it,mainViewModel.selectedPosition)
        })
    }


}