package com.ghovo.c_find_.adapters;

import static com.ghovo.c_find_.utilities.Constants.IMAGE_HEIGHT;
import static com.ghovo.c_find_.utilities.Constants.IMAGE_WIDTH;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ghovo.c_find_.databinding.NotificationsItemBinding;
import com.ghovo.c_find_.listeners.UserListener;
import com.ghovo.c_find_.models.User;

import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.NotificationsViewHolder>{
    private final List<User> userList;
    private final UserListener userListener;

    public NotificationsAdapter(List<User> userList, UserListener userListener) {
        this.userList = userList;
        this.userListener = userListener;
    }

    class NotificationsViewHolder extends RecyclerView.ViewHolder {

        NotificationsItemBinding itemBinding;

        NotificationsViewHolder(NotificationsItemBinding itemContainerUserBinding) {

            super(itemContainerUserBinding.getRoot());
            this.itemBinding = itemContainerUserBinding;

        }

        void setUserData(User user) {

            itemBinding.textUserName.setText(user.userName);
            itemBinding.textEmail.setText(user.number);

            itemBinding.meetingPlace.setImageURI(Uri.parse(user.notification));

            itemBinding.imageProfile.setImageBitmap(
                    getResizedBitmap(getUserImage(user.image)
                    ));

            itemBinding.getRoot().setOnClickListener(v -> userListener.onUserClicked(user));

        }
    }

    @NonNull
    @Override
    public NotificationsAdapter.NotificationsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        NotificationsItemBinding itemBinding = NotificationsItemBinding.inflate(

                LayoutInflater.from(parent.getContext()),
                parent,
                false

        );

        return new NotificationsAdapter.NotificationsViewHolder(itemBinding);

    }

    @Override
    public void onBindViewHolder(@NonNull NotificationsAdapter.NotificationsViewHolder holder, int position) {

        holder.setUserData(userList.get(position));

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    private Bitmap getUserImage(String encodedImage) {

        byte[] bytes = Base64.decode(encodedImage, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

    }

    private Bitmap getResizedBitmap(Bitmap bitmap) {

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scaleWidth = ((float) IMAGE_HEIGHT) / width;
        float scaleHeight = ((float) IMAGE_WIDTH) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);

        Bitmap resizedBitmap = Bitmap.createBitmap(
                bitmap, 0, 0, width, height, matrix, false);
        bitmap.recycle();

        return resizedBitmap;

    }
}
