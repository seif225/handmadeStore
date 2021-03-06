package com.example.ss.ProductActivityPack;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ss.MoreReviewsPackage.MoreReviewsActivity;
import com.example.ss.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sackcentury.shinebuttonlib.ShineButton;
import com.smarteist.autoimageslider.SliderLayout;

import de.hdodenhof.circleimageview.CircleImageView;
import flepsik.github.com.progress_ring.ProgressRingView;

public class ProductActivity extends AppCompatActivity {

    private String prodcutId, userId;
    private ProductActivityPresenter presenter;
    private SliderLayout sliderLayout;
    private TextView price, category, describtion, userName, productIdTv;
    private CircleImageView userPp;
    private ShineButton likeButton;
    private boolean likeState;
    private RatingBar ratingBar;
    private TextView textView, accuRate, rateCounter;
    private ProgressRingView progress;
    private Button removeProductBtn;
    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;
    private Button edit;
    private RecyclerView reviewRecyclerView;
    private ProgressDialog progressDialog;
    private Button seeMoreReviews;
    private FloatingActionButton addToCartFab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product);
        //Log.e("activity shit",productModel.getCategory()+"what so evaaaa ");
        intializeFields();
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                presenter.sendUserToEditProductActivity();

            }
        });

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {

                if (ratingBar.getRating() != 0) {
                    presenter.updateRating(rating);
                    textView.setVisibility(View.VISIBLE);
                }
                if (rating == 0) {
                    textView.setVisibility(View.GONE);
                    presenter.updateRating(0);

                }


            }
        });


        removeProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                alertDialog.show();

            }
        });


        presenter.getProductData(new ProgressDialog(this), sliderLayout, category, price, describtion, userName, productIdTv, userPp, progress, accuRate, rateCounter, removeProductBtn, edit);


        DatabaseReference likRef = FirebaseDatabase.getInstance().getReference().child("Users").child(FirebaseAuth.getInstance().getUid());
        likRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.hasChild("Likes")) {
                    if (dataSnapshot.child("Likes").hasChild(presenter.getProducName())) {


                        likeState = true;
                        likeButton.setChecked(true);

                    } else {

                        likeButton.setChecked(false);
                        likeState = false;

                    }


                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                if (!likeState) {
                    presenter.like();
                    likeButton.setChecked(true);

                } else if (likeState) {
                    presenter.disLike();
                    likeButton.setChecked(false);

                }
            }
        });

        presenter.getAndPreviewReviews(reviewRecyclerView, userId, prodcutId, seeMoreReviews);

        presenter.previewUserRate(ratingBar);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addNewReview();
            }
        });
        seeMoreReviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent j =  new Intent(getApplicationContext(), MoreReviewsActivity.class);
                j.putExtra("uid",userId);
              //  Log.e("uid in adapter",list.get(i).getuId()+"  helafhasjf");
                j.putExtra("productId",prodcutId);
                startActivity(j);
                //Toast.makeText(ProductActivity.this, "working on it ^_^ ", Toast.LENGTH_SHORT).show();
            }
        });

        addToCartFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                presenter.addProductToCart(userId,prodcutId);

            }
        });


    }


    private void intializeFields() {
        Intent i = getIntent();
        edit = findViewById(R.id.edit_product);
        alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setTitle("are you sure ?");
        addToCartFab = findViewById(R.id.floatingActionButton);
        alertDialogBuilder.setMessage("this operation can't be undone.");
        prodcutId = i.getStringExtra("productId");
        userId = i.getStringExtra("uid");

        seeMoreReviews = findViewById(R.id.see_more_reviews);
        Log.e("haahaha", prodcutId + "yid" + userId + "heyyou");
        presenter = new ProductActivityPresenter(this, userId, prodcutId);
        sliderLayout = findViewById(R.id.imageSlider);
        sliderLayout.setIndicatorAnimation(SliderLayout.Animations.WORM);
        sliderLayout.animate();
        sliderLayout.setScrollTimeInSec(2);
        removeProductBtn = findViewById(R.id.remove_product_button);
        progress = findViewById(R.id.progressRing);
        accuRate = findViewById(R.id.accumlated_rate);
        rateCounter = findViewById(R.id.ratings_counter);
        category = findViewById(R.id.product_category_tv);
        price = findViewById(R.id.product_price_tv);
        describtion = findViewById(R.id.product_describtion_tv);
        userName = findViewById(R.id.user_name_in_product_activity);
        productIdTv = findViewById(R.id.product_code_tv);
        userPp = findViewById(R.id.profile_picture_in_product_activity);
        likeButton = findViewById(R.id.like_image_button_in_product_activity);
        ratingBar = findViewById(R.id.rate);
        ratingBar.setMax(5);
        progressDialog = new ProgressDialog(this);
        reviewRecyclerView = findViewById(R.id.reviews_recycler_view);
        /*ratingBar.setStepSize(0.01f);*/
        textView = findViewById(R.id.write_review);

        alertDialogBuilder.setPositiveButton("yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                presenter.removeProduct();

            }
        });
        alertDialogBuilder.setNegativeButton("no", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //finish();
            }
        });

        alertDialog = alertDialogBuilder.create();
        Toolbar toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // add back arrow to toolbar
        if (getSupportActionBar() != null){

            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);

    }


    private void addNewReview() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProductActivity.this, R.style.AlertDialog);
        builder
                .setTitle("add review");
        final EditText reviewEt = new EditText(ProductActivity.this);

        reviewEt.setHint("  write your review here..                                       ");

        builder.setView(reviewEt);
        builder.setPositiveButton("Create ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String review = reviewEt.getText().toString();
                if (TextUtils.isEmpty(review)) {
                    // Toast.makeText(getApplicationContext(),"please enter a group name",Toast.LENGTH_LONG).show();
                    reviewEt.setError("please add review");
                    reviewEt.requestFocus();

                } else {


                    presenter.uploadReviewToFirebase(review, userId, prodcutId, FirebaseAuth.getInstance().getUid(), dialog);

                }

            }
        });

        builder.setNegativeButton("cancel ", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                dialog.cancel();

            }
        });

        builder.show();
    }


}
