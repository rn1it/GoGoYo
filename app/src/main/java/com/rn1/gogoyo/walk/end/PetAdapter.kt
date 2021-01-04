package com.rn1.gogoyo.walk.end

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
import com.rn1.gogoyo.R
import com.rn1.gogoyo.component.MapOutlineProvider
import com.rn1.gogoyo.databinding.ItemPetImageLayoutBinding
import com.rn1.gogoyo.home.post.PostPetAdapter
import com.rn1.gogoyo.model.Pets

class PetAdapter: ListAdapter<Pets, RecyclerView.ViewHolder>(PostPetAdapter) {

    class PetViewHolder(
        val binding: ItemPetImageLayoutBinding
        ): RecyclerView.ViewHolder(binding.root){

        fun bind(pets: Pets){
            binding.pets = pets

            val imgUri = pets.image?.toUri()?.buildUpon()?.scheme("https")?.build()
            Glide.with(binding.petsIv.context)
                .asBitmap()
                .load(imgUri)
                .addListener(object : RequestListener<Bitmap>{
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Bitmap>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Bitmap?,
                        model: Any?,
                        target: Target<Bitmap>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        binding.petsIv.setImageBitmap(resource)
                        return false
                    }

                })
                .apply(
                    RequestOptions()
                        .placeholder(R.drawable.dog_profile)
                        .error(R.drawable.my_pet)
                )
                .into(binding.petsIv)

            binding.petImageOuterSel.visibility = View.GONE
            binding.petImageOuter.visibility = View.GONE
            binding.petImageBorder.outlineProvider = MapOutlineProvider()
            binding.petsIv.outlineProvider = MapOutlineProvider()
            binding.executePendingBindings()
        }

        companion object{
            fun from(parent: ViewGroup): PetViewHolder{
                return PetViewHolder(
                    ItemPetImageLayoutBinding.inflate(
                        LayoutInflater.from(parent.context),
                        parent,
                        false
                    )
                )
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