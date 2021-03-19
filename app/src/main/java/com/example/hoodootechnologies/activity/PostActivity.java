package com.example.hoodootechnologies.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hoodootechnologies.R;
import com.example.hoodootechnologies.adapter.PostAdapter;
import com.example.hoodootechnologies.pojo.Post;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PostActivity extends AppCompatActivity {

    private RecyclerView postsRecyclerView;
    private ProgressDialog progressDialog;
    private AlertDialog alertDialog;
    private AlertDialog.Builder alertDialogBuilder;
    private LinearLayout noInternetLayout;
    private ImageView refresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        noInternetLayout = findViewById(R.id.no_i);
        refresh = findViewById(R.id.refresh);
        postsRecyclerView = findViewById(R.id.posts_rec_view);
        progressDialog = ProgressDialog.show(PostActivity.this, "", "Loading...Please wait...");
        alertDialogBuilder = new AlertDialog.Builder(PostActivity.this);
        fetchPostsData();

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(PostActivity.this,PostActivity.class));
                finish();
            }
        });

    }

    private void fetchPostsData(){
        if (isNetworkAvailable(PostActivity.this)) {
            try {
                    String url = "https://dummyapi.io/data/api/post";

                    StringRequest stringReques = new StringRequest(Request.Method.GET, url, new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                List<Post> postsList = new ArrayList<>();
                                JSONObject jsonObject = new JSONObject(response);
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    Post post = new Post();
                                    post.setUserId(jsonArray.getJSONObject(i).getJSONObject("owner").getString("id"));
                                    post.setEmail(jsonArray.getJSONObject(i).getJSONObject("owner").getString("email"));
                                    post.setTitle(jsonArray.getJSONObject(i).getJSONObject("owner").getString("title"));
                                    post.setUserPicture(jsonArray.getJSONObject(i).getJSONObject("owner").getString("picture"));
                                    post.setFirstName(jsonArray.getJSONObject(i).getJSONObject("owner").getString("firstName"));
                                    post.setLastName(jsonArray.getJSONObject(i).getJSONObject("owner").getString("lastName"));
                                    post.setPostId(jsonArray.getJSONObject(i).getString("id"));
                                    post.setPostImage(jsonArray.getJSONObject(i).getString("image"));
                                    post.setPublishDate(jsonArray.getJSONObject(i).getString("publishDate"));
                                    post.setText(jsonArray.getJSONObject(i).getString("text"));
                                    post.setLink(jsonArray.getJSONObject(i).getString("link"));
                                    post.setLikes(jsonArray.getJSONObject(i).getString("likes"));
//                                    String tagsString[] = new String[jsonArray.getJSONObject(i).getJSONArray("tags").length()];
//                                    for(int j=0;i<jsonArray.getJSONObject(i).getJSONArray("tags").length();j++){
//                                        tagsString[i] = String.valueOf(jsonArray.getJSONObject(i).getJSONArray("tags").get(j));
//                                    }
                                    postsList.add(post);
                                }
                                progressDialog.dismiss();
                                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(PostActivity.this);
                                PostAdapter postAdapter = new PostAdapter(postsList, PostActivity.this);
                                postsRecyclerView.setAdapter(postAdapter);
                                postsRecyclerView.setLayoutManager(linearLayoutManager);
                                Log.d("Posts", response);
                            } catch (JSONException e) {
                                System.out.println("error " + e);
                                somethingWentWrong();
                            }
                        }
                    }, new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println(error);
                            somethingWentWrong();
                        }
                    }) {
                        @Override
                        public Map<String, String> getHeaders() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("app-id", "605464d6fbe55f7692242069");
                            return params;
                        }
                    };

                    RequestQueue Queue = Volley.newRequestQueue(PostActivity.this);
                    Queue.add(stringReques);
                }catch(Exception e){
                    e.printStackTrace();
                    somethingWentWrong();
                }
        }else{
            progressDialog.dismiss();
            noInternetLayout.setVisibility(View.VISIBLE);
        }
    }

    public void somethingWentWrong() {
        progressDialog.dismiss();
        String title = "Oops !!!";
        String desc = "Something went wrong..\n Please try again later.";
        String ok = "OK";
        alertDialogBuilder.setTitle(title);
        alertDialogBuilder
                .setMessage(desc)
                .setCancelable(true)
                .setPositiveButton(ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        onBackPressed();
                    }
                });
        alertDialog = alertDialogBuilder.create();
        alertDialog.show();
        alertDialog.setCancelable(false);
        alertDialog.setCanceledOnTouchOutside(false);
    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

}