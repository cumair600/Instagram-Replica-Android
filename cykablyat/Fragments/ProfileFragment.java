package com.example.cykablyat.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.cykablyat.Adapter.PicAdapter;
import com.example.cykablyat.EditProfileActivity;
import com.example.cykablyat.ListActivity;
import com.example.cykablyat.Model.Post;
import com.example.cykablyat.Model.User;
import com.example.cykablyat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class ProfileFragment extends Fragment {

    ImageView imgProfile;
    TextView txtFollowers, txtFollowing, txtName, txtBio;
    Button btnEditProfile;

    RecyclerView recyclerView;
    PicAdapter picAdapter;
    List<Post> posts;

    FirebaseUser firebaseUser;
    String profileId;

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile,container,false);

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        SharedPreferences preferences = getContext().getSharedPreferences("PREFS",Context.MODE_PRIVATE);
        profileId = preferences.getString("profileId","none");

        imgProfile = view.findViewById(R.id.imgProfile);
        txtFollowers = view.findViewById(R.id.txtFollowers);
        txtFollowing = view.findViewById(R.id.txtFollowing);
        txtName = view.findViewById(R.id.txtName);
        txtBio = view.findViewById(R.id.txtBio);
        btnEditProfile = view.findViewById(R.id.btnEditProfile);

        recyclerView = view.findViewById(R.id.recyclerView);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new GridLayoutManager(getContext(),3);
        recyclerView.setLayoutManager(linearLayoutManager);
        posts = new ArrayList<>();
        picAdapter = new PicAdapter(getContext(), posts);
        recyclerView.setAdapter(picAdapter);

        userInfo();
        getFollowers();
        myPics();

        if(profileId.equals(firebaseUser.getUid()))
            btnEditProfile.setText("Edit Profile");
        else
        {
            checkFollow();
        }

        btnEditProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String btn = btnEditProfile.getText().toString();

                if(btn.equalsIgnoreCase("Edit Profile"))
                {
                    startActivity(new Intent(getContext(), EditProfileActivity.class));
                }
                else if(btn.equalsIgnoreCase("Follow"))
                {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("Following").child(profileId).setValue(true);
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId).child("Followers").child(firebaseUser.getUid()).setValue(true);
                    addFollowNotifications();
                }
                else if(btn.equalsIgnoreCase("Following"))
                {
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("Following").child(profileId).removeValue();
                    FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId).child("Followers").child(firebaseUser.getUid()).removeValue();
                }
            }
        });

        txtFollowing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ListActivity.class);
                intent.putExtra("id",profileId);
                intent.putExtra("title","Following");
                startActivity(intent);
            }
        });

        txtFollowers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), ListActivity.class);
                intent.putExtra("id",profileId);
                intent.putExtra("title","Followers");
                startActivity(intent);
            }
        });

        return view;
    }

    private  void userInfo()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(profileId);
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(getContext() == null)
                {
                    return;
                }

                User user = dataSnapshot.getValue(User.class);
                Glide.with(getContext()).load(user.getImageUrl()).into(imgProfile);
                txtName.setText(user.getName());
                txtBio.setText(user.getBio());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void checkFollow()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("Following");
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.child(profileId).exists())
                    btnEditProfile.setText("Following");
                else
                    btnEditProfile.setText("Follow");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getFollowers()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId).child("Followers");
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                txtFollowers.setText("" + dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("Follow").child(profileId).child("Following");
        ref.keepSynced(true);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                txtFollowing.setText("" + dataSnapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void myPics()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.keepSynced(true);
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                posts.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren())
                {
                    Post post = snapshot.getValue(Post.class);
                    if(post.getPublisher().equals(profileId))
                    {
                        posts.add(post);
                    }
                }
                Collections.reverse(posts);
                picAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addFollowNotifications()
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(profileId);
        reference.keepSynced(true);
        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userId",firebaseUser.getUid());
        hashMap.put("description", "started following you.");
        hashMap.put("postId","");
        hashMap.put("isPost",false);

        reference.push().setValue(hashMap);
    }
}
