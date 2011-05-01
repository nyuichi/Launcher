package selector;

import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.*;
import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import static java.awt.Color.*;
import static java.awt.event.KeyEvent.*;
import static java.awt.image.BufferedImage.*;

import selector.objects.*;
import selector.effects.*;

public class Selector implements KeyListener {

    public final int w;
    public final int h;
    public final Point p;

    public final Cursor cursor;
    public final Game[][] games;

    public final int width;
    public final int height;
    public final Noise noise;
    private final BufferedImage image;

    public static final int vborder = 20;
    public static final int hborder = 50;
    public static final int vspace = 20;
    public static final int hspace = 20;

    public Selector(int w, int h, Game[][] games){
	this.w = w;
	this.h = h;
	this.p = new Point(0,0);

	this.cursor = new Cursor(new Point(vborder, hborder));
	this.games = games;
	this.games[0][0].setFocused(true);

	this.width = Game.width * w + vspace * (w-1) + vborder * 2;
	this.height = Game.height * h + hspace * (h-1) + hborder * 2;
	this.noise = new Noise(width, height);
	this.image = new BufferedImage(width, height, TYPE_INT_ARGB);
    }

    // Listen to key event
    public void keyReleased(KeyEvent e){}
    public void keyTyped(KeyEvent e){}
    public void keyPressed(KeyEvent e){
	Point q = new Point(p);

	switch(e.getKeyCode()){
	case VK_UP:    q.y-=1; break;
	case VK_DOWN:  q.y+=1; break;
	case VK_LEFT:  q.x-=1; break;
	case VK_RIGHT: q.x+=1; break;
	}

	if(0 <= q.x && q.x < w &&
	   0 <= q.y && q.y < h &&
	   games[q.x][q.y] != null){
	    cursor.q.setLocation(Game.width*q.x+vspace*q.x+vborder,
				 Game.height*q.y+hspace*q.y+hborder);
	    games[p.x][p.y].setFocused(false);
	    games[q.x][q.y].setFocused(true);
	    p.setLocation(q);
	}
    }

    public void update(){
	// Ivoke update functions recursively.
	noise.update();
	cursor.update();
	for(int x=0;x<w;x++)
	    for(int y=0;y<h;y++)
		if(games[x][y] != null)
		    games[x][y].update();

	// Draw
	Graphics2D g = (Graphics2D)image.getGraphics();
	g.setColor(GRAY);
	g.fillRect(0,0,width,height);
	noise.draw(g);
	for(int x=0;x<w;x++)
	    for(int y=0;y<h;y++)
		if(games[x][y] != null)
		    g.drawImage(games[x][y].getImage(),
				Game.width * x + vspace * x + vborder,
				Game.height * y + hspace * y + hborder,
				null);
	g.drawImage(cursor.getImage(), cursor.p.x, cursor.p.y, null);
    }

    public Image getImage(){
	return this.image;
    }
}
