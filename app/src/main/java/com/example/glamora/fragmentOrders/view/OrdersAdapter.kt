package com.example.glamora.fragmentOrders.view

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.glamora.R
import com.example.glamora.data.model.ordersModel.LineItemDTO
import com.example.glamora.data.model.ordersModel.OrderDTO
import com.example.glamora.databinding.OrderListItemBinding

class OrdersAdapter(private var orders: List<OrderDTO>,
                    private val onItemClick: (String) -> Unit) : RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

    class OrderViewHolder(private val binding: OrderListItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(lineItem: LineItemDTO) {
            binding.orderItem = lineItem
            binding.executePendingBindings()

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: OrderListItemBinding = DataBindingUtil.inflate(inflater, R.layout.order_list_item, parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        for (lineItem in order.lineItems) {
            holder.bind(lineItem)
        }
        holder.itemView.setOnClickListener {
            onItemClick(order.id)
        }
    }

    override fun getItemCount(): Int {
        return orders.size
    }

    fun updateData(newOrders: List<OrderDTO>) {
        orders = newOrders
        notifyDataSetChanged()
    }

}
