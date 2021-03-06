package core;
import de.voidplus.leapmotion.*;

import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

import compiler.FuckedSourceException;
import compiler.LiveCompiler;
import core.CodePanel.CodeLine;
import core.CodePanel.CompileState;
import ddf.minim.AudioInput;
import ddf.minim.Minim;
import ddf.minim.analysis.BeatDetect;
import ddf.minim.analysis.FFT;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PImage;
import processing.core.PVector;

public class LiveCrud extends PApplet implements KeyListener{
	LiveCompiler testComp;
	DrawableClass currentDisplay, nextDisplay;
	boolean switchAtNextFrame = false;

	CodePanel[] cPanel = new CodePanel[5];
	
	int currentPanelIndex = 0;
	private int currentDisplayIndex;

	int beatDelay = 1000;
	int beatCounter = 1000;
	int beatHalfCounter = 500;
	int beatQuarterCounter = 250;
	int beatStartTime = 0;
	int[] beatMarkers = new int[5];
	int beatPtr = 0;	
	boolean beatDone = false;	

	int frameDeltaTime = 0;

	PFont font;
	
	HashMap<String, PImage> imageCache = new HashMap<String, PImage>();

	//audio

	public Minim audio;
	public AudioInput in;
	public FFT fft;
	public BeatDetect beatDetect;

	public LeapMotion leap;
	
	
	boolean argsRead = false;

	public void setup(){
		int sx = 400;
		int sy = 400;
		if(argsList != null && argsRead == false){
			for (String s : argsList){
				System.out.println(s);
				if(s.indexOf("width") != -1){
					String[] p = s.split("=");
					sx = Integer.parseInt(p[1]);
					
				} else if(s.indexOf("height") != -1){
					String[] p = s.split("=");
					sy = Integer.parseInt(p[1]);
					
				}
			}
			argsRead = true;
		}
		
		size(1024,768, P3D);
		frame.setResizable(true);
		frame.setSize(sx,sy);
		testComp = new LiveCompiler();
		for(int i = 0; i < cPanel.length; i++){
			cPanel[i] = new VimCodePanel(this, i);
		}

		audio = new Minim(this);
		in = audio.getLineIn();
		fft = new FFT( in.bufferSize(), in.sampleRate() );
		fft.linAverages( 1 );
		
		beatDetect = new BeatDetect();

		for(int i = 0; i < 5; i++){
			beatMarkers[i] = i * 1000;
		}
		refreshBeatDuration();
		font = loadFont("BitstreamVeraSansMono-Bold-48.vlw");
		hideCursor();
		
		leap = new LeapMotion(this);
		if(leap == null){
			System.out.println("no leap mption detected");
		}
	}

	public void draw(){
		long t = millis();
		background(0);
		hint(PApplet.ENABLE_DEPTH_TEST);

		//draw the currently running code
		if(currentDisplay != null){
			try{
				pushStyle();
				pushMatrix();
				currentDisplay.preDraw();
				popStyle();
				popMatrix();
			} catch (Exception e){}
		}
		//resetMatrix();
		camera(width/2.0f, height/2.0f, (height/2.0f) / tan(PI*30.0f / 180.0f), width/2.0f, height/2.0f, 0.0f, 0.0f, 1.0f, 0.0f);
		hint(PApplet.ENABLE_DEPTH_TEST);
		noTint();
		noLights();
		
		cPanel[currentPanelIndex].draw();
		
		
		int ct = 0;
		noStroke();
		for(CodePanel c : cPanel){
			if(c.state == CompileState.STATE_COMPILED){
				fill(0,255,0);

			} else if (c.state == CompileState.STATE_DIRTY){
				fill(255,255,0);

			} else {
				fill(255,0,0);
			}
			rect(ct * 15, 0, 10,10);
			if(ct == currentPanelIndex){
				rect(ct*15,12,5,5);
			}
			if(ct == currentDisplayIndex){
				rect(ct*15 + 5, 12, 5,10);
			}
			ct++;

		}

		//draw the beat marker
		strokeWeight(1);
		for(int i = 0; i < beatPtr; i++){
			rect(width - (i + 1) * 7 - 20, 0, 5,5);

		}
		if(beatCounter - millis() < 0){
			beatCounter = millis() + beatDelay;
			rect(width-20,0, 8,20);
			try { 
				if(currentDisplay != null){
					currentDisplay.onBeat();
				}
			} catch (Exception e){}

		}
		if(beatHalfCounter - millis() < 0){
			beatHalfCounter = millis() + beatDelay / 2;
			rect(width-12,0, 8,20);
			try{
				if(currentDisplay != null){
					currentDisplay.onHalfBeat();
				}
			} catch (Exception e){}
			

		}
		if(beatQuarterCounter - millis() < 0){
			beatQuarterCounter = millis() + beatDelay / 4;
			rect(width-4,0, 8,20);
			try{
				if(currentDisplay != null){
					currentDisplay.onQuarterBeat();
				}
			} catch (Exception e){}

		}

		//beat detection
		beatDetect.detect(in.mix);
		if(beatDetect.isOnset() && currentDisplay != null){
	//		currentDisplay.onBeat();
		}
		
		//now fft
		float centerFrequency = 0;
		float spectrumScale = 4;
		fft.forward(in.mix);

		textFont(font, 10);
		fill(250,250,250,100);
		noStroke();
		for(int i = 0; i < fft.specSize() / 5; i++)
			
		{
			if(i % 5 == 0){
				stroke(250,250,250, 128);
				line(i * 10, height, i*10, height-50);
				text(i / 5, i * 10 + 25, height - 60);
			}
			//stroke(255);
			noStroke();
			rect(i * 10, height, 10, -fft.getBand(i) * 0.5f );

		}
		stroke(250,250,250,128);
		line(0, height - 50, width, height - 50);
		line(0, height - 25, width, height - 25);

		//mouse markers
		fill(128,128,0);
		noStroke();
		rect(2, mouseY - 20, 5,40);
		text(mouseY, 15, mouseY);
		rect(mouseX - 20, 960, 40, 5);
		pushMatrix("mouse");
		translate(mouseX, 960);
		rotate(radians(-45));
		text(mouseY, 5,0);
		popMatrix("mouse");
		
		
		if(switchAtNextFrame && nextDisplay != null){
			currentDisplay = nextDisplay;
			switchAtNextFrame = false;
		}

		frameDeltaTime = (int) (millis() - t);
		
		
//		this.addComponentListener( new ComponentAdapter(){
//			public void componentResized(ComponentEvent e){
//				
//				for(CodePanel c : cPanel){
//					c.resize();
//				}
//				System.out.println("resized to " + width + " " + height);
//			}
//			
//			
//		});
	}
	
	public void pushMatrix(){
		super.pushMatrix();
	}
	public void pushMatrix(String tag){
		super.pushMatrix();
	}
	
	public void popMatrix(){
		super.popMatrix();
	}
	public void popMatrix(String tag){
		super.popMatrix();
	}
	
	@Override
	public void keyPressed(KeyEvent k){
		if(k.getModifiers() == 2){		//CTRL mod
			int c = k.getKeyChar();
			System.out.println("c: " + c);
			if(c >= 49 && c <= 54){
				currentPanelIndex = (int)c - 49;
			} else if (c == 32){
				beatMarkers[beatPtr] = millis();
				beatPtr++;
				if(beatPtr >= 5){
					beatPtr = 0;
					refreshBeatDuration();
				} else if (beatPtr == 0){
					beatStartTime = millis();
				}
			} else {
				cPanel[currentPanelIndex].keyPressed(k);
			}
		} else {
			int c = k.getKeyCode();
			if(c >= 96 && c <= 105){	
				//keypad, pass this into the currently running script
				if(currentDisplay !=null){
					currentDisplay.numpadKey(c - 96);
				}
			} else {
				cPanel[currentPanelIndex].keyPressed(k);
			}
		}

	}


	public DrawableClass compile(ArrayList<CodeLine> sb){
		StringBuilder s = new StringBuilder();
		for(CodeLine l : sb){
			
			s.append(l.toString() + "\r\n");

		}
		
		DrawableClass ret = null;
		try{
			Object o = testComp.compile(s);
			if(o != null){
				ret = (DrawableClass)o;
				

			}
		} catch (FuckedSourceException e){
			System.out.println(e.toString());
			cPanel[currentPanelIndex].setErrorPos((int) e.row, (int)e.column);
			e.printStackTrace();
		}catch (Exception e){

			e.printStackTrace();
		}
		return ret;
	}


	public DrawableClass compileAndRun(ArrayList<CodeLine> sb, int panelId){
		DrawableClass d = compile(sb);

		switchToDisplay(d, panelId);
		return d;
	}
	
	public void enableDisplay(DrawableClass d, int id){
		
	}
	
	public void disableDisplay(DrawableClass d, int id){
		
	}

	public void switchToDisplay(DrawableClass d, int id){
		if(d != null){
			d.setPApplet(this);
			nextDisplay = d;
			switchAtNextFrame = true;
			currentDisplayIndex = id;
		}
	}

	void hideCursor() {
		BufferedImage cursorImg = new BufferedImage(16, 16, BufferedImage.TYPE_INT_ARGB);
		Cursor blankCursor = Toolkit.getDefaultToolkit().createCustomCursor(
				cursorImg, new Point(0, 0), "blank cursor");
		frame.setCursor(blankCursor);
	}

	public void mouseClicked(){


	}

	private void refreshBeatDuration() {
		float avgDur = 0.0f;
		for(int i = 1; i < 5; i++){
			avgDur += beatMarkers[i] - beatMarkers[i-1];
		}
		avgDur /= 4.0f;
		beatDelay = (int)avgDur;
		beatCounter = millis() + beatDelay;
		beatHalfCounter = millis() + beatDelay /2;
		beatQuarterCounter = millis() + beatDelay /4;
		
		System.out.println("new beat duration: " + beatDelay);

	}
	@Override
	public PImage loadImage(String path){
		 
			PImage p = imageCache.get(path);
			if(p != null){
				System.out.println("cache hit");

				
				return p;
			}else {
		
			System.out.println("Cahce miss");
			 p = super.loadImage(path);
			imageCache.put(path, p);
			return p;
		}
		
	}
	
	
	/* some useful utilities */
	public void centre(){
		translate(width/2, height/2);
	}
	
	public void line(PVector p1, PVector p2){
		line(p1.x, p1.y, p1.z, p2.x, p2.y, p2.z);
	}
	public void line(PVector p1, PVector p2, PVector p3){
		line(p1.x, p1.y, p3.x, p2.x, p2.y, p3.z);
	}
	public void bezier(PVector p1, PVector p2, PVector p3, PVector p4){
		bezier( p1.x, p1.y, p1.z,
				p2.x, p2.y, p2.z,
				p3.x, p3.y, p3.z,
				p4.x, p4.y, p4.z);
		
	}
	public void translate(PVector p){
		translate(p.x, p.y, p.z);
	}
	
	
	public static String[] argsList;
	public static void main(String args[]) {
		System.out.println("Starting the fucking mess");
		PApplet.main(new String[] {"core.LiveCrud" });
		argsList = args;
	}


}


