package com.parse.starter.activity;

import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.starter.R;
import com.squareup.picasso.Picasso;

public class FloatingActivity extends AppCompatActivity {

    private ImageView imgFloat;
    private TextView user;
    private String imgURL;
    private String userAbout;
    private ImageButton btnFechar;
    private View parentLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_floating);
        parentLayout = this.findViewById(android.R.id.content);

        Bundle bundle = getIntent().getExtras();

        Snackbar.make(parentLayout, R.string.touch_image, Snackbar.LENGTH_SHORT)
                .setAction("Action", null).show();

        imgFloat = (ImageView)findViewById(R.id.image_float);
        user = (TextView)findViewById(R.id.txtFloat);
       // btnFechar = (ImageButton)findViewById(R.id.btnFloat);
/*        btnFechar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });*/

        imgURL = bundle.getString("image");
        userAbout = bundle.getString("user");

        Picasso.with(this)
                .load(imgURL)
                .fit()
                .into(imgFloat);

        user.setText(userAbout);

        imgFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.getVisibility() == View.VISIBLE){
                    user.setVisibility(View.INVISIBLE);
                }else{
                    user.setVisibility(View.VISIBLE);
                }
            }
        });

    }
}
