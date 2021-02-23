package com.pratclot.core.recycler

import androidx.recyclerview.widget.DiffUtil
import com.pratclot.burp.FindSomeQuery

object DiffUtilBusiness : DiffUtil.ItemCallback<FindSomeQuery.Business>() {
    override fun areItemsTheSame(p0: FindSomeQuery.Business, p1: FindSomeQuery.Business): Boolean {
        return p0.name == p1.name
    }

    override fun areContentsTheSame(
        p0: FindSomeQuery.Business,
        p1: FindSomeQuery.Business
    ): Boolean {
        return areItemsTheSame(p0, p1)
    }
}
