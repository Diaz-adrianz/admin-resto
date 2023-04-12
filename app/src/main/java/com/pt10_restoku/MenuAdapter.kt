package com.pt10_restoku

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class MenuAdapter(private val ctx: Context, private var mList: ArrayList<MenuModel>) : RecyclerView.Adapter<MenuAdapter.ViewHolder>() {

    private lateinit var mListener : onItemClickListener

    interface onItemClickListener{
        fun onItemClick(position : Int)
    }

    fun setOnItemClickListener(listener: onItemClickListener){
        mListener = listener
    }

    @SuppressLint("NotifyDataSetChanged")
    fun filterList(filterlist: ArrayList<MenuModel>) {
        mList = filterlist
        notifyDataSetChanged()
    }
    @SuppressLint("NotifyDataSetChanged")
    fun deleteItem(i : Int){
        mList.removeAt(i)
        notifyDataSetChanged()
    }
    @SuppressLint("NotifyDataSetChanged")
    fun addItem(i : Int, news : MenuModel){
        mList.add(i,news)
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.menu_item, parent, false)

        return ViewHolder(view, mListener)
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val item = mList[position]

        holder.vNama.text = item.nama
        holder.vHarga.text = if (item.tersedia) "Rp${item.harga}" else "HABIS"

        if (!item.tersedia) {
            val colorMatrix = ColorMatrix()
            colorMatrix.setSaturation(0f)
            val filter = ColorMatrixColorFilter(colorMatrix)
            holder.vPoster.colorFilter = filter
        } else {
            holder.vPoster.clearColorFilter()
        }

        Glide
            .with(ctx)
            .load(item.poster)
            .placeholder(R.drawable.loading)
            .error(R.drawable.loading)
            .centerCrop()
            .into(holder.vPoster);
    }

    override fun getItemCount(): Int {
        return mList.size
    }

    class ViewHolder(ItemView: View, listener: onItemClickListener) : RecyclerView.ViewHolder(ItemView) {
        val vPoster: ImageView = itemView.findViewById(R.id.poster)
        val vNama: TextView = itemView.findViewById(R.id.nama)
        val vHarga: TextView = itemView.findViewById(R.id.harga)

        init {
            itemView.setOnClickListener {
                listener.onItemClick(adapterPosition)
            }
        }
    }
}