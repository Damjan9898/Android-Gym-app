package com.example.fitpass;

import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.LinearLayout;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

public class ImagePickBottomDialog extends BottomSheetDialogFragment {
    PickListener listener;
    View view;

    public ImagePickBottomDialog(PickListener listner) {
        this.listener = listner;
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        Dialog dialog = super.onCreateDialog(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Window window = dialog.getWindow();
            if (window != null) {
                DisplayMetrics metrics = new DisplayMetrics();
                window.getWindowManager().getDefaultDisplay().getMetrics(metrics);
                GradientDrawable dimDrawable = new GradientDrawable();
                GradientDrawable navigationBarDrawable = new GradientDrawable();
                navigationBarDrawable.setShape(GradientDrawable.RECTANGLE);
                navigationBarDrawable.setColor(getResources().getColor(R.color.white));
                Drawable[] layers = {dimDrawable, navigationBarDrawable};
                LayerDrawable windowBackground = new LayerDrawable(layers);
                windowBackground.setLayerInsetTop(1, metrics.heightPixels);
                window.setBackgroundDrawable(windowBackground);
            }
        }
        return dialog;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.layout_upload, container, false);
        initView();
        return view;
    }

    private void initView() {

        LinearLayout btn_camera = view.findViewById(R.id.btn_camera);
        LinearLayout btn_gallery = view.findViewById(R.id.btn_gallery);

        btn_camera.setOnClickListener(view1 -> {
            listener.onPickClick(0);
            dismiss();
        });

        btn_gallery.setOnClickListener(view1 -> {
            listener.onPickClick(1);
            dismiss();
        });

    }

}

