package com.anadolstudio.adelaide.adapters;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.anadolstudio.adelaide.R;
import com.anadolstudio.adelaide.databinding.ItemMediaGalleryBinding;
import com.anadolstudio.adelaide.helpers.GlideLoader;
import com.anadolstudio.adelaide.interfaces.IDetailable;
import com.anadolstudio.adelaide.interfaces.ILoadMore;

import java.util.ArrayList;
import java.util.List;

public class GalleryListAdapter extends RecyclerView.Adapter<GalleryListAdapter.GalleryViewHolder> {
    public static final String TAG = GalleryListAdapter.class.getName();
    boolean isLoading = false;
    private ArrayList<String> mList;
    private IDetailable<String> iDetailable;
    private ILoadMore iLoadMore;
    private Drawable placeholder;
    private FragmentManager fragmentManager;

    public GalleryListAdapter(AppCompatActivity activity, ArrayList<String> list, IDetailable<String> detailable, @Nullable ILoadMore iLoadMore) {
        placeholder = AppCompatResources.getDrawable(activity, R.drawable.ic_image);
//        fragmentManager = activity.getSupportFragmentManager();
        mList = list;
        iDetailable = detailable;
        this.iLoadMore = iLoadMore;
    }

    @NonNull
    @Override
    public GalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_media_gallery, parent, false);
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
        GalleryDiffUtilCallback NMIDiffUtilCallback =
                new GalleryDiffUtilCallback(mList, list);
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
        private ItemMediaGalleryBinding binding;
        private String path;
        private IDetailable<String> mDetailable;

        public GalleryViewHolder(@NonNull View itemView, IDetailable<String> detailable) {
            super(itemView);
            binding = ItemMediaGalleryBinding.bind(itemView);
            binding.imageView.setOnClickListener(this);
            mDetailable = detailable;
        }

        public void onBind(String path) {
            this.path = path;
            binding.imageView.setTag(getAdapterPosition());
//            binding.imageView.setOnTouchListener();
            GlideLoader.loadImageCenterCrop(binding.imageView, path, placeholder);
        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            if (position == RecyclerView.NO_POSITION) return;

            mDetailable.toDetail(path);
        }
    }
}
