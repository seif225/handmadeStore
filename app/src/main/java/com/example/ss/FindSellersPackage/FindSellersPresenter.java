package com.example.ss.FindSellersPackage;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

class FindSellersPresenter {

    private ArrayList<UserModel> listOfUsers;
    private Context context;
    private FindSellersRecyclerAdapter adapter;
    private ArrayList<String> listOfFollowers;
    private UserModel userModel;
    FindSellersPresenter(Context context){
        this.context=context;
        listOfUsers= new ArrayList<>();
        listOfFollowers = new ArrayList<>();
    }


    void previewSellers(final ProgressDialog progressDialog, final RecyclerView recyclerView) {

    progressDialog.setTitle("loading..");
    progressDialog.show();

        FirebaseDatabase.getInstance().getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot d :dataSnapshot.getChildren()) {
                    if(d.child("account type").getValue().toString().equals("business account"))
                    listOfFollowers.add(d.child("userId").getValue().toString());
                }

                getUsers(progressDialog,recyclerView);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });




    }

    private void getUsers(ProgressDialog progressDialog, RecyclerView recyclerView) {

        for (int i = 0; i <listOfFollowers.size() ; i++) {

           // listOfUsers.add();



        }


    }
}
