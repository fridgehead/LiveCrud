package core;

import java.awt.event.KeyEvent;
import java.nio.CharBuffer;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;

public class CodePanel {
	LiveCrud parent;
	ArrayList<StringBuffer> lines = new ArrayList<StringBuffer>();
	int cursorX = 0;
	int cursorY = 0;
	int charWidth = 0;
	
	

	/* scaling stuff */
	int codeHeight = 0;
	float maxWidth = 0;
	float targetScaleX, targetScaleY;
	private float currentScaleX;
	private float currentScaley;
	
	DrawableClass compileResult;
	
	public CompileState state = CompileState.STATE_DIRTY;
	
	PFont font;
	Object lock = new Object();
	PGraphics pGfx;
	private int panelId;
	

	public CodePanel(LiveCrud parent, int panelId){
		this.parent = parent;
		this.panelId = panelId;
		pGfx = parent.createGraphics(parent.width, parent.height, PApplet.P2D);
		lines.add(new StringBuffer("protected void setup(){"));
		lines.add(new StringBuffer(" "));
		lines.add(new StringBuffer("}"));

		lines.add(new StringBuffer("protected void draw(){"));
		lines.add(new StringBuffer(" "));
		lines.add(new StringBuffer("}"));


		font = parent.loadFont("BitstreamVeraSansMono-Roman-48.vlw");
		parent.textFont(font, 15);
		//ZOMG ANOTHER HACK
		charWidth = (int)parent.textWidth(" ");
		targetScaleX = 1.0f;
		targetScaleY = 1.0f;
		currentScaleX = 1.0f;
		currentScaley = 1.0f;
	}

	public void draw(){
		pGfx.beginDraw();
		pGfx.background(0,0,0,0);
		pGfx.fill(255);
		pGfx.noStroke();
		pGfx.textFont(font, 15);
		pGfx.pushMatrix();
		float tY = pGfx.height / (float)codeHeight;
		float tX = pGfx.width / (maxWidth + 50);
		
		if(tX > tY){
			pGfx.scale(tY);
			
		} else {
			pGfx.scale(tX);
		}
		int ct = 40;
		maxWidth = 0;
		//tint the text slightly red if the panel needs compiling
		if(state == CompileState.STATE_DIRTY){
			pGfx.fill(255, 200,200);
			
		} else {
			pGfx.fill(255);
		}
		synchronized(lock){
			for(StringBuffer s : lines){
				String st = s.toString();
				float w = pGfx.textWidth(st);
				if(w > maxWidth){
					maxWidth = w;
				}
				pGfx.fill(0,0,0,80);
				pGfx.rect(10, ct - 14, w + 15, 20);
				pGfx.fill(255);
				pGfx.text(st, 20, ct);
				ct += 20;
			}
			codeHeight = ct+ 20;
		}
		pGfx.fill(255,255,0,200);
		pGfx.noStroke();
		pGfx.rect(cursorX * charWidth + 20, cursorY * 20 + 23, charWidth, 20);
		pGfx.popMatrix();
		pGfx.endDraw();
		
		parent.hint(PApplet.DISABLE_DEPTH_TEST);
		parent.image(pGfx, 0, 0);

	}

	public void keyPressed(KeyEvent k){

		int cCode = k.getKeyCode();
		//System.out.println("c: " + cCode);
		if(cCode == KeyEvent.VK_UP){		//up
			moveUp();

		} else if (cCode == KeyEvent.VK_RIGHT){	//right
			moveRight(k.getModifiers());
		} else if (cCode == KeyEvent.VK_DOWN){ 	//down
			moveDown();
		} else if (cCode == KeyEvent.VK_LEFT){
			moveLeft(k.getModifiers());
		} else if (cCode == KeyEvent.VK_BACK_SPACE){
			if(k.getModifiers() == 1){		//shift back to delete line
				deleteLine();
			} else if (k.getModifiers() == 2){	//ctrl backspace to delete from cursorX back to 0
				deleteToStartOfLine();
			} else {
				deleteChar();
			}
		} else if (cCode == KeyEvent.VK_ENTER){
			keyboardEnter(k);
		} else if (cCode == KeyEvent.VK_TAB){
			newTab();
		} else {
			char c = k.getKeyChar();
			if(isPrintableChar(c)){
				charTyped(k.getKeyChar());
			}
		}
	}

	private void deleteToStartOfLine() {
		if(cursorX > 0){
			StringBuffer s = lines.get(cursorY);
			s.delete(0, cursorX);
			cursorX = 0;
		}
	}

	private void keyboardEnter(KeyEvent k) {
		if(k.getModifiers() == 2){			//ctrl+enter compiles and runs this panel
											//unless the comple result isnt null and page isnt dirty
			if(compileResult != null && state == CompileState.STATE_COMPILED){
				//run the previously compiled object
				parent.switchToDisplay(compileResult, panelId);
			} else {
				DrawableClass res = parent.compileAndRun(lines,panelId);
				if(res != null){
					state = CompileState.STATE_COMPILED;
					compileResult = res;
				} else {
					state = CompileState.STATE_ERROR;
					
				}
			}
		} else if (k.getModifiers() == 1){ 	//shift + enter compiles but does not run the panel
			DrawableClass res = parent.compile(lines);
			if(res != null){
				state = CompileState.STATE_COMPILED;
				compileResult = res;
			} else {
				state = CompileState.STATE_ERROR;
			}
		} else {
			newLine();
		}
	}

	/* trollololol*/
	private void newTab() {
		charTyped(' ');
		charTyped(' ');
		charTyped(' ');

		state = CompileState.STATE_DIRTY;
	}

	private void newLine() {
		StringBuffer l = lines.get(cursorY);
		String end = l.substring(cursorX);
		synchronized (lock) {


			l.delete(cursorX, l.length());

			lines.add(cursorY + 1, new StringBuffer(end));
		}
		moveDown();
		cursorX = 0;
		state = CompileState.STATE_DIRTY;

	}

	private void moveUp() {
		cursorY -= 1;
		if (cursorY < 0){
			cursorY = lines.size() - 1;
		}
		int lineLen = lines.get(cursorY).length();
		if(cursorX >= lineLen){
			cursorX = lineLen-1;
			if(cursorX < 0){
				cursorX = 0;
			}
		}
	}

	private void moveRight(int mod) {
		if(mod == 2){
			cursorX = lines.get(cursorY).length();
		} else {
			cursorX++;
			if(cursorX > lines.get(cursorY).length()){
				cursorX = 0;
			}
		}
	}

	private void moveDown() {
		cursorY ++;
		if (cursorY >= lines.size()){
			cursorY = 0;
		}
		int lineLen = lines.get(cursorY).length();
		if(cursorX >= lineLen){
			cursorX = lineLen-1;
			if(cursorX < 0){
				cursorX = 0;
			}
		}
	}

	private void moveLeft(int mod) {
		if(mod == 2){
			cursorX = 0;
		} else {
			cursorX --;
			if (cursorX < 0){
				cursorX = lines.get(cursorY).length() -1;				
			}
		}
	}
	private void deleteChar() {
		synchronized (lock) {


			StringBuffer l = lines.get(cursorY);
			if(cursorX > 0){


				l.deleteCharAt(cursorX-1);
				cursorX--;
			} else {
				//backspace to end of prev line
			}
		}
		state = CompileState.STATE_DIRTY;
	}

	private void deleteLine(){
		synchronized (lock) {


			if(lines.size() == 1){
				StringBuffer l = lines.get(0);
				l = new StringBuffer(" ");
				cursorX = 0;
				cursorY = 0;

			} else {
				lines.remove(cursorY);
				cursorX = 0;
				if(cursorY >= lines.size()){
					cursorY = lines.size() - 1;
					
				}
			}
		}
		state = CompileState.STATE_DIRTY;
	}

	public boolean isPrintableChar( char c ) {
		Character.UnicodeBlock block = Character.UnicodeBlock.of( c );
		return (!Character.isISOControl(c)) &&
				c != KeyEvent.CHAR_UNDEFINED &&
				block != null &&
				block != Character.UnicodeBlock.SPECIALS;
	}

	private void charTyped(int cCode) {
		//get current line
		synchronized(lock){
			StringBuffer l = lines.get(cursorY);
			l.insert(cursorX, (char)cCode);
			cursorX++;
			state = CompileState.STATE_DIRTY;
			
		}

	}
	
	public enum CompileState {
		STATE_COMPILED, STATE_DIRTY, STATE_ERROR
	}
}
