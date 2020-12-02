package com.rn1.gogoyo.home.post

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rn1.gogoyo.component.MapOutlineProvider
import com.rn1.gogoyo.databinding.ItemPetImageLayoutBinding
import com.rn1.gogoyo.model.Pets

class PostPetAdapter: ListAdapter<Pets, RecyclerView.ViewHolder>(PetsImageDiffCallback) {

    class PetImageViewHolder(val binding: ItemPetImageLayoutBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(pets: Pets){
            binding.pets = pets
            binding.petsIv.outlineProvider = MapOutlineProvider()
            binding.executePendingBindings()
        }

        companion object{
            fun from(parent: ViewGroup): PetImageViewHolder{
                return PetImageViewHolder(ItemPetImageLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PetImageViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val petImageViewHolder = holder as PetImageViewHolder
        val pets = getItem(position)
        petImageViewHolder.bind(pets)
    }

    companion object PetsImageDiffCallback: DiffUtil.ItemCallback<Pets>(){
        override fun areItemsTheSame(oldItem: Pets, newItem: Pets): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Pets, newItem: Pets): Boolean {
            return oldItem == newItem
        }

    }
}