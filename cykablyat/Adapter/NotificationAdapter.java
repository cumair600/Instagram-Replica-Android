package com.example.cykablyat.Adapter;

import android.app.Notification;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.Image;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.cykablyat.Fragments.PostDetailFragment;
import com.example.cykablyat.Fragments.ProfileFragment;
import com.example.cykablyat.Model.Notifications;
import com.example.cykablyat.Model.Post;
import com.example.cykablyat.Model.User;
import com.example.cykablyat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{

    private Context context;
    private List<Notifications> notifications;

    public NotificationAdapter(Context context, List<Notifications> notifications) {
        this.context = context;
        this.notifications = notifications;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.notification_item, viewGroup, false);
        return new NotificationAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {

        final Notifications notify = notifications.get(i);

        viewHolder.txtDescription.setText(notify.getDescription());
        getUserInfo(viewHolder.imgProfile, viewHolder.txtUsername,notify.getUserId());

        if(notify.isIsPost())
        {
            viewHolder.imgPost.setVisibility(View.VISIBLE);
            getPostImage(viewHolder.imgPost, notify.getPostId());
        }
        else
            viewHolder.imgPost.setVisibility(View.GONE);

        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(notify.isIsPost())
                {
                    SharedPreferences.Editor editor = context.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                    editor.putString("postId",notify.getPostId());
                    editor.apply();

                    ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PostDetailFragment()).commit();
                }
                else
                {
                    SharedPreferences.Editor editor = context.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                    editor.putString("profileId",notify.getUserId());
                    editor.apply();

                    ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return notifications.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView imgProfile, imgPost;
        public TextView txtUsername, txtDescription;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            imgProfile = itemView.findViewById(R.id.imgProfile);
            imgPost = itemView.findViewById(R.id.imgPost);
            txtUsername = itemView.findViewById(R.id.txtUsername);
            txtDescription = itemView.findViewById(R.id.txtDescription);
        }

    }

    private void getUserInfo(final ImageView imgProfile, final TextView txtUsername, String publihserId)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(publihserId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(context).load(user.getImageUrl()).into(imgProfile);
                txtUsername.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getPostImage(final ImageView imgPost, final String postId)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts").child(postId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Post post = dataSnapshot.getValue(Post.class);
                Glide.with(context).load(post.getPostImage()).into(imgPost);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
