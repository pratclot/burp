package com.pratclot.core

import android.widget.Toast
import androidx.fragment.app.Fragment

open class HandyFragment : Fragment() {
    fun showToast(text: String) = Toast.makeText(requireContext(), text, Toast.LENGTH_SHORT).show()
}