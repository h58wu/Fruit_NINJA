/**
 * CS349 Winter 2014
 * Assignment 4 Demo Code
 * Jeff Avery
 */
package com.example.a4;
import android.graphics.*;
import android.util.Log;

/**
 * Class that represents a Fruit. Can be split into two separate fruits.
 */
public class Fruit {
    private Path path = new Path();
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Matrix transform = new Matrix();
    public boolean			 cutted = false;
    public double				v_x;
    public double				v_y;
    public int					time_count = 0;
    public boolean			start_fru = false;
    public boolean			star_fru = false;
    public float			scale_value = 1;
    public boolean			flash = false;

    /**
     * A fruit is represented as Path, typically populated 
     * by a series of points 
     */
    Fruit(float[] points) {
        init();
        this.path.reset();
        this.path.moveTo(points[0], points[1]);
        for (int i = 2; i < points.length; i += 2) {
            this.path.lineTo(points[i], points[i + 1]);
        }
        this.path.moveTo(points[0], points[1]);
    }
    
    Fruit(Region region) {
        init();
        this.path = region.getBoundaryPath();
    }

    Fruit(Path path) {
        init();
        this.path = path;
    }

    
    
    private void init() {
        this.paint.setColor(Color.BLUE);
        this.paint.setStrokeWidth(5);
    }

    /**
     * The color used to paint the interior of the Fruit.
     */
    public int getFillColor() { return paint.getColor(); }
    public void setFillColor(int color) { paint.setColor(color); }

    /**
     * The width of the outline stroke used when painting.
     */
    public double getOutlineWidth() { return paint.getStrokeWidth(); }
    public void setOutlineWidth(float newWidth) { paint.setStrokeWidth(newWidth); }

    /**
     * Concatenates transforms to the Fruit's affine transform
     */
    public void rotate(float theta) { transform.postRotate(theta); }
    public void scale(float x, float y) { transform.postScale(x, y); }
    public void translate(float tx, float ty) { transform.postTranslate(tx, ty); }
    

    /**
     * Returns the Fruit's affine transform that is used when painting
     */
    public Matrix getTransform() { return transform; }

    /**
     * The path used to describe the fruit shape.
     */
    public Path getTransformedPath() {
        Path originalPath = new Path(path);
        Path transformedPath = new Path();
        originalPath.transform(transform, transformedPath);
        return transformedPath;
    }

    /**
     * Paints the Fruit to the screen using its current affine
     * transform and paint settings (fill, outline)
     */
    public void draw(Canvas canvas) {
        // TODO BEGIN CS349
    	if(flash){
    		double rand_color = Math.random();
    		if(rand_color <= 0.25) setFillColor(Color.BLUE);
    		else if(rand_color <= 0.5) setFillColor(Color.RED);
    		else if(rand_color <= 0.7) setFillColor(Color.YELLOW);
    		else setFillColor(Color.GREEN);
    	}
    	canvas.drawPath(getTransformedPath(),paint);    	
    	// TODO END CS349
    }
    
    
    public void translate_time(){
    	translate((float) (v_x * time_count),(float)( -v_y * time_count + 0.01 * time_count * time_count / 2));
    	time_count++;
    }
    
    public boolean gone(){
    	RectF bounds = new RectF();
        getTransformedPath().computeBounds(bounds,false);
        float center_y = bounds.centerY();
    	if (center_y > 700)
    		return true;
    	return false;
    }
    

    /**
     * Tests whether the line represented by the two points intersects
     * this Fruit.
     */
    public boolean intersects(PointF p1, PointF p2) {    	
    	if(cutted) return false;
    	float x1 = p1.x;
        float y1 = p1.y;
        float x2 = p2.x;
        float y2 = p2.y;
        RectF bounds = new RectF();
        getTransformedPath().computeBounds(bounds,false);
        float center_x = bounds.centerX();
        float center_y = bounds.centerY();
        float radius = (float) (bounds.height() * 0.5);
        float slope = (y2-y1)/(x2-x1);
        float anti_slope = -1/slope;
        float intersection_origin = y2 - slope * x2;
        float intersection_anti = center_y - anti_slope*center_x;
        float intersect_line_x = (intersection_anti - intersection_origin)/(slope - anti_slope);
        float intersect_line_y = slope * intersect_line_x +intersection_origin;
        float distance = (float) Math.sqrt((center_x - intersect_line_x)*(center_x - intersect_line_x)+
        				(center_y - intersect_line_y)*(center_y - intersect_line_y));
        if(intersect_line_x < x1 && intersect_line_x <x2) return false;
        if(intersect_line_x > x1 && intersect_line_x >x2) return false;
        
        if(bounds.contains(x1, y1)) return false;
        if(bounds.contains(x2, y2)) return false;
        if(distance < radius) return true;
        // TODO END CS349
        return false;
    }

    /**
     * Returns whether the given point is within the Fruit's shape.
     */
    public boolean contains(PointF p1) {
        Region region = new Region();
        boolean valid = region.setPath(getTransformedPath(), new Region());
        return valid && region.contains((int) p1.x, (int) p1.y);
    }

    /**
     * This method assumes that the line represented by the two points
     * intersects the fruit. If not, unpredictable results will occur.
     * Returns two new Fruits, split by the line represented by the
     * two points given.
     */
    public Fruit[] split(PointF p1, PointF p2) {
    	Path topPath = new Path();
    	Path bottomPath = new Path();
    	if(p1.x > p2.x){
    		PointF temp = p1;
    		p1 = p2;
    		p2 = temp;
    	}
    	
    	topPath.moveTo(p1.x,p1.y);
    	topPath.lineTo(p2.x,p2.y);
    	topPath.lineTo(480,p2.y);
    	topPath.lineTo(480,0);
    	topPath.lineTo(0,0);
    	topPath.lineTo(0,p1.y);
    	topPath.lineTo(p1.x,p1.y);
    	
    	topPath.op(getTransformedPath(),Path.Op.INTERSECT);
    	
    	
    	bottomPath.moveTo(p1.x,p1.y);
    	bottomPath.lineTo(p2.x,p2.y);
    	bottomPath.lineTo(480,p2.y);
    	bottomPath.lineTo(480,800);
    	bottomPath.lineTo(0,800);
    	bottomPath.lineTo(0,p1.y);
    	bottomPath.lineTo(p1.x,p1.y);
    	
    	bottomPath.op(getTransformedPath(),Path.Op.INTERSECT);
    	
    	
    	
        if (topPath != null && bottomPath != null && (!topPath.isEmpty()) && (!bottomPath.isEmpty())) {
           return new Fruit[] { new Fruit(topPath), new Fruit(bottomPath) };
        }
        return new Fruit[0];
    }
}
