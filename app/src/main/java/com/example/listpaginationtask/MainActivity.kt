package com.example.listpaginationtask

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.AbsListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.listpaginationtask.databinding.ActivityMainBinding
import com.example.listpaginationtask.model.ListItem
import com.example.listpaginationtask.utils.gone
import com.example.listpaginationtask.utils.visible
import com.example.listpaginationtask.view.adapters.ListItemsAdapter


class MainActivity : AppCompatActivity() {
    private lateinit var listAdapter: ListItemsAdapter
    private lateinit var binding: ActivityMainBinding
    val itemsList = ArrayList<ListItem>()
    val manuallySelectedItems = ArrayList<ListItem>()
    //pagination params
    private var totalPage = 0
    private var page = 1
    var userType = ""
    private var value: Boolean = false
    private var isLoading = false
    companion object{
        var isAllSelected = false
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        initClickListeners()
        initData()
        setAdapter()
    }

    private fun initClickListeners() {
        binding.selectAllCB.setOnCheckedChangeListener { compound, checked ->
            isAllSelected = checked
            if (checked) {
                itemsList.map { it.isSelected = true }
                listAdapter.loadList(itemsList)
                calculatePrice()
            } else {
                Log.d("", "initClickListeners: $manuallySelectedItems  ")
                itemsList.map {
                    it.isSelected = (manuallySelectedItems.contains(it))
                    listAdapter.loadList(itemsList)
                    calculatePrice()
                }
            }
        }
    }

    private fun initData() {
            for (i in 1..15)
                itemsList.add(ListItem("Item $i", i * 5, false))
    }

    private fun setAdapter() {
        //set adapter
        listAdapter = ListItemsAdapter(itemsList)
        binding.itemsRV.adapter = listAdapter
        //handle click listener
        listAdapter?.clickListener = { pos, checked ->
            if (!isAllSelected) {
                itemsList[pos].isSelected = checked
                if (checked)
                    manuallySelectedItems.add(itemsList[pos])
                else
                    manuallySelectedItems.remove(itemsList[pos])
                calculatePrice()
            }
        }
        //handle scroll listener
        binding.itemsRV.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val lastVisibleItem = layoutManager.findLastCompletelyVisibleItemPosition()
                val totalItem =layoutManager.itemCount

                //check if scrolled to last visible item
                if(lastVisibleItem == totalItem-1 && !isLoading){
                    //add new data to list
                    if(itemsList.size< 30) {  //check if already added
                        loadMoreData()
                    }
                }
            }
        })
    }
    private fun loadMoreData() {
        binding.progress.visible()
        isLoading = true
        //load based on item count
        Handler(Looper.getMainLooper()).postDelayed({
                isLoading = false
                for (i in 16..30)
                    itemsList.add(ListItem("Item $i", i * 5, false))
            listAdapter.loadList(itemsList)
            binding.progress.gone()
        }, 1500)

        //load based on page size
         /*   isLoading = true
            page++

            // Simulate API call delay
            Handler(Looper.getMainLooper()).postDelayed({
                // api
                isLoading = false
                if (page < totalPage) {
                    page += 1
                    getPosts(page)
                }
            }, 1500)*/
    }
    private fun calculatePrice() {
        var sum = 0
        val selectedNames = ArrayList<String>()
        for (i in itemsList)
            if (i.isSelected) {
                sum += i.price
                selectedNames.add(i.name)
            }
        binding.totalPriceTV.text = sum.toString()
        if (selectedNames.isEmpty)
            binding.itemsNameTV.text = getString(R.string.nothing_selected)
        else if (selectedNames.size == itemsList.size)
            binding.itemsNameTV.text = getString(R.string.all_selected)
        else
            binding.itemsNameTV.text = selectedNames.joinToString()
    }
}