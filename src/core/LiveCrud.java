package core;


import java.awt.Cursor;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import compiler.FuckedSourceException;
import compiler.LiveCompiler;
import core.CodePanel.CompileState;
import ddf.minim.AudioInput;
import ddf.minim.Minim;
import ddf.minim.analysis.FFT;
import processing.core.PApplet;
import processing.core.PFont;

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

	//audio

	public Minim audio;
	public AudioInput in;
	public FFT fft;


	public void setup(){
		size(1280,1024, P3D);
		testComp = new LiveCompiler();
		for(int i = 0; i < cPanel.length; i++){
			cPanel[i] = new CodePanel(this, i);
		}

		audio = new Minim(this);
		in = audio.getLineIn();
		fft = new FFT( in.bufferSize(), in.sampleRate() );
		fft.linAverages( 200 );

		for(int i = 0; i < 5; i++){
			beatMarkers[i] = i * 1000;
		}
		refreshBeatDuration();
		font = loadFont("BitstreamVeraSansMono-Bold-48.vlw");
		hideCursor();
	}

	public void draw(){
		long t = millis();
		background(0);

		if(currentDisplay != null){
			currentDisplay.preDraw();
		}
		//resetMatrix();
		camera(width/2.0f, height/2.0f, (height/2.0f) / tan(PI*30.0f / 180.0f), width/2.0f, height/2.0f, 0.0f, 0.0f, 1.0f, 0.0f);
		hint(PApplet.ENABLE_DEPTH_TEST);
		try{
			cPanel[currentPanelIndex].draw();
		} catch (Exception e){
			
		}
		noLights();
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

			if(currentDisplay != null){
				currentDisplay.onBeat();
			}

		}
		if(beatHalfCounter - millis() < 0){
			beatHalfCounter = millis() + beatDelay / 2;
			rect(width-12,0, 8,20);

			if(currentDisplay != null){
				currentDisplay.onHalfBeat();
			}

		}
		if(beatQuarterCounter - millis() < 0){
			beatQuarterCounter = millis() + beatDelay / 4;
			rect(width-4,0, 8,20);

			if(currentDisplay != null){
				currentDisplay.onQuarterBeat();
			}

		}

		//now fft
		float centerFrequency = 0;
		float spectrumScale = 4;
		fft.forward(in.mix);

		textFont(font, 10);
		fill(250);
		for(int i = 0; i < fft.specSize(); i++)
		{
			if(i % 10 == 0){
				line(i * 2, height, i*2, height-50);
				text(i / 10, i * 2 + 5, height - 10);
			}
			stroke(255);
			rect(i * 2, height, 2, -fft.getBand(i) * 0.5f );

		}
		line(0, height - 50, width, height - 50);
		line(0, height - 25, width, height - 25);

		if(switchAtNextFrame && nextDisplay != null){
			currentDisplay = nextDisplay;
			switchAtNextFrame = false;
		}

		frameDeltaTime = (int) (millis() - t);
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
			cPanel[currentPanelIndex].keyPressed(k);
		}

	}

	public DrawableClass compile(ArrayList<StringBuffer> sb){
		StringBuilder s = new StringBuilder();
		for(StringBuffer l : sb){
			s.append(l.toString() + "\r\n");

		}
		DrawableClass ret = null;
		try{
			Object o = testComp.compile(s);
			if(o != null){
				ret = (DrawableClass)o;

			}
		} catch (FuckedSourceException e){
			System.out.println("r: " + e.row + "c: " + e.column);
			cPanel[currentPanelIndex].setErrorRow((int) e.row);
		}catch (Exception e){

			e.printStackTrace();
		}
		return ret;
	}

	public DrawableClass compileAndRun(ArrayList<StringBuffer> sb, int panelId){
		DrawableClass d = compile(sb);
		switchToDisplay(d, panelId);
		return d;
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
	
	/* some useful utilities */
	public void centre(){
		translate(width/2, height/2);
	}

	public static void main(String args[]) {
		System.out.println("Starting the fucking mess");
		PApplet.main(new String[] { "core.LiveCrud" });
	}


}


