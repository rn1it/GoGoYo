package com.rn1.gogoyo.walk.start

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.net.toUri
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.gms.maps.model.Marker
import com.rn1.gogoyo.R
import com.rn1.gogoyo.component.MapOutlineProvider
import com.rn1.gogoyo.databinding.ItemPetImageLayoutBinding
import com.rn1.gogoyo.model.Pets


class WalkStartPetAdapter(val viewModel: WalkStartViewModel) : ListAdapter<Pets, RecyclerView.ViewHolder>(WalkPetsImageDiffCallback) {

    class PetViewHolder(val viewModel: WalkStartViewModel, val binding: ItemPetImageLayoutBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(pets: Pets){
            binding.pets = pets

            binding.petImageOuterSel.visibility = View.GONE
            binding.petImageOuter.visibility = View.GONE
            binding.petImageBorder.outlineProvider = MapOutlineProvider()
            binding.petsIv.outlineProvider = MapOutlineProvider()
            binding.executePendingBindings()
        }

        companion object{
            fun from(viewModel: WalkStartViewModel, parent: ViewGroup): PetViewHolder{
                return PetViewHolder(viewModel, ItemPetImageLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return PetViewHolder.from(viewModel, parent)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val petViewHolder = holder as PetViewHolder
        val pets = getItem(position)
        petViewHolder.bind(pets)
    }

    companion object WalkPetsImageDiffCallback: DiffUtil.ItemCallback<Pets>(){
        override fun areItemsTheSame(oldItem: Pets, newItem: Pets): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: Pets, newItem: Pets): Boolean {
            return oldItem == newItem
        }

    }

}