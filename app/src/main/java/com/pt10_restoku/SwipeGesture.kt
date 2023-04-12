package com.pt10_restoku


import android.content.Context
import android.graphics.Canvas
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

import it.xabaras.android.recyclerview.swipedecorator.RecyclerViewSwipeDecorator;

abstract class SwipeGesture(context : Context) : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP or
        ItemTouchHelper.DOWN or ItemTouchHelper.START or ItemTouchHelper.END,
        ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    val deleteColor = ContextCompat.getColor(context,R.color.red)
    val editColor = ContextCompat.getColor(context,R.color.blue)
    val iconTint = ContextCompat.getColor(context, R.color.white)
    val deleteIcon = R.drawable.ic_baseline_delete_24
    val EditIcon = R.drawable.ic_baseline_edit_24

    override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {

        RecyclerViewSwipeDecorator.Builder(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                .addSwipeLeftBackgroundColor(deleteColor)
                .addSwipeLeftActionIcon(deleteIcon)
                .setSwipeLeftActionIconTint(iconTint)
                .addSwipeRightBackgroundColor(editColor)
                .addSwipeRightActionIcon(EditIcon)
                .setSwipeRightActionIconTint(iconTint)
                .create()
                .decorate()

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }


}