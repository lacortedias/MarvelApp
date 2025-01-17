package com.example.marvelapp.presentation.detail

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.marvelapp.databinding.ItemParentDetailBinding
import com.example.marvelapp.framework.imageloader.ImageLoader

class DetailParentAdapter(
    private val detailParentList: MutableList<DetailParentVE>,
    private val imageLoader: ImageLoader,
    private val onScrollToEnd: () -> Unit
) : RecyclerView.Adapter<DetailParentAdapter.DetailParentViewHolder>() {

    private var globalBinding: ItemParentDetailBinding? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DetailParentViewHolder {
        return DetailParentViewHolder.create(parent, imageLoader, onScrollToEnd)
    }

    override fun onBindViewHolder(holder: DetailParentViewHolder, position: Int) {
        holder.bind(detailParentList[position])
        globalBinding = holder.getBinding()
    }

    override fun getItemCount() = detailParentList.size

    fun addItems(newItems: List<DetailChildVE>) {
        globalBinding?.let { binding ->
            val recyclerView = binding.recyclerChildDetail
            val layoutManager = recyclerView.layoutManager as? LinearLayoutManager
            val currentScrollPosition = layoutManager?.findFirstVisibleItemPosition() ?: 0
            val currentOffset = layoutManager?.getChildAt(0)?.top ?: 0

            detailParentList.forEach { parentItem ->
                val childItems = newItems.filter { item ->
                    item.titleParentRes == parentItem.categoryStringResId &&
                            parentItem.detailChildList.none { existingItem ->
                                existingItem.id == item.id
                            }
                }

                if (childItems.isNotEmpty()) {
                    parentItem.detailChildList.addAll(childItems)
                }
            }

            notifyDataSetChanged()

            recyclerView.post {
                layoutManager?.scrollToPositionWithOffset(currentScrollPosition, currentOffset)
            }
        }
    }

    class DetailParentViewHolder(
        private val itemBinding: ItemParentDetailBinding,
        private val imageLoader: ImageLoader,
        private val onScrollToEnd: () -> Unit
    ) : RecyclerView.ViewHolder(itemBinding.root) {

        private var childAdapter: DetailChildAdapter? = null
        private val textItemCategory: TextView = itemBinding.textItemCategory
        private val recyclerChildDetail: RecyclerView = itemBinding.recyclerChildDetail

        fun bind(detailParentList: DetailParentVE) {
            val categoryStringResId = detailParentList.categoryStringResId ?: 0
            textItemCategory.text = itemView.context.getString(categoryStringResId)

            if (childAdapter == null) {
                childAdapter = DetailChildAdapter(detailParentList.detailChildList, imageLoader)
                recyclerChildDetail.apply {
                    setHasFixedSize(true)
                    adapter = childAdapter
                    layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)

                    addOnScrollListener(object : RecyclerView.OnScrollListener() {
                        override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {
                            val layoutManager = recyclerView.layoutManager as? LinearLayoutManager
                            val totalItemCount = layoutManager?.itemCount ?: 0
                            val lastVisibleItemPosition = layoutManager?.findLastVisibleItemPosition() ?: 0

                            if (
                                lastVisibleItemPosition + 1 == totalItemCount &&
                                newState == RecyclerView.SCROLL_STATE_IDLE
                                ) {
                                onScrollToEnd.invoke()
                            }
                        }
                    })
                }
            } else {
                childAdapter?.notifyDataSetChanged()
            }
        }

        fun getBinding(): ItemParentDetailBinding {
            return itemBinding
        }

        companion object {
            fun create(
                parent: ViewGroup,
                imageLoader: ImageLoader,
                onScrollToEnd: () -> Unit
            ): DetailParentViewHolder {
                val itemBinding = ItemParentDetailBinding
                    .inflate(LayoutInflater.from(parent.context), parent, false)
                return DetailParentViewHolder(itemBinding, imageLoader, onScrollToEnd)
            }
        }
    }
}