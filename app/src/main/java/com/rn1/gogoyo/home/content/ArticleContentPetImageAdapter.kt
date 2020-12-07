package com.rn1.gogoyo.home.content

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rn1.gogoyo.component.MapOutlineProvider
import com.rn1.gogoyo.databinding.ItemPetImageLayoutBinding
import com.rn1.gogoyo.model.Pets

class ArticleContentPetImageAdapter(val viewModel: ArticleContentViewModel) : ListAdapter<Pets, RecyclerView.ViewHolder>(PetsDiffCallback) {

    class PetImageViewHolder(
        val viewModel: ArticleContentViewModel,
        val binding: ItemPetImageLayoutBinding
    ): RecyclerView.ViewHolder(binding.root) {

        fun bind(pets: Pets){
            binding.pets = pets

            binding.petImageOuterSel.visibility = View.GONE
            binding.petImageOuter.visibility = View.GONE
            binding.petImageBorder.outlineProvider = MapOutlineProvider()
            binding.petsIv.outlineProvider = MapOutlineProvider()
            binding.executePendingBindings()
        }

        companion object{
            fun from(viewModel: ArticleContentViewModel, parent: ViewGroup): PetImageViewHolder{
                return PetImageViewHolder(viewModel, ItemPetImageLayoutBinding.inflate(
                    LayoutInflater.from(parent.context), parent, false))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PetImageViewHolder.from(viewModel, parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val petImageViewHolder = holder as PetImageViewHolder
        val pets = getItem(position)
        petImageViewHolder.bind(pets)
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