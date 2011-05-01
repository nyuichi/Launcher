package selector.objects;

import java.awt.*;
import java.awt.image.*;
import static java.awt.Color.*;
import static java.awt.image.BufferedImage.*;
import static java.lang.Math.*;

public class Cursor {

    public final Point p;		// Current position
    public final Point q;		// Destination

    public static final int width = 400;
    public static final int height = 280;
    public static final int thickness = 5;

    private final BufferedImage image;

    public Cursor(Point p){
	this.p = new Point(p);
	this.q = new Point(p);

	this.image = new BufferedImage(width, height, TYPE_INT_ARGB);

	// Prerendering
	Graphics2D g = (Graphics2D)image.getGraphics();
	g.setColor(WHITE);
	g.drawRect(0, 0, width, thickness);
	g.drawRect(0, 0, thickness, height);
	g.drawRect(0, height - thickness-1, width, thickness);
	g.drawRect(width - thickness-1, 0, thickness, height);
    }

    public void update(){
	if(p.x > q.x)	    p.x -= kernel(p.x-q.x);
	else if(p.x < q.x)  p.x += kernel(q.x-p.x);

	if(p.y > q.y)	    p.y -= kernel(p.y-q.y);
	else if(p.y < q.y)  p.y += kernel(q.y-p.y);
    }

    // Kernel function for calculating the speed
    private int kernel(int d){
	if(d <= 1){
	    return d;
	}else if(d >= 45){
	    return (int)(pow(1.07,45)*1.5);
	}else {
	    return (int)(pow(1.07,d)*1.5);
	}
    }

    public Image getImage(){
	return this.image;
    }
}

