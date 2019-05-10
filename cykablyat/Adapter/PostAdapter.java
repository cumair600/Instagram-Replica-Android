package com.example.cykablyat.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.ContactsContract;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.cykablyat.CommentActivity;
import com.example.cykablyat.Fragments.PostDetailFragment;
import com.example.cykablyat.Fragments.ProfileFragment;
import com.example.cykablyat.ListActivity;
import com.example.cykablyat.Model.Post;
import com.example.cykablyat.Model.User;
import com.example.cykablyat.PostActivity;
import com.example.cykablyat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static android.app.Activity.RESULT_OK;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{

    public Context context;
    public List<Post> posts;

    private TextToSpeech TTS;

    private FirebaseUser firebaseUser;

    public PostAdapter(Context c, List<Post> pList)
    {
        context = c;
        posts = pList;
    }



    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(context).inflate(R.layout.post_item,viewGroup,false);
        return new PostAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final Post post = posts.get(i);

        Glide.with(context).load(post.getPostImage()).into(viewHolder.imgPost);

        if(post.getCaption().equals(""))
        {
            viewHolder.txtCaption.setVisibility(View.GONE);
        }
        else
        {
            viewHolder.txtCaption.setVisibility(View.VISIBLE);
            viewHolder.txtCaption.setText(post.getCaption());
        }

        viewHolder.txtLocation.setText(post.getLocation());

        publisherInfo(viewHolder.imgProfile,viewHolder.txtUsername,viewHolder.txtPublisher,post.getPublisher());
        isLiked(post.getPostId(),viewHolder.imgLike);
        countLikes(viewHolder.txtLikes,post.getPostId());
        getComments(post.getPostId(),viewHolder.txtComments);

        viewHolder.imgProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = context.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("profileId",post.getPublisher());
                editor.apply();

                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
            }
        });

        viewHolder.txtUsername.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = context.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("profileId",post.getPublisher());
                editor.apply();

                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
            }
        });

        viewHolder.txtPublisher.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = context.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("profileId",post.getPublisher());
                editor.apply();

                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
            }
        });

        viewHolder.imgPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = context.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                editor.putString("postId",post.getPostId());
                editor.apply();

                ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new PostDetailFragment()).commit();
            }
        });

        viewHolder.imgLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewHolder.imgLike.getTag().equals("Like")) {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostId()).child(firebaseUser.getUid()).setValue(true);
                    addLikeNotifications(post.getPublisher(),post.getPostId());
                }
                else
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostId()).child(firebaseUser.getUid()).removeValue();
            }
        });

        TTS = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status == TextToSpeech.SUCCESS) {
                    int result = TTS.setLanguage(Locale.UK);
                }
                else
                {
                    Toast.makeText(context,"Intialization Failed!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        viewHolder.imgSpeaker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text = viewHolder.txtCaption.getText().toString();
                TTS.speak(text,TextToSpeech.QUEUE_FLUSH,null);
            }
        });

        viewHolder.imgComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("postId",post.getPostId());
                intent.putExtra("publisherId",post.getPublisher());
                context.startActivity(intent);
            }
        });

        viewHolder.txtComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommentActivity.class);
                intent.putExtra("postId",post.getPostId());
                intent.putExtra("publisherId",post.getPublisher());
                context.startActivity(intent);
            }
        });

        viewHolder.txtLikes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ListActivity.class);
                intent.putExtra("id",post.getPostId());
                intent.putExtra("title","Likes");
                context.startActivity(intent);
            }
        });
    }


    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView imgProfile;
        public ImageView imgPost;
        public ImageView imgLike;
        public ImageView imgComment;
        public ImageView imgSpeaker;

        public TextView txtUsername;
        public TextView txtLikes;
        public TextView txtPublisher;
        public TextView txtCaption;
        public TextView txtComments;
        public TextView txtLocation;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            imgProfile = itemView.findViewById(R.id.imgProfile);
            imgPost = itemView.findViewById(R.id.imgPost);
            imgLike = itemView.findViewById(R.id.imgLike);
            imgComment = itemView.findViewById(R.id.imgComment);
            imgSpeaker = itemView.findViewById(R.id.imgSpeaker);
            txtUsername = itemView.findViewById(R.id.txtUsername);
            txtLikes = itemView.findViewById(R.id.txtLikes);
            txtComments = itemView.findViewById(R.id.txtComments);
            txtCaption = itemView.findViewById(R.id.txtCaption);
            txtPublisher = itemView.findViewById(R.id.txtPublisher);
            txtLocation = itemView.findViewById(R.id.txtLocation);
        }

    }

    private void getComments(String postId, final TextView txtComments)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Comments").child(postId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                txtComments.setText("View All " + dataSnapshot.getChildrenCount() + " Comments");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void isLiked(String postId, final ImageView imgView)
    {
        final FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(postId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(firebaseUser.getUid()).exists())
                {
                    imgView.setImageResource(R.drawable.ic_liked);
                    imgView.setTag("Liked");
                }
                else
                {
                    imgView.setImageResource(R.drawable.ic_like);
                    imgView.setTag("Like");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void countLikes(final TextView txtLikes, String postId)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Likes").child(postId);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                txtLikes.setText(dataSnapshot.getChildrenCount() + " Likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void publisherInfo(final ImageView imgProfile, final TextView txtUsername, final TextView txtPublisher, String userId)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(userId);

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                Glide.with(context).load(user.getImageUrl()).into(imgProfile);
                txtUsername.setText(user.getUsername());
                txtPublisher.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addLikeNotifications(String userId, String postId)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userId);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userId",firebaseUser.getUid());
        hashMap.put("description", "Liked your post.");
        hashMap.put("postId",postId);
        hashMap.put("isPost",true);

        reference.push().setValue(hashMap);
    }

}
