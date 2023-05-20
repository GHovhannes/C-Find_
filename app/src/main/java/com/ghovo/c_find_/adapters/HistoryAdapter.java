package com.ghovo.c_find_.adapters;



import static com.ghovo.c_find_.utilities.Constants.IMAGE_HEIGHT;
import static com.ghovo.c_find_.utilities.Constants.IMAGE_WIDTH;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ghovo.c_find_.databinding.ItemContainerUserBinding;
import com.ghovo.c_find_.databinding.ItemContainerUserLikedBinding;
import com.ghovo.c_find_.models.User;

import java.util.List;


public class HistoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private final List<User> userList;
    private static final String isLiked = "true";
    public static final int VIEW_TYPE_LIKED = 1;
    public static final int VIEW_TYPE_USER = 2;

    public HistoryAdapter(List<User> userList) {
        this.userList = userList;
    }

    class HistoryViewHolder extends RecyclerView.ViewHolder {

        ItemContainerUserBinding itemContainerUserBinding;

        HistoryViewHolder(ItemContainerUserBinding itemContainerUserBinding) {

            super(itemContainerUserBinding.getRoot());
            this.itemContainerUserBinding = itemContainerUserBinding;

        }

        void setUserData(User user) {

            itemContainerUserBinding.textUserName.setText(user.userName);
            itemContainerUserBinding.textEmail.setText(user.email);

            itemContainerUserBinding.imageProfile.setImageBitmap(
                    getResizedBitmap(getUserImage(user.image)
                    ));

        }
    }

    class HistoryViewHolderLiked extends RecyclerView.ViewHolder {

        ItemContainerUserLikedBinding itemContainerUserLikedBinding;

        HistoryViewHolderLiked(ItemContainerUserLikedBinding itemContainerUserLikedBinding) {

            super(itemContainerUserLikedBinding.getRoot());
            this.itemContainerUserLikedBinding = itemContainerUserLikedBinding;

        }

        void setUserData(User user) {

            itemContainerUserLikedBinding.textUserName.setText(user.userName);
            itemContainerUserLikedBinding.textEmail.setText(user.email);

            itemContainerUserLikedBinding.imageProfile.setImageBitmap(
                    getResizedBitmap(getUserImage(user.image)
                    ));

        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        if (viewType == VIEW_TYPE_LIKED) {

            return new HistoryViewHolderLiked(
                    ItemContainerUserLikedBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );

        } else {

            return new HistoryViewHolder(
                    ItemContainerUserBinding.inflate(
                            LayoutInflater.from(parent.getContext()),
                            parent,
                            false
                    )
            );

        }

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        if (getItemViewType(position) == VIEW_TYPE_LIKED) {

            ((HistoryViewHolderLiked) holder).setUserData(userList.get(position));

        } else {

            ((HistoryViewHolder) holder).setUserData(userList.get(position));

        }

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    @Override
    public int getItemViewType(int position) {

        if (userList.get(position).isLiked.equals(isLiked)) {

            return VIEW_TYPE_LIKED;

        } else {

            return VIEW_TYPE_USER;

        }

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
