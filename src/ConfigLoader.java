import java.awt.image.*;
import java.io.*;
import javax.imageio.*;
import javax.swing.*;

import selector.objects.*;

public class ConfigLoader {

    public final int w = 3;
    public int h;
    public Game[][] games;
    public static final String path = "data";
    private static ConfigLoader loader = null;

    private ConfigLoader(){
	this.h = 0;
	this.games = new Game[0][0];
	try{
	    File dir = new File(path);
	    File[] gamedirs = dir.listFiles(new FileFilter(){
		    public boolean accept(File pathname){
			return !(pathname.getName().startsWith("."));
		    }
		});

	    this.h = (gamedirs.length+w-1)/w;
	    this.games = new Game[w][h];

	    for(int i=0;i<gamedirs.length;i++){
		System.out.println("ConfigLoader: Processing...: "+gamedirs[i]);
		File parent = gamedirs[i];
		String title = parent.getName();
		BufferedImage image
		    = ImageIO.read(new File(parent, "thumb.jpg"));
		String cmd = (new File(parent, "run.bat")).getAbsolutePath();

		this.games[i%w][i/w] = new Game(title, image, cmd);
	    }
	}catch(Exception e){
	    JOptionPane.showMessageDialog(null, "設定の読み込みに失敗しました");
	    e.printStackTrace();
	    System.exit(1);
	}
    }

    public static ConfigLoader load(){
	if(loader == null)
	    loader = new ConfigLoader();
	return loader;
    }
}