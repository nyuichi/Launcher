package selector.effects;

import java.awt.*;
import static java.awt.Color.*;

public class Noise {

    private final int width;
    private final int height;

    private int x1;
    private int x2;
    private int y1;
    private int y2;

    public static final int step = 1;

    public Noise(int width, int height){
	this.width = width;
	this.height = height;

	this.x1 = 0;
	this.x2 = 0;
	this.y1 = 0;
	this.y2 = 0;
    }

    public void update(){
	this.x1 = (x1 > width)? 0 : x1+step;
	this.x2 = (x2 > width)? 0 : x2+step*3;
	this.y1 = (y1 > height)? 0 : y1+step*2;
	this.y2 = (y2 > height)? 0 : y2+step*3;
    }

    public void draw(Graphics2D g){
	g.setColor(WHITE);
	g.drawLine(x1, 0, x1, height);
	g.drawLine(x2, 0, x2, height);
	g.drawLine(0, y1, width, y1);
	g.drawLine(0, y2, width, y2);
    }
}


