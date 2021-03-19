package com.example.hoodootechnologies.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.hoodootechnologies.R;
import com.example.hoodootechnologies.activity.CommentActivity;
import com.example.hoodootechnologies.pojo.Post;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>  {

    private List<Post> postsList;
    private Context context;
    public static String commentId = "";

    public PostAdapter(List<Post> postsList, Context context) {
        this.postsList = postsList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_post, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Post post = postsList.get(position);
        holder.userName.setText(post.getTitle()+" "+post.getFirstName()+" "+post.getLastName());
        holder.postLocation.setText(post.getText());
        holder.noOfLikes.setText(post.getLikes()+" likes");
        Glide.with(context).load(post.getUserPicture()).into(holder.userImage);
        Glide.with(context).load(post.getPostImage()).into(holder.postImage);
//        String tags = "";
//        String tagsArray[] = new String[post.getTags().length];
//        for(int i=0;i<post.getTags().length;i++){
//            tags += "#"+tagsArray[i]+" ";
//        }
//        holder.tagged.setText(tags);
    }

    @Override
    public int getItemCount() {
        return postsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView userImage, postImage, commentImage;
        public TextView userName, postLocation, noOfLikes, tagged;
        public ToggleButton likeButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            userName = itemView.findViewById(R.id.post_user_name);
            postLocation = itemView.findViewById(R.id.post_location);
            noOfLikes = itemView.findViewById(R.id.no_of_likes);
            tagged = itemView.findViewById(R.id.hastags);
            userImage = itemView.findViewById(R.id.post_user_image);
            postImage = itemView.findViewById(R.id.post_image);
            commentImage = itemView.findViewById(R.id.post_comment);
            likeButton = itemView.findViewById(R.id.like_toggle);

            commentImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    commentId = postsList.get(getAdapterPosition()).getPostId();
                    context.startActivity(new Intent(context, CommentActivity.class));
                }
            });

            likeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Post post = postsList.get(getAdapterPosition());
                    if(likeButton.isChecked()){
                        noOfLikes.setText((Integer.parseInt(post.getLikes())+1)+" likes");
                    }else {
                        noOfLikes.setText(post.getLikes()+" likes");
                    }
                }
            });

        }
    }

}
