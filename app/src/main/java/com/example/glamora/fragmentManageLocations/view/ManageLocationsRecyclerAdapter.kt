package com.example.glamora.fragmentManageLocations.view

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.glamora.R
import com.example.glamora.data.model.AddressModel
import com.example.glamora.databinding.ItemManageLocationsBinding
import com.example.glamora.fragmentCart.view.CartItemInterface

class DiffUtilCallback : DiffUtil.ItemCallback<AddressModel>() {
    override fun areItemsTheSame(oldItem: AddressModel, newItem: AddressModel) = oldItem.firstName == newItem.firstName
    override fun areContentsTheSame(oldItem: AddressModel, newItem: AddressModel) = oldItem == newItem
}

class ManageLocationsRecyclerAdapter (
    private val setDefaultAddress : (address : AddressModel)->Unit,
    private val deleteItem : (address : AddressModel)->Unit,
) : ListAdapter<AddressModel, ViewHolder>(DiffUtilCallback()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_manage_locations, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)
        holder.binding.apply {
            addressItemUserName.text = "Name : ${item.firstName} ${item.lastName}"
            addressItemUserPhone.text = "Phone : ${item.phone}"
            addressItemUserAddress.text = "Address : ${item.country}, ${item.city}, ${item.street}"

            addressItemDeleteButton.setOnClickListener {
                deleteItem(item)
            }
            addressItemCheckBox.isChecked = item.isDefault
            if(item.isDefault) addressItemDeleteButton.visibility = View.GONE


            addressItemCheckBox.isClickable = !item.isDefault

            addressItemCheckBox.setOnClickListener {
                addressItemCheckBox.isChecked = item.isDefault
                if(!item.isDefault)
                    setDefaultAddress(item)
            }

        }
    }
}

class ViewHolder(view : View) : RecyclerView.ViewHolder(view)
{
    val binding = ItemManageLocationsBinding.bind(view)
}