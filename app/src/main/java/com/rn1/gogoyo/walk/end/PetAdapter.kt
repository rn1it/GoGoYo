package com.rn1.gogoyo.walk.end

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rn1.gogoyo.component.MapOutlineProvider
import com.rn1.gogoyo.databinding.ItemPetImageLayoutBinding
import com.rn1.gogoyo.home.post.PostPetAdapter
import com.rn1.gogoyo.model.Pets

class PetAdapter: ListAdapter<Pets, RecyclerView.ViewHolder>(PostPetAdapter) {

    class PetViewHolder(val binding: ItemPetImageLayoutBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(pets: Pets){
            binding.pets = pets
            binding.petsIv.outlineProvider = MapOutlineProvider()
            binding.executePendingBindings()
        }

        companion object{
            fun from(parent: ViewGroup): PetViewHolder{
                return PetViewHolder(ItemPetImageLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PetViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val petViewHolder = holder as PetViewHolder
        val pets = getItem(position)
        petViewHolder.bind(pets)
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