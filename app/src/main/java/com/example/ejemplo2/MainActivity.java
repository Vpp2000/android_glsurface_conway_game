package com.example.ejemplo2;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MainActivity extends Activity {
    Button btniniser;
    private GLSurfaceView mGLView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        btniniser = (Button) findViewById(R.id.btnStart);
//
//        btniniser.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View arg0) {
                mGLView = new MyGLSurfaceView(getApplicationContext());
                setContentView(mGLView);
//            }
//        });
    }
}