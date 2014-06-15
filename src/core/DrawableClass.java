package core;
import processing.core.PApplet;


public abstract class DrawableClass {
	boolean ready = false;
	protected PApplet p;
	protected abstract void draw();
	//called by main loop, only call draw if p has been set
	public void preDraw(){
		if(ready){
			draw();
		}
	}
	
	protected abstract void setup();
	
	public void setPApplet(PApplet p){
		System.out.println("set p");
		this.p = p;
		ready = true;
		setup();
	}
	
}
