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
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.hoodootechnologies.R;
import com.example.hoodootechnologies.adapter.CommentAdapter;
import com.example.hoodootechnologies.adapter.PostAdapter;
import com.example.hoodootechnologies.pojo.Comment;
import com.example.hoodootechnologies.pojo.Post;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommentActivity extends AppCompatActivity {

    private RecyclerView commentsRecyclerView;
    private ProgressDialog progressDialog;
    private AlertDialog alertDialog;
    private AlertDialog.Builder alertDialogBuilder;
    private LinearLayout noInternetLayout;
    private ImageView refresh, goBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        noInternetLayout = findViewById(R.id.comment_noi);
        refresh = findViewById(R.id.comment_refresh);
        commentsRecyclerView = findViewById(R.id.comment_rec_view);
        progressDialog = ProgressDialog.show(CommentActivity.this, "", "Loading...Please wait...");
        alertDialogBuilder = new AlertDialog.Builder(CommentActivity.this);
        goBack = findViewById(R.id.go_back);
        fetchCommentsData();

        goBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CommentActivity.this,CommentActivity.class));
                finish();
            }
        });

    }

    private void fetchCommentsData(){
        if (isNetworkAvailable(CommentActivity.this)) {
            try {
                String url = "https://dummyapi.io/data/api/post/"+PostAdapter.commentId+"/comment";

                StringRequest stringReques = new StringRequest(Request.Method.GET, url, new com.android.volley.Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            List<Comment> commentList = new ArrayList<>();
                            JSONObject jsonObject = new JSONObject(response);
                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            for (int i = 0; i < jsonArray.length(); i++) {
                                Comment comment = new Comment();
                                comment.setUserId(jsonArray.getJSONObject(i).getJSONObject("owner").getString("id"));
                                comment.setTitle(jsonArray.getJSONObject(i).getJSONObject("owner").getString("title"));
                                comment.setFirstName(jsonArray.getJSONObject(i).getJSONObject("owner").getString("firstName"));
                                comment.setLastName(jsonArray.getJSONObject(i).getJSONObject("owner").getString("lastName"));
                                comment.setEmail(jsonArray.getJSONObject(i).getJSONObject("owner").getString("email"));
                                comment.setPicture(jsonArray.getJSONObject(i).getJSONObject("owner").getString("picture"));
                                comment.setcommentId(jsonArray.getJSONObject(i).getString("id"));
                                comment.setMessage(jsonArray.getJSONObject(i).getString("message"));
                                comment.setPublishDate(jsonArray.getJSONObject(i).getString("publishDate"));
                                commentList.add(comment);
                            }
                            progressDialog.dismiss();
                            if(commentList.size()==0) {
                                Toast.makeText(CommentActivity.this, "No Item Available !!", Toast.LENGTH_LONG).show();
                            }
                            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(CommentActivity.this);
                            CommentAdapter commentAdapter = new CommentAdapter(commentList, CommentActivity.this);
                            commentsRecyclerView.setAdapter(commentAdapter);
                            commentsRecyclerView.setLayoutManager(linearLayoutManager);
                            Log.d("Comments", response);
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
                }){
                    @Override
                    public Map<String, String> getHeaders() throws AuthFailureError {
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("app-id", "605464d6fbe55f7692242069");
                        return params;
                    }
                };

                RequestQueue Queue = Volley.newRequestQueue(CommentActivity.this);
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