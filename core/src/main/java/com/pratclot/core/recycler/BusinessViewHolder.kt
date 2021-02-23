package com.pratclot.core.recycler

import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.pratclot.burp.FindSomeQuery
import com.pratclot.core.R
import com.pratclot.core.databinding.LayoutBusinessBinding
import com.pratclot.core.toKm

class BusinessViewHolder(val binding: LayoutBusinessBinding) :
    RecyclerView.ViewHolder(binding.root) {
    fun bind(item: FindSomeQuery.Business) {
        binding.apply {
            item.let {
                textViewLayoutBusinessName.text = it.name
                textViewLayoutBusinessDistance.text = it.distance?.toKm()
                Glide.with(this.root).load(it.photos?.firstOrNull() ?: "")
                    .error(R.drawable.ic_baseline_error_outline_24)
                    .into(imageViewLayoutBusinessPhoto)
                ratingBarLayoutBusiness.rating = it.rating?.toFloat() ?: 0f
            }
        }
    }
}
