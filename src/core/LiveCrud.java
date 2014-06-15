package core;


import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import compiler.LiveCompiler;
import processing.core.PApplet;

public class LiveCrud extends PApplet implements KeyListener{
	LiveCompiler testComp;
	DrawableClass d;
	
	CodePanel cPanel;
	
	public void setup(){
		size(1024,768, P3D);
		testComp = new LiveCompiler();
		cPanel = new CodePanel(this);
	}

	public void draw(){
		background(0);
		
		if(d != null){
			d.preDraw();
		}
		//resetMatrix();
		camera(width/2.0f, height/2.0f, (height/2.0f) / tan(PI*30.0f / 180.0f), width/2.0f, height/2.0f, 0.0f, 0.0f, 1.0f, 0.0f);
		
		cPanel.draw();
		
		
	}
	
	@Override
	public void keyPressed(KeyEvent k){
		cPanel.keyPressed(k);	
	}
	
	public void compile(ArrayList<StringBuffer> sb){
		StringBuilder s = new StringBuilder();
		for(StringBuffer l : sb){
			s.append(l.toString());
			
		}
		try{
        	Object o = testComp.compile(s);
        	if(o != null){
        		DrawableClass c = (DrawableClass)o;
        		c.setPApplet(this);
        		d = c;
        	}
        }catch (Exception e){
        	e.printStackTrace();
        }
	}
	
	public void mouseClicked(){
		
		
	}
	
	public static void main(String args[]) {
		System.out.println("Starting the fucking mess");
		PApplet.main(new String[] { "core.LiveCrud" });
	}
	

}


