package core;

import java.awt.event.KeyEvent;
import java.nio.CharBuffer;
import java.util.ArrayList;

import processing.core.PApplet;
import processing.core.PFont;

public class CodePanel {
	LiveCrud parent;
	ArrayList<StringBuffer> lines = new ArrayList<StringBuffer>();
	int cursorX = 0;
	int cursorY = 0;
	int charWidth = 0;

	int codeHeight = 0;
	float maxWidth = 0;


	PFont font;
	Object lock = new Object();

	public CodePanel(LiveCrud parent){
		this.parent = parent;

		lines.add(new StringBuffer("protected void setup(){"));
		lines.add(new StringBuffer("System.out.println(\"setup\");"));
		lines.add(new StringBuffer("}"));

		lines.add(new StringBuffer("protected void draw(){"));
		lines.add(new StringBuffer(" "));
		lines.add(new StringBuffer("}"));


		font = parent.loadFont("BitstreamVeraSansMono-Roman-48.vlw");
		parent.textFont(font, 15);
		//ZOMG ANOTHER HACK
		charWidth = (int)parent.textWidth(" ");
	}

	public void draw(){
		parent.fill(255);
		parent.textFont(font, 15);
		parent.pushMatrix();
		float scaleFactorY = parent.height / (float)codeHeight;
		float scaleFactorX = parent.width / maxWidth;
		parent.scale(scaleFactorY);

		int ct = 40;
		maxWidth = 0;
		synchronized(lock){
			for(StringBuffer s : lines){
				String st = s.toString();
				float w = parent.textWidth(st);
				if(w > maxWidth){
					maxWidth = w;
				}
				parent.text(st, 20, ct);
				ct += 20;
			}
			codeHeight = ct+ 20;
		}
		parent.fill(255,255,0,200);
		parent.rect(cursorX * charWidth + 20, cursorY * 20 + 23, charWidth, 20);
		parent.popMatrix();

	}

	public void keyPressed(KeyEvent k){

		int cCode = k.getKeyCode();
		//System.out.println("c: " + cCode);
		if(cCode == KeyEvent.VK_UP){		//up
			moveUp();

		} else if (cCode == KeyEvent.VK_RIGHT){	//right
			moveRight();
		} else if (cCode == KeyEvent.VK_DOWN){ 	//down
			moveDown();
		} else if (cCode == KeyEvent.VK_LEFT){
			moveLeft();
		} else if (cCode == KeyEvent.VK_BACK_SPACE){
			if(k.getModifiers() == 2){
				deleteLine();
			} else {
				deleteChar();
			}
		} else if (cCode == KeyEvent.VK_ENTER){
			if(k.getModifiers() == 2){
				parent.compile(lines);
			} else {
				newLine();
			}
		} else if (cCode == KeyEvent.VK_TAB){
			newTab();
		} else {
			char c = k.getKeyChar();
			if(isPrintableChar(c)){
				charTyped(k.getKeyChar());
			}
		}
	}

	private void newTab() {
		charTyped(' ');
		charTyped(' ');
		charTyped(' ');


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

	private void moveRight() {
		cursorX++;
		if(cursorX > lines.get(cursorY).length()){
			cursorX = 0;
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

	private void moveLeft() {
		cursorX --;
		if (cursorX < 0){
			cursorX = lines.get(cursorY).length() -1;				
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
		}

	}
}
