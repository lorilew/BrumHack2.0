package gui;

import java.awt.GridBagLayout;
import java.awt.Image;

import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.swing.JFrame;

public class start{
	static Image background;
	static String startScreen = "StartScreen.JPG";
	
	public static void main(String[] args) {
		
		try{
			File audioFile = new File("soundtrack.wav");
		    AudioInputStream audioInputStream =  AudioSystem.getAudioInputStream(audioFile);
		    Clip clip = AudioSystem.getClip();
		    clip.open(audioInputStream);
		    clip.start();
		}
		catch(Exception ex)	{
			ex.printStackTrace();
		}
		
		try {
			background = ImageIO.read(new File(startScreen));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		JFrame frame = new JFrame("Mutant ZOmBiE Bunnies VS. USA!");
		frame.setBounds(100,100, 1000, 800);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(new ImagePanel(frame, background,new GridBagLayout()));
		frame.setVisible(true);
	}
	
}

