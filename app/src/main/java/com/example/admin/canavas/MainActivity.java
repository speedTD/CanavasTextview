package com.example.admin.canavas;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.UUID;

public class MainActivity extends AppCompatActivity {
    private Button btn_Save,btn_process,btn_choise;
    private TextView mText;
    private EditText medit;
    private ImageView imageView;
    private int REQUEST_CODE=111;
    private Uri uri;
    Bitmap bitmapEnd;


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_choise=(Button) findViewById(R.id.id_choise);
        btn_process=(Button) findViewById(R.id.id_process);
        btn_Save=(Button) findViewById(R.id.id_save);
        medit=(EditText) findViewById(R.id.id_input);
        mText=(TextView) findViewById(R.id.id_text);
        imageView=(ImageView) findViewById(R.id.id_img);


        // set click Open Grally alt+ enter

        btn_choise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(intent,REQUEST_CODE);
            }
        });
        btn_process.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mText.setText(medit.getText().toString());
                if(uri!=null){

                    bitmapEnd=ProcessIMG(uri,mText);
                    imageView.setImageBitmap(bitmapEnd);
                    String path= Environment.getExternalStorageDirectory()+"/"+ UUID.randomUUID().toString()+".jpg";
                    File file=new File(path);
                    OutputStream outputStream=null;
                    try {
                        outputStream=new FileOutputStream(file);
                        bitmapEnd.compress(Bitmap.CompressFormat.JPEG,100,outputStream);
                        Toast.makeText(MainActivity.this, "File Save"+file.getPath(), Toast.LENGTH_SHORT).show();

                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }finally {
                        if(outputStream!=null){
                            try {
                                outputStream.flush();
                                outputStream.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }
            }
        });
        btn_Save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        //Move Text
        mText.setOnTouchListener(new View.OnTouchListener() {
            private float ititX;
            private float ititY;
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch (motionEvent.getActionMasked()){
                    case MotionEvent.ACTION_DOWN:
                        ititX=view.getX()-motionEvent.getRawX();
                        ititY=view.getY()-motionEvent.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        view.animate()
                                .setDuration(0)
                                .y(ititY+motionEvent.getRawY())
                                .x(ititX+motionEvent.getRawX())
                                .start();
                        break;

                     default:
                            return false;

                }

                return true;
            }
        });



    }


    private Bitmap ProcessIMG(Uri uri, TextView mText) {
        Bitmap newBitmap=null;
        Bitmap bitmap=null;

        try {
            bitmap= BitmapFactory.decodeStream(getContentResolver().openInputStream(uri));
            Bitmap.Config config=bitmap.getConfig();
            if(config==null){
                config=Bitmap.Config.ARGB_8888;
            }
            newBitmap=  Bitmap.createBitmap(bitmap.getWidth(),bitmap.getHeight(),config);
            Canvas canvas=new Canvas(newBitmap);
            canvas.drawBitmap(bitmap,0,0,null);
            String text=mText.getText().toString();
            if(text!=null){
                //paint
                Paint paint=new Paint(Paint.SUBPIXEL_TEXT_FLAG);
                paint.setTextSize(mText.getTextSize());
                paint.setColor(Color.GREEN);
                Rect rect=new Rect();
                paint.getTextBounds(text,0,text.length(),rect);
                canvas.drawText(text,mText.getX(),mText.getY(),paint);

            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        return newBitmap;
    }

    //Result and Set imageview
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE&&resultCode==RESULT_OK&&data!=null){
            uri=data.getData();
            imageView.setImageURI(uri);
        }
    }
}
