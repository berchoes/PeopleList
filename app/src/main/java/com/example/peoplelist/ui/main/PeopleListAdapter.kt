package com.example.peoplelist.ui.main

/**
 * Created by Berk Ç. on 9/13/21.
 */
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.peoplelist.databinding.ItemPeopleListBinding
import com.example.peoplelist.entity.Person


class PeopleListAdapter(private val items: List<Person>) :
    RecyclerView.Adapter<PeopleListAdapter.ViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val itemBinding = ItemPeopleListBinding.inflate(layoutInflater, parent, false)
        return ViewHolder(itemBinding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount() = items.size

    inner class ViewHolder(private val binding: ItemPeopleListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Person) {
            binding.tvPerson.text = "${item.fullName} (${item.id})"
        }
    }
}