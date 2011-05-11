import javax.swing.*;

public class Main {
    public static void main(String[] args){
	try{
	    UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
	    new Launcher();
	}catch(Exception e){
	    e.printStackTrace();
	}
    }
}



