package com.example.glamora.fragmentOrderDetails.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.glamora.R
import com.example.glamora.data.model.ordersModel.LineItemDTO
import com.example.glamora.data.model.ordersModel.OrderDTO
import com.example.glamora.databinding.OrderListItemBinding

class OrderLineItemsAdapter(private var lineItems: List<LineItemDTO>,
                            private val onItemClick: (String) -> Unit ) :
    RecyclerView.Adapter<OrderLineItemsAdapter.LineItemViewHolder>() {

    class LineItemViewHolder(private val binding: OrderListItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(lineItem: LineItemDTO,  onItemClick: (String) -> Unit) {
            binding.orderItem = lineItem
            binding.executePendingBindings()

//            binding.root.setOnClickListener {
//                onItemClick(lineItem.id) // Trigger navigation by passing the product ID
//            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LineItemViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: OrderListItemBinding = DataBindingUtil.inflate(
            inflater, R.layout.order_list_item, parent, false
        )
        return LineItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LineItemViewHolder, position: Int) {
        val lineItem = lineItems[position]
        holder.bind(lineItem, onItemClick)
    }

    override fun getItemCount(): Int {
        return lineItems.size
    }

    fun updateData(newLineItems: List<LineItemDTO>) {
        lineItems = newLineItems
        notifyDataSetChanged()
    }
}
