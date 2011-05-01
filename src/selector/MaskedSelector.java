package selector;

import java.awt.*;
import java.awt.image.*;
import static java.lang.Math.*;

import selector.objects.*;

public class MaskedSelector extends Selector {
    private final int mwidth;
    private final int mheight;
    private final Point mpoint;
    private final int bheight;

    public MaskedSelector(int w, int h, Game[][] games, int height){
	super(w, h, games);
	this.mwidth = width-vborder*2;
	this.mheight = height-hborder*2;
	this.mpoint = new Point(vborder,hborder);
	this.bheight = height;
    }

    public void update(){
	super.update();

	if(cursor.p.x < mpoint.x){
	    mpoint.x = cursor.p.x;
	}else if(cursor.p.x + Game.width > mpoint.x + mwidth){
	    mpoint.x = cursor.p.x + Game.width - mwidth;
	}

	if(cursor.p.y < mpoint.y){
	    mpoint.y = cursor.p.y;
	}else if(cursor.p.y + Game.height > mpoint.y + mheight){
	    mpoint.y = cursor.p.y + Game.height - mheight;
	}
    }

    public Image getImage(){
	BufferedImage image = (BufferedImage)super.getImage();
	return image.getSubimage(mpoint.x-vborder,mpoint.y-hborder,
				 width,min(height, bheight));
    }
}