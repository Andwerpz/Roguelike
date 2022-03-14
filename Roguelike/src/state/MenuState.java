package state;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.image.BufferedImage;

import input.InputManager;
import input.Button;
import input.SliderButton;
import input.TextField;
import input.ToggleButton;
import map.Map;
import util.ScrollWindow;
import util.TextBox;

public class MenuState extends State{
	
	InputManager im;
	
	Map map;

	public MenuState(GameManager gsm) {
		super(gsm);
		
		im = new InputManager();
		
		map = new Map();
		
	}

	@Override
	public void init() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void tick(Point mouse) {

		im.tick(mouse);
		
	}

	@Override
	public void draw(Graphics g) {
		
		im.draw(g);
		
		//map.draw(g, 0, 0);
		
	}

	@Override
	public void keyPressed(KeyEvent arg0) {
		im.keyPressed(arg0);
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		im.keyReleased(arg0);
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
		im.keyTyped(arg0);
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {

		String which = im.mouseClicked(arg0);
		
		if(which == null) {
			return;
		}
		
		switch(which) {
		case "button1":
			im.setVal("slider1", 50);
			im.setVal("slider2", 75);
			break;
		}
		
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		im.mousePressed(arg0);
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		im.mouseReleased(arg0);
	}

	@Override
	public void mouseWheelMoved(MouseWheelEvent arg0) {
	}

}