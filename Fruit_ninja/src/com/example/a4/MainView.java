/**
 * CS349 Winter 2014
 * Assignment 4 Demo Code
 * Jeff Avery & Michael Terry
 */
package com.example.a4;

import android.content.Context;
import android.graphics.*;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;

import java.util.Iterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Timer;
import java.util.TimerTask;


/*
 * View of the main game area.
 * Displays pieces of fruit, and allows players to slice them.
 */
public class MainView extends View implements Observer {
    private final Model model;
    private final MouseDrag drag = new MouseDrag();
    private Timer timer;

    // Constructor
    MainView(Context context, Model m) {
        super(context);

        // register this view with the model
        model = m;
        model.addObserver(this);

        // TODO BEGIN CS349
        // test fruit, take this out before handing in!
        Fruit f1 = new Fruit(new float[] {0, 20, 20, 0, 40, 0, 60, 20, 60, 40, 40, 60, 20, 60, 0, 40});
        f1.translate(220, 220);
        f1.star_fru = true;
        f1.setFillColor(Color.BLACK);
        model.add(f1);

        // TODO END CS349
        start();
        // add controller
        // capture touch movement, and determine if we intersect a shape
        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        // Log.d(getResources().getString(R.string.app_name), "Touch down");
                        drag.start(event.getX(), event.getY());
                        break;

                    case MotionEvent.ACTION_UP:
                        // Log.d(getResources().getString(R.string.app_name), "Touch release");
                        drag.stop(event.getX(), event.getY());

                        // find intersected shapes
                        Iterator<Fruit> i = model.getShapes().iterator();
                        while(i.hasNext()) {
                            Fruit s = i.next();
                            if (s.intersects(drag.getStart(), drag.getEnd())) {
                            	if(s.star_fru){
                            		mystart();
                            		model.score--;
                     
                            	}
                            		
                            	model.score++;
                            	if(s.flash) model.score += 9;
                                try {
                                    Fruit[] newFruits = s.split(drag.getStart(), drag.getEnd());

                                    // TODO BEGIN CS349
                                    // you may want to place the fruit more carefully than this
                                    newFruits[0].translate(0, -10);
                                    newFruits[0].cutted = true;
                                    newFruits[0].setFillColor(Color.GRAY);
                                    newFruits[0].v_x = s.v_x;
                                    newFruits[0].v_y = s.v_y;
                                    newFruits[0].time_count = s.time_count;
                                    newFruits[1].translate(0, +10);
                                    newFruits[1].cutted = true;
                                    newFruits[1].setFillColor(Color.GRAY);
                                    newFruits[1].v_x = s.v_x;
                                    newFruits[1].v_y = s.v_y;
                                    newFruits[1].time_count = s.time_count;
                                    // TODO END CS349
                                    model.add(newFruits[0]);
                                    model.add(newFruits[1]);

                                    // TODO BEGIN CS349
                                    model.remove(s);
                                    if(model.score > 50 * ( 1 + model.level % 1 * 20)){
                                    	model.level += 0.1;
                                    }
                                    
                                    // TODO END CS349

                                } catch (Exception ex) {
                                    Log.e("fruit_ninja", "Error: " + ex.getMessage());
                                }
                            } else {
                            }
                            invalidate();
                        }
                        break;
                }
                return true;
            }
        });
    }
    
    final Handler timer_event = new Handler(new Callback(){
    	@Override
    	public boolean handleMessage(Message msg){
    		if(Math.random() <= 0.03 * model.level && model.start){
    			Fruit f = new Fruit(new float[] {0, 20, 20, 0, 40, 0, 60, 20, 60, 40, 40, 60, 20, 60, 0, 40});
                double rand_num = Math.random() * 1000 % 440;
                f.translate((float)rand_num,(float) 565);
                if(rand_num > 240){
                	f.v_x = - Math.random() % 0.1;
                	
                }
                else{
                	f.v_x = Math.random() % 0.1;
                }
                f.v_y = 0.5;
                double rand_color = Math.random();
                if(rand_color <= 0.2) f.setFillColor(Color.BLUE);
                else if(rand_color <= 0.4) f.setFillColor(Color.RED);
                else if(rand_color <= 0.6) f.setFillColor(Color.YELLOW);
                else if(rand_color <= 0.8) f.setFillColor(Color.GREEN);
                else f.flash = true;
                
                model.add(f);
    		}
    		
    		
    		Iterator<Fruit> i = model.getShapes().iterator();
            while(i.hasNext()) {
                Fruit s = i.next();
               if(!s.star_fru){
                s.time_count++;
                if(!s.start_fru) s.translate_time();
                invalidate();
            	if(s.gone() && (!s.cutted)) model.miss++;
            	if(model.miss > 5 ){
            		mystop();
            	}
            	if(s.gone()){
            		model.remove(s);
            	}
               }
            }
            invalidate();
            return false;
    	}
    });
    
    class myTask extends TimerTask{
    	@Override
    	public void run(){
    		timer_event.sendEmptyMessage(0);
    	}
    };
    
    
    
    

    // inner class to track mouse drag
    // a better solution *might* be to dynamically track touch movement
    // in the controller above
    class MouseDrag {
        private float startx, starty;
        private float endx, endy;

        protected PointF getStart() { return new PointF(startx, starty); }
        protected PointF getEnd() { return new PointF(endx, endy); }

        protected void start(float x, float y) {
            this.startx = x;
            this.starty = y;
        }

        protected void stop(float x, float y) {
            this.endx = x;
            this.endy = y;
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // draw background
        setBackgroundColor(Color.WHITE);

        // draw all pieces of fruit
        for (Fruit s : model.getShapes()) {
            s.draw(canvas);
        }
    }

    @Override
    public void update(Observable observable, Object data) {
        invalidate();
    }
    
    public void start(){
    	timer = new Timer();
    	timer.schedule(new myTask(),0,50);
    }
     public void mystart(){
    	 model.start = true;
    	 model.miss = 0;
    	 model.score = 0;
    	 model.level = 1;
     }
     
     public void mystop(){
    	 Iterator<Fruit> i = model.getShapes().iterator();
         while(i.hasNext()) {
             Fruit s = i.next();
             model.remove(s);
         }
         model.start = false;
         Fruit f1 = new Fruit(new float[] {0, 20, 20, 0, 40, 0, 60, 20, 60, 40, 40, 60, 20, 60, 0, 40});
         f1.translate(220, 220);
         f1.setFillColor(Color.BLACK);
         f1.star_fru = true;
         model.add(f1);
     }
     
}
