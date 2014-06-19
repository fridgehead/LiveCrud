package core;
import ddf.minim.analysis.FFT;


public abstract class DrawableClass  {
	

	boolean ready = false;
	protected LiveCrud p;
	protected FFT fft;
	
	DrawableClass d;
	
	protected abstract void setup();
	

	//called by main loop, only call draw if p has been set
	public void preDraw(){
		if(d != null && p != null){
			d.draw();
		}
	}
	
	
	
	public void setPApplet(LiveCrud p){
		System.out.println("set p");
		this.p = p;
		this.fft = p.fft;
		if(d!=null){
			d.p = p;
		}
		ready = true;
		
	}
	
	protected abstract void draw();
	//called by main loop, only call draw if p has been set
	
	
	protected abstract void onBeat();
	
	protected abstract void onHalfBeat();
	
	protected abstract void onQuarterBeat();
	
}
