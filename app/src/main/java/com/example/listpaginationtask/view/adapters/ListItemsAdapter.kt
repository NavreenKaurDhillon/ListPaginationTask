package com.example.listpaginationtask.view.adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.listpaginationtask.MainActivity
import com.example.listpaginationtask.databinding.ItemLayoutBinding
import com.example.listpaginationtask.model.ListItem

class ListItemsAdapter(private var items : ArrayList<ListItem>) : RecyclerView.Adapter<ListItemsAdapter.ViewHolder>(){

    var clickListener : ((pos: Int, checked: Boolean)-> Unit)?=null

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ListItemsAdapter.ViewHolder {
       return ViewHolder(ItemLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    fun loadList(newList : ArrayList<ListItem>)
    {
        items = newList
        notifyDataSetChanged()
    }

    inner class ViewHolder (val binding : ItemLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onBindViewHolder(holder: ListItemsAdapter.ViewHolder, position: Int) {
        with(holder){
            binding.apply {

                if (items[position].isSelected)
                    checkbox.isChecked = true
                else
                    checkbox.isChecked = false

                itemsNameTV.text = items[position].name
                priceTV.text = items[position].price.toString()

                checkbox.setOnCheckedChangeListener{compound, checked ->
                      clickListener?.invoke(position, checked)
                }
            }
        }
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return items.size
    }

}