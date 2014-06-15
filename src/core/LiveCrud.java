package core;


import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;

import compiler.LiveCompiler;
import core.CodePanel.CompileState;
import processing.core.PApplet;

public class LiveCrud extends PApplet implements KeyListener{
	LiveCompiler testComp;
	DrawableClass currentDisplay, nextDisplay;
	boolean switchAtNextFrame = false;
	
	CodePanel[] cPanel = new CodePanel[5];
	int currentPanelIndex = 0;
	private int currentDisplayIndex;

	int beatDelay = 1000;
	int beatCounter = 1000;
	int beatStartTime = 0;
	int[] beatMarkers = new int[5];
	int beatPtr = 0;
	boolean beatDone = false;	
	
	int frameDeltaTime = 0;
	
	public void setup(){
		size(1024,768, P3D);
		testComp = new LiveCompiler();
		for(int i = 0; i < cPanel.length; i++){
			cPanel[i] = new CodePanel(this, i);
		}
		for(int i = 0; i < 5; i++){
			beatMarkers[i] = i * 1000;
		}
		refreshBeatDuration();
	}

	public void draw(){
		long t = millis();
		background(0);
		
		if(currentDisplay != null){
			currentDisplay.preDraw();
		}
		//resetMatrix();
		camera(width/2.0f, height/2.0f, (height/2.0f) / tan(PI*30.0f / 180.0f), width/2.0f, height/2.0f, 0.0f, 0.0f, 1.0f, 0.0f);
		
		cPanel[currentPanelIndex].draw();
		int ct = 0;
		noStroke();
		for(CodePanel c : cPanel){
			if(c.state == CompileState.STATE_COMPILED){
				fill(0,255,0);
				
			} else if (c.state == CompileState.STATE_DIRTY){
				fill(255,255,0);
				
			} else {
				fill(0,255,0);
			}
			rect(ct * 10, 0, 20,20);
			if(ct == currentPanelIndex){
				rect(ct*10,20,5,5);
			}
			if(ct == currentDisplayIndex){
				rect(ct*10 + 5, 20, 5,10);
			}
			ct++;
		}
		
		//draw the beat marker
		
		
		if(millis() > beatCounter){
			beatCounter = millis() + beatDelay;
			rect(0, 700, 70,70);
		//	System.out.println("beatCounter : " + beatCounter + " - " + millis());
			if(currentDisplay != null){
				currentDisplay.onBeat();
			}
			
			
		}
		
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
		System.out.println("new beat duration: " + beatDelay);
		
	}

	public static void main(String args[]) {
		System.out.println("Starting the fucking mess");
		PApplet.main(new String[] { "core.LiveCrud" });
	}
	

}


