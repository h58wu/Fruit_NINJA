/**
 * CS349 Winter 2014
 * Assignment 4 Demo Code
 * Jeff Avery & Michael Terry
 */
package com.example.a4;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.widget.TextView;
import com.example.a4complete.R;

import java.util.Observable;
import java.util.Observer;

/*
 * View to display the Title, and Score
 * Score currently just increments every time we get an update
 * from the model (i.e. a new fruit is added).
 */
public class TitleView extends TextView implements Observer {
    private int count = 0;
    private Model model;

    // Constructor requires model reference
    public TitleView(Context context, Model model) {
        super(context);
        this.model = model;

        // set width, height of this view
        this.setHeight(235);
        this.setWidth(MainActivity.displaySize.x);

        // register with model so that we get updates
        model.addObserver(this);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // TODO BEGIN CS349
        // add high score, anything else you want to display in the title
        // TODO END CS349
        setBackgroundColor(Color.GREEN);
        setTextSize(14);
        if(!model.start)
        	setText(getResources().getString(R.string.app_title) + "   HIGHEST SCORE:    " + model.score + "     MISSED:    " + 0 +
        		"          cut the fruit to begin   "  + "    LEVEL:" +(int) (model.level%1 *10 +1));
        else
        	setText(getResources().getString(R.string.app_title) + "   HIGHEST SCORE:    " + model.score + "     MISSED:    " + model.miss+
        			"          flashing one worth 10!!!   "+ "    LEVEL:" +(int) (model.level%1 *10 +1)	);
    }

    // Update from model
    // ONLY useful for testing that the view notifications work
    @Override
    public void update(Observable observable, Object data) {
        // TODO BEGIN CS349
        // do something more meaningful here
        // TODO END CS349
        count++;
        invalidate();
    }
}
