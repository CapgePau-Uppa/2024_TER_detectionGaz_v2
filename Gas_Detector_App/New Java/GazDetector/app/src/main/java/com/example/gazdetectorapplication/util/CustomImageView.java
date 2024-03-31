package com.example.gazdetectorapplication.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;

public class CustomImageView extends androidx.appcompat.widget.AppCompatImageView {

    public Bitmap bpm;
    Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);





    public CustomImageView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }



    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
    }



    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
    }

    public void drawOnImage(float ratioCoordinate, ArrayList<Room> path, int imageID){
        setImageResource(imageID);
        p.setColor(Color.RED);
        p.setStrokeWidth(20);
        p.setStyle(Paint.Style.FILL);
        this.setMaxWidth(this.getDrawable().getIntrinsicWidth());
        this.setMaxHeight(this.getDrawable().getIntrinsicHeight());
        //this.setMinimumHeight(this.getDrawable().getIntrinsicHeight());
        Bitmap b =  Bitmap.createBitmap(
                this.getDrawable().getIntrinsicWidth(),
                this.getDrawable().getIntrinsicHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        c.save();
        c.drawBitmap(((BitmapDrawable)this.getDrawable()).getBitmap(),0,0,null);
        for(int i = 0; i<path.size();i++){
            Pair middle = path.get(i).getMiddle();
            if(i==0){
                c.drawCircle(middle.getX()*ratioCoordinate,middle.getY()*ratioCoordinate,30,p);
            }
            if(i==path.size()-1){
                //si c'est la dernière salle
                c.drawLine(middle.getX()*ratioCoordinate, middle.getY()*ratioCoordinate,
                        path.get(i).getEnd().get(0).getX()*ratioCoordinate,
                        path.get(i).getEnd().get(0).getY()*ratioCoordinate,p);
                c.drawCircle(path.get(i).getEnd().get(0).getX()*ratioCoordinate,path.get(i).getEnd().get(0).getY()*ratioCoordinate,30,p);
            }else{//sinon
                HashMap<Integer, Pair> rooms = path.get(i).getDoors();
                Pair c2=null;
                for(Integer j : rooms.keySet()){
                    if(path.get(i+1).getId()==j){
                        c2=rooms.get(j);
                    }
                }
                c.drawLine(middle.getX()*ratioCoordinate, middle.getY()*ratioCoordinate,
                        c2.getX()*ratioCoordinate, c2.getY()*ratioCoordinate,p);
                Pair middleNext = path.get(i+1).getMiddle();
                c.drawLine(c2.getX()*ratioCoordinate, c2.getY()*ratioCoordinate,
                        middleNext.getX()*ratioCoordinate, middleNext.getY()*ratioCoordinate,p);
            }
        }
        this.setImageDrawable(new BitmapDrawable(getResources(),b));
    }




}
