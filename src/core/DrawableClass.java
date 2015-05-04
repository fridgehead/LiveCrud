package core;
import damkjer.ocd.Camera;
import ddf.minim.analysis.FFT;
import de.voidplus.leapmotion.LeapMotion;
import processing.core.PApplet;
import processing.opengl.*;


public abstract class DrawableClass extends DrawableBase{
	boolean ready = false;
	protected Camera camera;
	

	protected abstract void draw();
	//called by main loop, only call draw if p has been set
	public void preDraw(){
		if(ready){
			draw();
			p.colorMode(PApplet.RGB);
		}
		
	}
	
	protected abstract void setup();
	
	protected void onBeat(){
		
	}
	protected void onHalfBeat(){
		
	}
	protected void onQuarterBeat(){
	
	}
	
	public void setPApplet(LiveCrud p){
		System.out.println("set p");
		this.p = p;
		this.fft = p.fft;
		this.camera = new Camera(p);
		ready = true;
		setup();
	}
	protected void numpadKey(int i) {
		// TODO Auto-generated method stub
		
	}
	
}
