import java.awt.GraphicsEnvironment;
import java.awt.GraphicsDevice;
import java.awt.DisplayMode;
import java.awt.GraphicsConfiguration;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Image;
import java.awt.Font;
import java.awt.Shape;
import java.awt.Point;
import java.awt.font.*;
import java.awt.image.*;
import java.awt.geom.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.Scanner;
import java.io.*;
import javax.imageio.*;
import static java.awt.Color.*;
import static java.lang.Math.*;
import static java.awt.event.KeyEvent.*;
import static java.awt.geom.AffineTransform.*;

public class Main {
    public static void main(String[] args) throws Exception {
	new Launcher();
    }
}

class Launcher extends JPanel {

    private final MainWindow window;
    private final int width;
    private final int height;

    private final Game[][] games;
    private final int n,m;
    private final Point pointer;

    private final Cursor cursor;
    private final Title title;
    private final Contents contents;
    private final Thumbnail[][] thumbs;

    private Timer timer;

    public Launcher() throws Exception{
	super(true);
	this.setFocusable(true);

	this.window = new MainWindow();
	this.width = window.width;
	this.height = window.height;

	final Config config = Config.loadConfig("resources/launcher.conf");
	this.games = config.games;
	this.n = config.n;
	this.m = config.m;

	this.pointer = new Point(0,0);
	this.cursor = new Cursor(games[0][0].point);
	this.title  = new Title(games[0][0].title);
	this.contents = new Contents(games[0][0].contents, width);
	this.thumbs = new Thumbnail[n][m];
	for(int i=0;i<n;i++)
	    for(int j=0;j<m;j++)
		this.thumbs[i][j] = new Thumbnail(games[i][j].image, games[i][j].point);

	this.thumbs[0][0].setFocused(true);

	this.addKeyListener(new KeyAdapter(){
		public void keyPressed(KeyEvent e){
		    Point to = new Point(pointer);
		    switch(e.getKeyCode()){
		    case VK_UP: to.y-=1; break;
		    case VK_DOWN: to.y+=1; break;
		    case VK_RIGHT: to.x+=1; break;
		    case VK_LEFT: to.x-=1; break;
		    case VK_ENTER:
			Launcher.this.window.hide();
			games[pointer.y][pointer.x].run();
			Launcher.this.window.show();
		    }
		    if(0 <= to.y && to.y < n && 0 <= to.x && to.x < m){
			cursor.move(games[to.y][to.x].point);
			title.setText(games[to.y][to.x].title);
			contents.setText(games[to.y][to.x].contents);
			thumbs[pointer.y][pointer.x].setFocused(false);
			thumbs[to.y][to.x].setFocused(true);
			pointer.setLocation(to);
		    }
		}
	    });

	this.timer = new Timer(1000/60, new ActionListener(){
		public void actionPerformed(ActionEvent e){
		    // Update
		    cursor.update();
		    title.update();
		    contents.update();
		    for(int i=0;i<n;i++) for(int j=0;j<m;j++) thumbs[i][j].update();
		    // Draw
		    repaint();
		}
	    });
	this.timer.start();

	// Shows launcher
	this.window.add(this);
	this.window.show();
    }

    public void paintComponent(Graphics g){
	Graphics2D g2 = (Graphics2D)g;

	// Background
	g2.setPaint(new GradientPaint(0, 0, new Color(0,0,0), 0, getHeight(), new Color(240,240,240)));
	g2.fill(new Rectangle2D.Double(0,0,getWidth(),getHeight()));

	// Draw sprites
	for(int i=0;i<n;i++) for(int j=0;j<m;j++) thumbs[i][j].draw(g2);
	cursor.draw(g2);
	title.draw(g2);
	contents.draw(g2);
    }
}

class MainWindow {

    private final JFrame frame;

    private final GraphicsEnvironment env;
    private final GraphicsDevice device;
    private final GraphicsConfiguration config;
    private final DisplayMode display;

    public final int width;
    public final int height;

    public MainWindow() {
	// FullScreen support
	this.env	    = GraphicsEnvironment.getLocalGraphicsEnvironment();
	this.device	    = env.getDefaultScreenDevice();
	this.config	    = device.getDefaultConfiguration();
	this.display	    = device.getDisplayMode();

	this.frame = new JFrame(config);
	this.frame.setUndecorated(true);
	this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	this.frame.getRootPane().setDoubleBuffered(true);

	this.width = display.getWidth();
	this.height = display.getHeight();
    }

    public void add(JComponent c){
	this.frame.add(c);
    }

    public void show(){
	this.device.setFullScreenWindow(frame);
    }

    public void hide(){
	this.device.setFullScreenWindow(null);
    }
}


class Config {
    public Game[][] games = null;
    public int n;
    public int m;

    private Config(String filename){
	try {
	    BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(filename), "UTF-8"));

	    // Skip comments
	    StringBuffer buf = new StringBuffer();
	    while(br.ready()){
		String str = br.readLine();
		if(!str.matches("(^\\s*#.*)|(^\\s*$)")) buf.append(str+"\n");
	    }
	    
	    Scanner sc = new Scanner(buf.toString());
	    sc.next("N"); this.n = sc.nextInt();
	    sc.next("M"); this.m = sc.nextInt();
	    this.games = new Game[n][m];
	    for(int i=0;i<n;i++){
		for(int j=0;j<m;j++){
		    sc.next("ID");    int id    = sc.nextInt();
		    sc.next("X");     int x     = sc.nextInt();
		    sc.next("Y");     int y     = sc.nextInt();
		    sc.next("Cmd");   String cmd   = sc.nextLine().trim();
		    sc.next("Title"); String title = sc.nextLine().trim();
		    sc.next("Image"); String file  = sc.nextLine().trim();
		    sc.next("Desc");  String desc  = sc.nextLine().trim();
		    games[i][j] = new Game(id, x, y, cmd, title, file, desc);
		}
	    }
	}catch(Exception e){
	    JOptionPane.showMessageDialog(null, "設定ファイルを読み込む際にエラーが発生しました。");
	    System.exit(1);
	}
    }

    public static Config loadConfig(String filename){
	return new Config(filename);
    }
}

class Game {
    public int id;
    public Point point;
    public String cmd;
    public BufferedImage image;
    public Shape title;
    public Shape contents;

    public Game(int id, int x, int y,
		String cmd, String title,
		String file, String desc) throws Exception {
	this.id = id;
	this.point = new Point(x,y);
	this.cmd = cmd;
	this.image = ImageIO.read(new File(file));

	Font font;
	FontRenderContext context;

	// Title
	font = new Font("Sans", Font.PLAIN, 150);
	context = new FontRenderContext(null,true,true);
	this.title = new TextLayout(title, font, context).getOutline(null);
	this.title = getRotateInstance(PI/2).createTransformedShape(this.title);
	this.title = getTranslateInstance(1300,50).createTransformedShape(this.title);

	// Contents
	font = new Font("serif", Font.PLAIN, 50);
	context = new FontRenderContext(null,true,true);
	this.contents = new TextLayout(desc, font, context).getOutline(null);
	this.contents = getTranslateInstance(600,800).createTransformedShape(this.contents);
    }

    public void run() {
	try{
	    Runtime.getRuntime().exec(cmd).waitFor();
	}catch(Exception e){
	    JOptionPane.showMessageDialog(null, "実行できませんでした");
	    e.printStackTrace();
	}
    }
}

class Cursor {
    private Point p;
    private Point q;
    public final int width = 400;
    public final int height = 300;
    public final int thickness = 5;

    public Cursor(Point p){
	this.p = new Point(p);
	this.q = new Point(p);
    }

    public void update(){
	if(p.x > q.x)	    p.x -= kernel(p.x-q.x);
	else if(p.x < q.x)  p.x += kernel(q.x-p.x);

	if(p.y > q.y)	    p.y -= kernel(p.y-q.y);
	else if(p.y < q.y)  p.y += kernel(q.y-p.y);
    }

    private int kernel(int d){
	if(d <= 1){
	    return d;
	}else if(d >= 45){
	    return (int)(pow(1.07,45)*1.5);
	}else {
	    return (int)(pow(1.07,d)*1.5);
	}
    }

    public void draw(Graphics2D g){
	g.setColor(WHITE);
	g.drawRect(p.x, p.y, width, thickness);
	g.drawRect(p.x, p.y, thickness, height);
	g.drawRect(p.x, p.y + height - thickness, width, thickness);
	g.drawRect(p.x + width - thickness, p.y, thickness, height);
    }

    public void move(Point p){
	this.q.setLocation(p);
    }
}

class Title {
    private Shape text;
    public Title(Shape text){
	setText(text);
    }
    public void update(){}
    public void draw(Graphics2D g){
	g.setColor(WHITE);
	g.fill(text);
    }
    public void setText(Shape text){
	this.text = text;
    }
}

class Contents {
    private Shape text;
    private float width;
    private float x;
    public Contents(Shape text, int width) {
	this.width = (float)width;
	setText(text);
    }
    public void update(){
	if(-text.getBounds().getWidth() > text.getBounds().getX()+x){
	    x = width;
	}else {
	    x -= 5f;
	}
    }
    public void draw(Graphics2D g){
	g.setColor(WHITE);
	g.fill(getTranslateInstance(x,0f).createTransformedShape(text));
    }
    public void setText(Shape text){
	this.text = text;
	this.x = 0;
    }
}

class Thumbnail {
    private Point point;
    private int dark;
    private int dest;
    private int width;
    private int height;
    private BufferedImage[] images;

    public final int step = 30;
    public final float dim = 0.7f;

    public Thumbnail(BufferedImage orig, Point point){
	this.point = new Point(point);
	this.width = orig.getWidth();
	this.height = orig.getHeight();

	// Prerendering
	this.images = new BufferedImage[step];
	for(int i=0;i<step;i++){
	    images[i] = new BufferedImage(width,height,orig.TYPE_INT_ARGB);
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
	if(flag)
	    this.dest = 0;
	else
	    this.dest = step-1;
    }

    public void update(){
	if(dark < dest)
	    dark = min(dark+1, step-1);
	else if(dark > dest)
            dark = max(dark-1, 0);
    }

    public void draw(Graphics2D g){
	g.drawImage(images[dark],point.x,point.y,null);
    }
}
