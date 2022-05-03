package com.anadolstudio.adelaide.view.screens.gallery;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.anadolstudio.adelaide.R;
import com.anadolstudio.adelaide.databinding.ItemGalleryBinding;
import com.anadolstudio.adelaide.domain.utils.ImageLoader;
import com.anadolstudio.core.interfaces.IDetailable;
import com.anadolstudio.core.interfaces.ILoadMore;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import static com.anadolstudio.adelaide.domain.utils.ImageLoader.ScaleType.CENTER_CROP;

public class GalleryListAdapter extends RecyclerView.Adapter<GalleryListAdapter.GalleryViewHolder> {
    public static final String TAG = GalleryListAdapter.class.getName();
    private final IDetailable<String> iDetailable;
    private final ILoadMore iLoadMore;
    boolean isLoading = false;
    private ArrayList<String> mList;

    public GalleryListAdapter(ArrayList<String> list, IDetailable<String> detailable, @Nullable ILoadMore iLoadMore) {
        mList = list;
        iDetailable = detailable;
        this.iLoadMore = iLoadMore;
    }

    @NonNull
    @Override
    public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_gallery, parent, false);
        return new GalleryViewHolder(view, iDetailable);
    }

    @Override
    public void onBindViewHolder(@NonNull GalleryViewHolder holder, int position) {
        holder.onBind(mList.get(position));
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public List<String> getData() {
        return mList;
    }

    public void setData(ArrayList<String> list) {
        GalleryDiffUtilCallback NMIDiffUtilCallback = new GalleryDiffUtilCallback(mList, list);
        DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(NMIDiffUtilCallback, false);

        mList = list;
        diffResult.dispatchUpdatesTo(this);
    }

    public void addData(List<String> list) {
        mList.addAll(list);
        notifyItemRangeInserted(mList.size() - list.size(), list.size());
    }

    @Override
    public void onViewAttachedToWindow(@NonNull GalleryViewHolder holder) {
        super.onViewAttachedToWindow(holder);
        int position = holder.getAdapterPosition();
        if (position == getItemCount() - 1 && !isLoading) {
            isLoading = true;
            if (iLoadMore != null) {
                iLoadMore.loadMore();
            }
            isLoading = false;
        }
    }

    protected class GalleryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private final ItemGalleryBinding binding;
        private final IDetailable<String> mDetailable;
        private String path;

        public GalleryViewHolder(@NonNull View itemView, IDetailable<String> detailable) {
            super(itemView);
            binding = ItemGalleryBinding.bind(itemView);
            binding.cardView.setOnClickListener(this);
            mDetailable = detailable;
        }

        public void onBind(String path) {
            this.path = path;
            binding.imageView.setTag(getAdapterPosition());
            ImageLoader.loadImage(binding.imageView, path, CENTER_CROP);// TODO не учитывает невидимые пиксели
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position == RecyclerView.NO_POSITION) {
                return;
            }

            mDetailable.toDetail(path);
        }
    }
}
