package com.rn1.gogoyo.home.post

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.*
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.rn1.gogoyo.component.MapOutlineProvider
import com.rn1.gogoyo.databinding.ItemPetImageLayoutBinding
import com.rn1.gogoyo.model.Pets

class PostPetAdapter(val viewModel: PostViewModel) : ListAdapter<Pets, RecyclerView.ViewHolder>(PetsImageDiffCallback) {

    class PetImageViewHolder(
        val viewModel: PostViewModel,
        val binding: ItemPetImageLayoutBinding
    ): RecyclerView.ViewHolder(binding.root), LifecycleOwner{

        val isSelected: LiveData<Boolean> = Transformations.map(viewModel.selectedPetPositionList) {
            it.contains(adapterPosition)
        }



        fun bind(pets: Pets){
            binding.pets = pets

            binding.lifecycleOwner = this
            binding.viewHolder = this
            binding.viewModel = viewModel

            binding.petImageOuterSel.outlineProvider = MapOutlineProvider()
            binding.petImageOuter.outlineProvider = MapOutlineProvider()
            binding.petImageBorder.outlineProvider = MapOutlineProvider()
            binding.petsIv.outlineProvider = MapOutlineProvider()
            binding.executePendingBindings()
        }

        private val lifecycleRegistry = LifecycleRegistry(this)

        init {
            lifecycleRegistry.currentState = Lifecycle.State.INITIALIZED
        }

        fun onAttach() {
            lifecycleRegistry.currentState = Lifecycle.State.STARTED
        }

        fun onDetach() {
            lifecycleRegistry.currentState = Lifecycle.State.DESTROYED
        }

        override fun getLifecycle(): Lifecycle {
            return lifecycleRegistry
        }

        companion object{
            fun from(viewModel: PostViewModel, parent: ViewGroup): PetImageViewHolder{
                return PetImageViewHolder(viewModel, ItemPetImageLayoutBinding.inflate(LayoutInflater.from(parent.context), parent, false))
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

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        holder as PetImageViewHolder
        holder.onAttach()
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        holder as PetImageViewHolder
        holder.onDetach()
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