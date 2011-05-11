package selector.objects;

import java.awt.*;
import java.awt.event.*;
import java.awt.font.*;
import java.awt.geom.*;
import java.awt.image.*;
import static java.awt.Color.*;
import static java.awt.image.BufferedImage.*;
import static java.lang.Math.*;

public class Game {

    private final Title title;
    private final Thumbnail thumb;
    private final BufferedImage image;

    public final String name;
    public final String cmd;

    public static final int width = 400;
    public static final int height = 360;

    public Game(String title, Image thumb, String cmd){
	this.name  = title;
	this.cmd   = cmd;

	this.title = new Title(title);
	this.thumb = new Thumbnail(thumb);
	this.image = new BufferedImage(width, height, TYPE_INT_ARGB);

	this.setFocused(false);
    }

    public void update(){
	// Ivoke update functions recursively
	this.thumb.update();
	this.title.update();

	Graphics2D g = (Graphics2D)image.getGraphics();

	// Clear
	g.setColor(new Color(0,0,0,0));
	g.fillRect(0,0,width,height);

	// Thumbnail
	g.drawImage(thumb.getImage(),(width-thumb.width)/2, 20,null);
	g.drawImage(title.getImage(),(width-title.width)/2,290,null);
    }

    public Image getImage(){
	return this.image;
    }

    public void setFocused(boolean focused){
	this.thumb.setFocused(focused);
    }
}

class Title {
    private static final Font font; 
    private static final FontRenderContext frc;

    private final Shape title;
    private final BufferedImage image;

    public final int width;
    public final int height;

    static {
	font = new Font("Sans", Font.BOLD, 30);
	frc  = new FontRenderContext(null,true,true);
    }

    public Title(String title){
	this.title = new TextLayout(title, font, frc).getOutline(null);
	this.width = (int)this.title.getBounds().getWidth();
	this.height = (int)this.title.getBounds().getHeight();
	this.image = new BufferedImage(width, height, TYPE_INT_ARGB);

	double dx = -this.title.getBounds().getX();
	double dy = -this.title.getBounds().getY();
	AffineTransform at = AffineTransform.getTranslateInstance(dx,dy);

	Graphics2D g = (Graphics2D)this.image.getGraphics();
	g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
			   RenderingHints.VALUE_ANTIALIAS_ON);
	g.setPaint(new Color(200,200,200));
	g.fill(at.createTransformedShape(this.title));
    }

    public void update(){}
    
    public Image getImage(){
	return this.image;
    }
}

class Thumbnail {

    private int dark;
    private int dest;
    private BufferedImage[] images;

    public static final int width = 360;
    public static final int height = 240;
    public static final int step = 30;
    public static final float dim = 0.5f;

    public Thumbnail(Image orig){
	orig = orig.getScaledInstance(width, height, SCALE_SMOOTH);

	// Prerendering
	this.images = new BufferedImage[step];
	for(int i=0;i<step;i++){
	    images[i] = new BufferedImage(width,height,TYPE_INT_ARGB);
	    Graphics2D g = (Graphics2D)images[i].getGraphics();
	    g.drawImage(orig,0,0,null);
	    g.setColor(new Color(0f,0f,0f,dim/step*i));
	    g.fillRect(0,0,width,height);
	}

	this.dark = 0;
	this.dest = 0;

	this.setFocused(false);
    }

    public void setFocused(boolean flag){
	if(flag)    this.dest = 0;
	else	    this.dest = step-1;
    }

    public void update(){
	if(dark < dest)
	    dark = min(dark+2, step-1);
	else if(dark > dest)
	    dark = max(dark-2, 0);
    }

    public Image getImage(){
	return images[dark];
    }
}
