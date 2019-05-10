package com.example.cykablyat.Adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.cykablyat.Fragments.ProfileFragment;
import com.example.cykablyat.MainActivity;
import com.example.cykablyat.Model.User;
import com.example.cykablyat.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context context;
    private List<User> users;
    private boolean isFragment;

    private FirebaseUser firebaseUser;

    public UserAdapter(Context c, List<User> uList, boolean isFrag)
    {
        context = c;
        users = uList;
        isFragment = isFrag;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i)
    {
        View v = LayoutInflater.from(context).inflate(R.layout.user_item,viewGroup,false);
        return new UserAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        final User user = users.get(i);

        viewHolder.btnFollow.setVisibility(View.VISIBLE);

        viewHolder.txtUsername.setText(user.getUsername());
        viewHolder.txtName.setText(user.getName());
        Glide.with(context).load(user.getImageUrl()).into(viewHolder.imageProfile);
        isFollowing(user.getId(), viewHolder.btnFollow);

        if(user.getId().equals(firebaseUser.getUid()))
        {
            viewHolder.btnFollow.setVisibility(View.GONE);
        }

        viewHolder.itemView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                if(isFragment)
                {
                    SharedPreferences.Editor editor = context.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit();
                    editor.putString("profileId",user.getId());
                    editor.apply();

                    ((FragmentActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
                }
                else
                {
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.putExtra("publisherId",user.getId());
                    context.startActivity(intent);
                }
            }
        });

        viewHolder.btnFollow.setOnClickListener(new View.OnClickListener()
                                                {
                                                    @Override
                                                    public void onClick(View v)
                                                    {
                                                        if(viewHolder.btnFollow.getText().toString().equalsIgnoreCase("Follow"))
                                                        {
                                                            FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("Following").child(user.getId()).setValue(true);
                                                            FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId()).child("Followers").child(firebaseUser.getUid()).setValue(true);
                                                            addFollowNotifications(user.getId());
                                                        }
                                                        else
                                                        {
                                                            FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("Following").child(user.getId()).removeValue();
                                                            FirebaseDatabase.getInstance().getReference().child("Follow").child(user.getId()).child("Followers").child(firebaseUser.getUid()).removeValue();
                                                        }
                                                    }
                                                }
        );
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public TextView txtUsername;
        public TextView txtName;
        public ImageView imageProfile;
        public Button btnFollow;

        public ViewHolder(@NonNull View itemView)
        {
            super(itemView);

            txtUsername = itemView.findViewById(R.id.txtUsername);
            txtName = itemView.findViewById(R.id.txtName);
            imageProfile = itemView.findViewById(R.id.imgProfile);
            btnFollow = itemView.findViewById(R.id.btnFollow);
        }
    }

    private void isFollowing(final String uid, final Button btn)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("Following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.child(uid).exists())
                {
                    btn.setText("Following");
                }
                else
                    btn.setText("Follow");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void addFollowNotifications(String userId)
    {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Notifications").child(userId);

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("userId",firebaseUser.getUid());
        hashMap.put("description", "started following you.");
        hashMap.put("postId","");
        hashMap.put("isPost",false);

        reference.push().setValue(hashMap);
    }

}
