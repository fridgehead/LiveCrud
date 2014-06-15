package core;
import ddf.minim.analysis.FFT;


public abstract class DrawableClass {
	boolean ready = false;
	protected LiveCrud p;
	protected FFT fft;
	
	protected abstract void draw();
	//called by main loop, only call draw if p has been set
	public void preDraw(){
		if(ready){
			draw();
		}
	}
	
	protected abstract void setup();
	
	protected void onBeat(){
		
	}
	
	public void setPApplet(LiveCrud p){
		System.out.println("set p");
		this.p = p;
		this.fft = p.fft;
		ready = true;
		setup();
	}
	
}
