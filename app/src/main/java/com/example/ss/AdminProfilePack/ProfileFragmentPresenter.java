package com.example.ss.AdminProfilePack;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ss.HomeFragmentV2Package.ProductModel;
import com.example.ss.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragmentPresenter {

    private Context context;
    private FirebaseDatabase database;
    private DatabaseReference userRef,productsRef;
    private FirebaseAuth mAuth;
    private String userName, bio, image;
    private ProductModel productModel;
    private ArrayList<ProductModel> listOfProducts;
    private ArrayList<String> listOfPictureLinks;
    private NewsFeedRecyclerAdapterForCurrentUser adapter;

    ProfileFragmentPresenter(Context context) {
        this.context = context;
        database = FirebaseDatabase.getInstance();
        mAuth = FirebaseAuth.getInstance();
        userRef = database.getReference().child("Users").child(mAuth.getUid());
        productsRef = database.getReference().child("products").child(mAuth.getUid());
        listOfPictureLinks = new ArrayList<>();
        listOfProducts=new ArrayList<>();
        adapter= new NewsFeedRecyclerAdapterForCurrentUser(context,listOfProducts);

    }


    void retriveUserData(final ProgressDialog progressDialog,final TextView userNameTv,
                         final CircleImageView profilePicture,
                         final TextView bioTv, final TextView numOfFollowers, final TextView numOfFollowing) {
        progressDialog.setMessage("getting your data");
        progressDialog.setCancelable(false);
        progressDialog.show();
        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                userName = dataSnapshot.child("userName").getValue().toString();
                Log.e("username", userName + "");
                if (dataSnapshot.hasChild("image")) {
                    image = dataSnapshot.child("image").getValue().toString();
                    Log.e("image", image + "");

                }
                if(dataSnapshot.hasChild("following")){
                    numOfFollowing.setText(dataSnapshot.child("following").getChildrenCount()+"");
                }
                if(dataSnapshot.hasChild("followers")){
                    numOfFollowers.setText(dataSnapshot.child("followers").getChildrenCount()+"");
                }

                if (dataSnapshot.hasChild("bio")) {

                    bio = dataSnapshot.child("bio").getValue().toString();
                    Log.e("bio", bio + "");

                }


                fetchData(progressDialog,userNameTv, profilePicture, bioTv);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    String getUserName() {
        return userName;
    }

    String getImage() {


        return image;
    }

    String getBio() {
        if (bio == null) {

            return "no bio";
        }
        return bio;
    }

    private void fetchData(ProgressDialog progressDialog, TextView v1, CircleImageView v2, TextView v3) {
        Log.e("username Frag", this.getUserName() + "");
        Log.e("image Frag", this.getImage() + "");
        Log.e("bio Frag ", this.getBio() + "");

        v3.setText(this.getBio());
        Picasso.get().load(this.getImage()).placeholder(R.drawable.user).into(v2);
        v1.setText(this.getUserName());
        progressDialog.dismiss();


    }

    public void showOrHideFab(final FloatingActionButton button) {

      userRef.addValueEventListener(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

              if(dataSnapshot.child("account type").getValue().toString().equals("business account")){

                button.show();

              }
          }

          @Override
          public void onCancelled(@NonNull DatabaseError databaseError) {

          }
      });




    }

    void getAndAddDataToRecycler(final RecyclerView recyclerView, final ProgressDialog progressDialog){

        progressDialog.setMessage("getting data");
        progressDialog.setCancelable(false);
        progressDialog.show();

        productsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {


                adapter.notifyDataSetChanged();


                    for (DataSnapshot dataSnapshot1: dataSnapshot.getChildren()) {
                        productModel = new ProductModel();

                        if(dataSnapshot1.hasChild("images")){
                            //productModel.setImagesLinks(dataSnapshot.child("images").getValue().toString());
                            int i=0;
                            listOfPictureLinks = new ArrayList<>();
                            for (DataSnapshot imagesDataSnapShot:dataSnapshot1.child("images").getChildren()){

                                listOfPictureLinks.add(imagesDataSnapShot.getValue().toString());

                            }
                            productModel.setImagesLinks(listOfPictureLinks);

                        }


                        if(dataSnapshot1.hasChild("category")){
                            productModel.setCategory(dataSnapshot1.child("category").getValue().toString());
                            // Log.e("category",dataSnapshot1.child("category").getValue().toString()+" msh null yasta wla eh?");

                        }
                        if(dataSnapshot1.hasChild("color")){
                            productModel.setColor(dataSnapshot1.child("color").getValue().toString());

                        }
                        if(dataSnapshot1.hasChild("price_range")){
                            productModel.setPriceRange(dataSnapshot1.child("price_range").getValue().toString());

                        }
                        if(dataSnapshot1.hasChild("product_describtion")){
                            productModel.setProductDescribtion(dataSnapshot1.child("product_describtion").getValue().toString());

                        }
                        if(dataSnapshot1.hasChild("product_id")){
                            productModel.setProductId(dataSnapshot1.child("product_id").getValue().toString());

                        }
                        if(dataSnapshot1.hasChild("product_name")){
                            productModel.setProductName(dataSnapshot1.child("product_name").getValue().toString());

                        }
                        if(dataSnapshot1.hasChild("product_price")){
                            productModel.setProdcutPrice(dataSnapshot1.child("product_price").getValue().toString());

                        }
                        if(dataSnapshot1.hasChild("use_id")){
                            productModel.setuId(dataSnapshot1.child("use_id").getValue().toString());
                            Log.e("profile Fragment","id check "+productModel.getuId());


                        }
                        listOfProducts.add(productModel);
                        previewDataOnHome(recyclerView,progressDialog);

                        adapter.notifyDataSetChanged();


                    }
                progressDialog.dismiss();

            }



            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

                progressDialog.dismiss();


            }
        });



    }
    private void previewDataOnHome(RecyclerView recyclerView,ProgressDialog progressDialog) {

        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        // Log.e("previewOnHome",listOfProducts.get(0).getCategory()+"inshallah msh null ");
        recyclerView.setItemViewCacheSize(20);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        recyclerView.setAdapter(adapter);

        progressDialog.dismiss();

    }

     void showProductsnumber(final TextView productsTv) {

    FirebaseDatabase.getInstance().getReference().child("products").child(FirebaseAuth.getInstance().getUid()).addValueEventListener(new ValueEventListener() {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

            productsTv.setText(dataSnapshot.getChildrenCount()+"");





        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {

        }
    });






    }

    public void showOrHideCardView(final CardView cardView) {


    FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid())
            .addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                    if(dataSnapshot.child("account type").getValue().toString().equals("user account")){

                        cardView.setVisibility(View.GONE);

                    }




                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });



    }
}
