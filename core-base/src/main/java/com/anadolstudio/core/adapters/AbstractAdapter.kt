package com.anadolstudio.core.adapters

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.anadolstudio.core.adapters.selectablecontroller.SelectableController
import com.anadolstudio.core.interfaces.IDetailable

abstract class AbstractAdapter<Data, Holder : AbstractViewHolder<Data>>(
    protected var dataList: MutableList<Data> = mutableListOf(),
    protected val detailable: IDetailable<Data>
) : RecyclerView.Adapter<Holder>() {

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.onBind(dataList[position])
    }

    open fun getDiffUtilCallback(dataList: MutableList<Data>, list: MutableList<Data>): DiffUtil.Callback? =
        BaseDiffUtilCallback(dataList, list)

    open fun setData(list: MutableList<Data>) {
        getDiffUtilCallback(dataList, list)
            ?.let { callback ->
                val diffResult = DiffUtil.calculateDiff(callback, false)
                dataList = list
                diffResult.dispatchUpdatesTo(this)
            }
            ?: let { dataList = list }
    }

    open fun addData(list: List<Data>) {
        dataList.addAll(list)
        notifyItemRangeInserted(dataList.size - list.size, list.size)
    }

    override fun getItemCount(): Int = dataList.size

//    fun toDetail(e: Data) = detailable.toDetail(e)

    abstract class Selectable<Data, Holder : AbstractSelectableViewHolder<Data>>(
        dataList: MutableList<Data> = mutableListOf(),
        detailable: IDetailable<Data>,
    ) : AbstractAdapter<Data, Holder>(dataList, detailable) {

        protected open val selectableController: SelectableController<Holder> =
            object : SelectableController.Abstract<Data, Holder>() {

                override fun updateView(holder: Holder, isSelected: Boolean, state: Int) =
                    notifyItemChanged(state)
            }

        override fun onBindViewHolder(holder: Holder, position: Int) {
            holder.onBind(dataList[position], position == selectableController.getCurrentPosition())
        }

        open fun clearSelectedItem() = selectableController.clear()

        override fun getItemCount(): Int = dataList.size
    }
}