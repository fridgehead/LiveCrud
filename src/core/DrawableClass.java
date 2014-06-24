package core;
import damkjer.ocd.Camera;
import ddf.minim.analysis.FFT;


public abstract class DrawableClass {
	boolean ready = false;
	protected LiveCrud p;
	protected FFT fft;
	protected Camera camera;
	
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
