package com.pratclot.core.recycler

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.ListAdapter
import com.pratclot.burp.FindSomeQuery
import com.pratclot.core.databinding.LayoutBusinessBinding

class BusinessAdapter : PagingDataAdapter<FindSomeQuery.Business, BusinessViewHolder>(DiffUtilBusiness) {
    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): BusinessViewHolder {
        val layoutInflater = LayoutInflater.from(p0.context)
        return BusinessViewHolder(LayoutBusinessBinding.inflate(layoutInflater, p0, false))
    }

    override fun onBindViewHolder(p0: BusinessViewHolder, p1: Int) {
        getItem(p1)?.let { p0.bind(it) }
    }
}