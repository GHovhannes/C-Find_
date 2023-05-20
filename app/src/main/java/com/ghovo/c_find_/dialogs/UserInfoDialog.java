package com.ghovo.c_find_.dialogs;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.ghovo.c_find_.R;
import com.ghovo.c_find_.listeners.DialogListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.Objects;

public class UserInfoDialog extends DialogFragment{

    Bitmap userImage;
    DialogListener dialogListener;

    public UserInfoDialog(Bitmap userImage, DialogListener dialogListener) {

        this.userImage = userImage;
        this.dialogListener = dialogListener;

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.dialog_user_info, null);
        RoundedImageView roundedImageView = view.findViewById(R.id.imageProfile);

        Objects.requireNonNull(getDialog()).getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.argb(0, 0, 0, 0)));

        getDialog().getWindow().setDimAmount(5);

        view.startAnimation(AnimationUtils.loadAnimation(
                getContext(), R.anim.fade_in
        ));

        roundedImageView.setImageBitmap(getResizedBitmap(userImage));

        return view;

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView image = view.findViewById(R.id.imageProfile);
        ImageView imageView_bad = view.findViewById(R.id.badImage);
        ImageView imageView_good = view.findViewById(R.id.goodImage);

        imageView_bad.setOnClickListener(v -> {

            dialogListener.onButtonClicked(false);
            dismiss();

        });

        imageView_good.setOnClickListener(v -> {

            dialogListener.onButtonClicked(true);
            dismiss();

        });

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);

        return dialog;

    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);

        dialog.dismiss();
    }

    @Override
    public void onCancel(@NonNull DialogInterface dialog) {
        super.onCancel(dialog);

        dialogListener.onButtonClicked(false);

        dialog.cancel();
    }

    @Nullable
    @Override
    public Animation onCreateAnimation(int transit, boolean enter, int nextAnim) {
        return super.onCreateAnimation(transit, enter, nextAnim);
    }

    private Bitmap getResizedBitmap(Bitmap bitmap) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scaleWidth = ((float) 1440) / width;
        float scaleHeight = ((float) 2560) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(
                bitmap, 0, 0, width, height, matrix, false);
        bitmap.recycle();

        return resizedBitmap;

    }

}
