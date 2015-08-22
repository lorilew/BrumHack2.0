package gui;

import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

import javax.swing.JButton;
import javax.swing.JPanel;

import data.StateMap;

public class ImagePanel extends JPanel {

	private Image img;
	private Frame frame;
	public ImagePanel(LayoutManager layout) {
		super(layout);

		this.addMouseListener(new MouseListener() {

			@Override
			public void mouseClicked(MouseEvent e) {

			}

			@Override
			public void mousePressed(MouseEvent e) {}

			@Override
			public void mouseReleased(MouseEvent e) {
				StateMap.setup();
				frame.dispose();
			}

			@Override
			public void mouseEntered(MouseEvent e) {}

			@Override
			public void mouseExited(MouseEvent e) {}

		});
	}

	public ImagePanel(Frame frame, Image img, LayoutManager layout) {

		this(layout);
		this.frame = frame;
		this.img = img;
	}

	public Image getImage() {
		return img;
	}

	public void setImage(Image value) {
		if (img != value) {
			Image old = img;
			img = value;
			firePropertyChange("image", old, img);
			revalidate();
		}
	}

	@Override
	public Dimension getPreferredSize() {
		Image img = getImage();
		return img == null ? super.getPreferredSize() : new Dimension(img.getWidth(this), img.getHeight(this));
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		if (img != null) {
			g.drawImage(img, 0, 0, this);
		}
	}

}