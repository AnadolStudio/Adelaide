package com.anadolstudio.adelaide.fragments;

import static com.anadolstudio.adelaide.domain.utils.BitmapHelper.decodeSampledBitmapFromContentResolverPath;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDialogFragment;

import com.anadolstudio.adelaide.R;
import com.anadolstudio.adelaide.databinding.DialogImageBinding;

public class ImageDialog extends AppCompatDialogFragment {
    public static final String TAG = ImageDialog.class.getName();
    private static final String PHOTO_FILE = "photo_file";
    private DialogImageBinding binding;
    private String path;

    public static ImageDialog newInstance(String path) {
        Bundle args = new Bundle();
        args.putString(PHOTO_FILE, path);
        ImageDialog fragment = new ImageDialog();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        binding = DialogImageBinding.inflate(getLayoutInflater());
        path = getArguments().getString(PHOTO_FILE);
        Point size = new Point();
        getActivity().getWindowManager().getDefaultDisplay().getSize(size);
        int min = Math.min(size.x, size.y);
        Bitmap bitmap = decodeSampledBitmapFromContentResolverPath(getActivity(), path, min, min);
        binding.imagePhoto.getLayoutParams().height = bitmap.getHeight();
        binding.imagePhoto.requestLayout();

        binding.imagePhoto.setImageBitmap(bitmap);

        AlertDialog dialog = new AlertDialog.Builder(getActivity())
                .setView(binding.getRoot())
                .create();
        if (dialog.getWindow() != null)
            dialog.getWindow().getAttributes().windowAnimations = R.style.SlidingDialogAnimation;

//        dialog.getWindow().setBackgroundDrawable(new BitmapDrawable(getResources(), fast));
        return dialog;
    }
}
