package com.rn1.gogoyo.mypets

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rn1.gogoyo.component.MapOutlineProvider
import com.rn1.gogoyo.databinding.ListPagerItemPetBinding
import com.rn1.gogoyo.model.Pets

class MyPetsPagerAdapter: ListAdapter<Pets, RecyclerView.ViewHolder>(PetsDiffCallback) {

    class PetsViewHolder(private val binding: ListPagerItemPetBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(pets: Pets) {
            binding.pets = pets
            binding.myPetsIv.outlineProvider = MapOutlineProvider()
            binding.executePendingBindings()
        }
        companion object{
            fun from(parent: ViewGroup): PetsViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = ListPagerItemPetBinding.inflate(layoutInflater, parent, false)
                return PetsViewHolder(binding)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PetsViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val petsViewHolder = holder as PetsViewHolder
        val pets = getItem(position)
        petsViewHolder.bind(pets)
    }

    companion object PetsDiffCallback: DiffUtil.ItemCallback<Pets>(){
        override fun areItemsTheSame(oldItem: Pets, newItem: Pets): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Pets, newItem: Pets): Boolean {
            return oldItem == newItem
        }

    }
}