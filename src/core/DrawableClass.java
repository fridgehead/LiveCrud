package core;
import ddf.minim.analysis.FFT;


public abstract class DrawableClass  {
	
	public LiveCrud p;
	protected FFT fft;
	
	
	protected abstract void draw();
	//called by main loop, only call draw if p has been set
	
	
	protected abstract void onBeat();
	
	protected abstract void onHalfBeat();
	
	protected abstract void onQuarterBeat();
	
}
