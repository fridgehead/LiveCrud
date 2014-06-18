package core;

import java.awt.Point;
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
	
	Point selectionStart, selectionEnd;
	boolean selectionRunning = false;
	
	ArrayList<Point> highLights = new ArrayList<Point>();
	
	boolean snippetMode = false;
	String[] snippets;

	/* scaling stuff */
	int codeHeight = 0;
	float maxWidth = 0;
	float targetScaleX, targetScaleY;
	private float currentScaleX;
	private float currentScaley;
	
	int scrollOffset = 0;
	
	DrawableClass compileResult;
	
	public CompileState state = CompileState.STATE_DIRTY;
	
	PFont font; 	
	Object lock = new Object();
	PGraphics pGfx;
	private int panelId;
	private int errorRow = -1;
	

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
		
		lines.add(new StringBuffer("protected void onBeat(){"));
		lines.add(new StringBuffer(" "));
		lines.add(new StringBuffer("}"));
		lines.add(new StringBuffer("protected void onHalfBeat(){"));
		lines.add(new StringBuffer(" "));
		lines.add(new StringBuffer("}"));
		lines.add(new StringBuffer("protected void onQuarterBeat(){"));
		lines.add(new StringBuffer(" "));
		lines.add(new StringBuffer("}"));


		font = parent.loadFont("BitstreamVeraSansMono-Bold-48.vlw");
		parent.textFont(font, 15);
		//ZOMG ANOTHER HACK
		charWidth = (int)parent.textWidth(" ");
		targetScaleX = 1.0f;
		targetScaleY = 1.0f;
		currentScaleX = 1.0f;
		currentScaley = 1.0f;
		
		snippets = parent.loadStrings("snippets/snippets.txt");
		System.out.println("CodePanel : Loaded " + snippets.length + " snippets");
	}

	public void draw(){
		pGfx.beginDraw();
		pGfx.background(0,0,0,0);
		pGfx.fill(255);
		pGfx.noStroke();
		pGfx.textFont(font, 15);
		pGfx.pushMatrix();
		if(codeHeight > 900){
			codeHeight = 900;
			
		}
		float tY = pGfx.height / ((float)codeHeight + 50.0f);
		float tX = pGfx.width / (maxWidth + 50);
		
		if(tX > tY){
			pGfx.scale(tY);
			
		} else {
			pGfx.scale(tX);
		}
		int ct = 40;
		maxWidth = 0;
		int cursorYScreen = (int) ((cursorY * 20 + 23) * tY);
		
		scrollOffset = 0;
		if(cursorYScreen > parent.height - 300){
			scrollOffset = parent.height - 300 -cursorYScreen ;
		} else if (cursorY - scrollOffset < 5){
			//scrollOffset = cursorY -5 ;
			
		}
		pGfx.translate(0, scrollOffset);
		pGfx.fill(255,0,0,128);
		
		synchronized(lock){
			for(int i = 0; i < lines.size(); i++){
				
				String st = lines.get(i).toString();
				float w = pGfx.textWidth(st);
				if(w > maxWidth){
					maxWidth = w;
				}
				
				pGfx.fill(0,0,0,80);
				pGfx.rect(10, ct - 14, w + 15, 20);
				//tint the text slightly red if the panel needs compiling
				if(state != CompileState.STATE_COMPILED){
					pGfx.fill(255, 200, 200);
					//highlight error row
					if (i == errorRow){
						pGfx.fill(255,100,100);
					}
				
				}else {
					
					pGfx.fill(255);
				}
				
				pGfx.text(st, 20, ct);
				ct += 20;
			}
			codeHeight = ct+ 20;
		}
		//draw cursor
		pGfx.fill(255,255,0,200);
		pGfx.noStroke();
		pGfx.rect(cursorX * charWidth + 20, cursorY * 20 + 23, charWidth, 20);
		
		//draw snippet
		if(snippetMode){
			pGfx.fill(150,150,150,200);

			pGfx.rect(cursorX * charWidth + 20, cursorY * 20 + 23, 400, snippets.length * 11);
			pGfx.fill(255,255,255);
			 ct = 0;
			 pGfx.textFont(parent.font, 10);
			for(String sn : snippets){
				pGfx.text((ct + 1) + ": " + sn, cursorX * charWidth + 20, cursorY * 20 + 33 + ct *10 );
				ct++;
			}

		}
		
		pGfx.popMatrix();
		pGfx.endDraw();
		
		parent.hint(PApplet.DISABLE_DEPTH_TEST);
		parent.image(pGfx, 0, 0);

	}

	public void setErrorPos(int row, int col){
		errorRow = row;
		
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
				backspaceChar();
			}
		} else if (cCode == KeyEvent.VK_ENTER){
			keyboardEnter(k);
		} else if (cCode == KeyEvent.VK_TAB){
			newTab();
		} else if (cCode == KeyEvent.VK_DELETE){
			deleteChar();
		
		} else {
			
				charTyped(k);
			
		}
	}

	private void deleteChar() {
		if(cursorX >= lines.get(cursorY).length()){
			//move next line onto this line
			if(cursorY +1 < lines.size()){
				lines.get(cursorY).append(lines.get(cursorY + 1));
				lines.remove(cursorY + 1);
			}
			
			
		} else {
			lines.get(cursorY).deleteCharAt(cursorX);
			
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
					errorRow = -1;
				} else {
					state = CompileState.STATE_ERROR;
					
				}
			}
		} else if (k.getModifiers() == 1){ 	//shift + enter compiles but does not run the panel
			DrawableClass res = parent.compile(lines);
			if(res != null){
				state = CompileState.STATE_COMPILED;
				compileResult = res;
				errorRow = -1;
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
		if(end.length() == 0){
			end = " ";
		}
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
		} else if (mod == 1){
			if(selectionStart == null){
				selectionRunning =  true;
			}
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
	private void backspaceChar() {
		synchronized (lock) {


			StringBuffer l = lines.get(cursorY);
			if(cursorX > 0){

//				if(cursorX-1 > 0){
					if(l.length() > 0){
						l.deleteCharAt(cursorX-1);
					}
					cursorX--;
//				}
			} else {
				//backspace to end of prev line
				synchronized (lock) {
					
				
					StringBuffer oldLine = lines.get(cursorY);
					
					cursorY --;
					if(cursorY >= 0) {
						lines.get(cursorY).append(oldLine);
						lines.remove(cursorY + 1);
						cursorX ++;
						
					}
				}
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

	private void charTyped(KeyEvent e) {
		int cCode = e.getKeyCode();
		
		System.out.println("code: "  + cCode);
		
		if(e.getModifiers() == 1 && cCode == 32){
			
				snippetMode = true;
			
		} else if (cCode == KeyEvent.VK_ESCAPE){
			snippetMode = false;
			
		} else {
			
			
			
			char c = e.getKeyChar();
			if(snippetMode && c >= '1' && c <='5'){
				insertSnippet((int)cCode - 49);
			} else {
				//System.out.println(c);
				if(isPrintableChar(c)){
					charTyped(c);
				}
			}
		}
	} 
	
	private void insertSnippet(int i) {
		// TODO Auto-generated method stub
		synchronized (lock) {
			lines.add(cursorY, new StringBuffer(snippets[i]));
			snippetMode = false;
			
		}
	}

	private void charTyped(int cCode){
	
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
