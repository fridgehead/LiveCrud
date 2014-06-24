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
	ArrayList<CodeLine> lines = new ArrayList<CodeLine>();
	int cursorX = 0;
	int cursorY = 0;
	int charWidth = 0;
	
	Point selectionStart, selectionEnd;
	boolean selectionRunning = false;
	
	ArrayList<Point> highLights = new ArrayList<Point>();
	StringBuffer clipboard;
	int markerPos = 0;
	
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
	
	int sizeX = 0;
	int sizeY = 0;
	

	public CodePanel(LiveCrud parent, int panelId){
		this.parent = parent;
		this.panelId = panelId;
		sizeX = parent.width;
		sizeY = parent.height;
		pGfx = parent.createGraphics(parent.width, parent.height, PApplet.P2D);
		lines.add(new CodeLine("protected void setup(){"));
		lines.add(new CodeLine(" "));
		lines.add(new CodeLine("}"));


		lines.add(new CodeLine("protected void draw(){"));
		lines.add(new CodeLine(" "));
		lines.add(new CodeLine("}"));
		
		lines.add(new CodeLine("protected void numpadKey(int key){"));
		lines.add(new CodeLine(" "));
		lines.add(new CodeLine("}"));
		
		lines.add(new CodeLine("protected void onBeat(){"));
		lines.add(new CodeLine(" "));
		lines.add(new CodeLine("}"));
		lines.add(new CodeLine("protected void onHalfBeat(){"));
		lines.add(new CodeLine(" "));
		lines.add(new CodeLine("}"));
		lines.add(new CodeLine("protected void onQuarterBeat(){"));
		lines.add(new CodeLine(" "));
		lines.add(new CodeLine("}"));


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
	
	public void resize(){
		pGfx = parent.createGraphics(parent.width, parent.height, PApplet.P2D);
		sizeX = parent.width;
		sizeY = parent.height;
	}

	public void draw(){
		
		if(parent.width  != sizeX && parent.height != sizeY){
			resize();
		}
		pGfx.beginDraw();
		pGfx.background(0,0,0,0);
		pGfx.fill(255);
		pGfx.noStroke();
		pGfx.textFont(font, 15);
		pGfx.pushMatrix();
		if(codeHeight > parent.height - 300){
			codeHeight = parent.height-300;
			
		}
		float tY = pGfx.height / ((float)codeHeight + 50.0f);
		float tX = pGfx.width / (maxWidth + 50);
		int cursorYScreen = 0;
		if(tX > tY){
			pGfx.scale(tY);
			cursorYScreen = (int) ((cursorY * 20 + 23) * tY);
		} else {
			pGfx.scale(tX);
			cursorYScreen = (int) ((cursorY * 20 + 23) * tX);
		}
		int ct = 40;
		maxWidth = 0;
		
		
		scrollOffset = 0;
		if(cursorYScreen > parent.height - 300){
			scrollOffset = parent.height - 300 - cursorYScreen  ;
		} else if (cursorY - scrollOffset < 5){
			//scrollOffset = cursorY -5 ;
			
		}
		pGfx.translate(0, scrollOffset);
		pGfx.fill(255,0,0,128);
		
		synchronized(lock){
			for(int i = 0; i < lines.size(); i++){
				
				CodeLine st = lines.get(i);
				float w = pGfx.textWidth(st.getString());
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
				if(st.setupMarker){
					pGfx.text("//----------------", 20, ct);
					markerPos = i;
				} else {
					pGfx.text(st.getString(), 20, ct);
				}
//				pGfx.fill(255,0,0);
//				pGfx.rect(st.tabStart * charWidth + 20, ct - 20, charWidth, 20);
				
				
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
		parent.imageMode(PApplet.CORNERS);
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
		if(lines.get(cursorY).immutable == true){
			return;
		}
		if(cursorX >= lines.get(cursorY).data.length()){
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
			CodeLine s = lines.get(cursorY);
			if(s.immutable == true){
				return;
			}
			s.delete(0, cursorX);
			cursorX = 0;
			s.tabStart = 0;
			
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
		lines.get(cursorY).newTab();

		state = CompileState.STATE_DIRTY;
	}

	private void newLine() {
		CodeLine l = lines.get(cursorY);
		if(l.immutable == true){
			return;
		}
		
		String end = l.substring(cursorX);
		if(end.length() == 0){
			end = " ";
		}
		synchronized (lock) {


			l.delete(cursorX, l.length());
			for(int i =0 ; i < l.tabStart / 3; i++){
				end = "   " + end;
			}
			CodeLine newLine = new CodeLine(end);
			newLine.tabStart = l.tabStart;
			lines.add(cursorY + 1, newLine);
		}
		moveDown();
		cursorX = l.tabStart;
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
			//find next ' ' in string
			String t = lines.get(cursorY).data.toString().substring(cursorX+1);
			int ind = t.indexOf(" ");
			//System.out.println(t + " " + ind);
			if(ind > 0){
				cursorX = cursorX + ind + 1;
			} else {
				cursorX = lines.get(cursorY).data.length() - 1;
			}
		} else if (mod == 1){
			cursorX = lines.get(cursorY).data.length() ;
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
			//cursorX = 0;
			//find next ' ' in string
			String t = lines.get(cursorY).data.toString().substring(0, cursorX);
			int ind = t.lastIndexOf(" ");
			//System.out.println(t + " " + ind);
			if(ind > 0){
				cursorX = ind;
			} else {
				cursorX = 0;
			}
		} else if (mod == 1){
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


			CodeLine l = lines.get(cursorY);
			if(l.immutable){
				return;
			}
			if(cursorX > 0){

//				if(cursorX-1 > 0){
					l.backSpace();
//				}
			} else {
				//backspace to end of prev line
				synchronized (lock) {
					
				
					CodeLine oldLine = lines.get(cursorY);
					
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
				CodeLine l = lines.get(0);
				if(l.immutable) return;
				l = new CodeLine(" ");
				cursorX = 0;
				cursorY = 0;

			} else {
				CodeLine l = lines.get(cursorY);
				if(l.immutable) return;
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
			System.out.println("sni");
				snippetMode = true;
			
		} else if (cCode == KeyEvent.VK_ESCAPE){
			snippetMode = false;
		} else if (cCode == KeyEvent.VK_C && e.getModifiers() == 2){
			copyLine();
		} else if (cCode == KeyEvent.VK_V && e.getModifiers() == 2){
			pasteLine();
			
		} else {
			
			
			
			char c = e.getKeyChar();
			if(snippetMode && c >= '1' && c <='5'){
				insertSnippet((int)cCode - 49);
			} else {
				snippetMode = false;
				//System.out.println(c);
				if(isPrintableChar(c)){
					charTyped(c);
				}
			}
		}
	} 
	
	private void charTyped(int cCode){
	
		//get current line
		synchronized(lock){
			CodeLine l = lines.get(cursorY);
			if(l.immutable) return;
			l.insert(cursorX, (char)cCode);
			cursorX++;
			state = CompileState.STATE_DIRTY;
			
		}
	
	}

	private void pasteLine() {
		if(lines.get(cursorY).immutable) return;
		if(clipboard != null){
			lines.add(cursorY, new CodeLine(clipboard.toString()) );
		}
	}

	private void copyLine() {
		if(lines.get(cursorY).immutable) return;
		clipboard = lines.get(cursorY).data;
		
		
	}

	private void insertSnippet(int i) {
		// TODO Auto-generated method stub
		if(lines.get(cursorY).immutable) return;
		synchronized (lock) {
			if(i >= 0 && i <snippets.length){
				lines.add(cursorY, new CodeLine(snippets[i]));
			}
			snippetMode = false;
			
		}
	}

	public enum CompileState {
		STATE_COMPILED, STATE_DIRTY, STATE_ERROR
	}
	
	public class CodeLine {
		public StringBuffer data;
		public boolean immutable = false;
		public boolean setupMarker = false;
		public int tabStart = 0;
		
		public CodeLine (String data){
			this.data = new StringBuffer(data);
		}
		
		public void backSpace() {
			
			if(data.length() > 0){
				
					if(cursorX - 1 < data.length()){
						data.deleteCharAt(cursorX-1);
					}
					cursorX--;
				if(cursorX < tabStart){
					tabStart = cursorX;
				}
			}
			
			
		}

		public void newTab() {
			insert(cursorX, ' ');
			insert(cursorX, ' ');
			insert(cursorX, ' ');
			if(cursorX <= tabStart){
				cursorX = tabStart + 3;
				tabStart += 3;
			} else {
				cursorX += 2;
			}
			
			
			
		}

		public int getTabStart(){
			return tabStart;
		}
		public String getString() {
			return data.toString();
			
		}
		public void insert(int cursorX, char cCode) {
			
			data.insert(cursorX,  cCode);
			
		}
		public String substring(int cursorX) {
			
			return data.substring(cursorX);
		}
		public int length() {
			
			return data.length();
		}
		public void delete(int i, int cursorX) {
			if(immutable == false){
				data.delete(i, cursorX);
				if(cursorX < tabStart){
					tabStart--;
				}
			}
			
		}
		public void append(CodeLine codeLine) {
			if(immutable == false){
				data.append(codeLine.getString());
			}
			
		}
		public void deleteCharAt(int cursorX) {
			if(immutable == false){
				data.deleteCharAt(cursorX);
				if(cursorX < tabStart){
					tabStart--;
				}
			}
			
		}
		
		public String toString(){
			return data.toString();
		}
	}
	
	
	
}
