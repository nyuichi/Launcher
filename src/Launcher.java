import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import static java.awt.Color.*;
import static java.awt.event.KeyEvent.*;

import selector.*;

class Launcher extends JPanel implements ActionListener, KeyListener {

    private final ConfigLoader conf;
    private final Selector selector;
    private final Timer timer;
    private final JFrame frame;

    private final GraphicsEnvironment env;
    private final GraphicsDevice device;
    private final GraphicsConfiguration config;
    private final DisplayMode display;

    public final int width;
    public final int height;

    public static final int fps = 60;

    public Launcher() {
	super(true);
	this.setFocusable(true);

	// Fullscreen support
	this.env     = GraphicsEnvironment.getLocalGraphicsEnvironment();
	this.device  = env.getDefaultScreenDevice();
	this.config  = device.getDefaultConfiguration();
	this.display = device.getDisplayMode();
	this.width   = display.getWidth();
	this.height  = display.getHeight();

	// Frame
	this.frame = new JFrame(config);
	this.frame.setUndecorated(true);
	this.frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	this.frame.getRootPane().setDoubleBuffered(true);
	this.frame.add(this);

	// Selector
	this.conf = ConfigLoader.load();
	this.selector = new MaskedSelector(conf.w, conf.h, conf.games, height);
	this.addKeyListener(this.selector);
	this.addKeyListener(this);

	// Timer
	this.timer = new Timer(1000/fps, this);

	// Run
	this.timer.start();
	this.device.setFullScreenWindow(frame);
	this.frame.setVisible(true);
    }

    // Timer
    public void actionPerformed(ActionEvent e){
	this.selector.update();
	this.repaint();
    }

    // Key events
    public void keyReleased(KeyEvent e){}
    public void keyTyped(KeyEvent e){}
    public void keyPressed(KeyEvent e){
	int c = e.getKeyCode();
	if(c == VK_ENTER){	// Execute
	    this.device.setFullScreenWindow(null);
	    String cmd = this.selector.games[selector.p.x][selector.p.y].cmd;
	    try{
		Runtime.getRuntime().exec(cmd).waitFor();
	    }catch(Exception ex){
		JOptionPane.showMessageDialog(null, "実行できませんでした");
		ex.printStackTrace();
	    }
	    this.device.setFullScreenWindow(frame);
	}else if(c == VK_ESCAPE){ // Exit
	    System.exit(0);
	}
    }

    protected void paintComponent(Graphics g){
	g.setColor(WHITE);
	g.fillRect(0,0,width,height);

	Image i = this.selector.getImage();
	g.drawImage(i, (width-i.getWidth(null))/2, 0, this);

	// Credit
	g.drawString("Powerd by Wasabiz", width-220, height-30);
    }
}
