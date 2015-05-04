package core;

import java.awt.Point;
import java.awt.RenderingHints.Key;
import java.awt.event.KeyEvent;
import java.io.File;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;

public class VimCodePanel extends CodePanel {
	
	public enum TextMode {
		EDITOR_EDIT, EDITOR_COMMAND;
	}
	
	ArrayList<KeyEvent> commandBuffer = new ArrayList<KeyEvent>();
	
	TextMode editorMode = TextMode.EDITOR_COMMAND;
	
	int commandRepetitions = 1;


	public VimCodePanel(LiveCrud parent, int panelId){
		super(parent, panelId);
		
	}
	
	protected void drawTextEditor() {
		super.drawTextEditor();
		String mode = "";
		if(editorMode == TextMode.EDITOR_COMMAND){
			mode = "COMMAND";
		} else if (editorMode == TextMode.EDITOR_EDIT){
			mode = "EDIT";
		}
		parent.text(mode, parent.width / 2, 10);
		parent.textFont(parent.font, 20);
		
	}

	public void nextWord(){
		cursorX += findWordBound();
	}
	
	//return index of next word bound on current line
	private int findWordBound() {
		CodeLine cl = lines.get(cursorY);
		String st = cl.getString();
		if(st.length() > 2){
			st = st.substring(cursorX+1, st.length() - 1);
			String regex = "[ {}()]";
			
			Pattern p = Pattern.compile(regex);
			Matcher m = p.matcher(st);
			if(m.find()){
				return m.start() + 1;
			}
		}
		return 0;
		
	}

	public void keyPressed(KeyEvent k){
		int cCode = k.getKeyCode();
	
		if(editorMode == TextMode.EDITOR_COMMAND){
			//only add printable chars to the command buffer
			
			
			if(k.getKeyChar() != KeyEvent.CHAR_UNDEFINED){
				commandBuffer.add(k);
				checkBuffer();
			}
			if (cCode == KeyEvent.VK_S && k.getModifiers() == 2){ 	//save
				System.out.println("jesus saves tab number : " + panelId);
				editorMode = TextMode.EDITOR_EDIT;
				startFileDialog(EditorMode.MODE_SAVE);
			} else if (cCode == KeyEvent.VK_L && k.getModifiers() == 2){ 	//load
				System.out.println("take a load : " + panelId);

				startFileDialog(EditorMode.MODE_LOAD);
			} else if (cCode == KeyEvent.VK_L && k.getModifiers() == 3){ 	//load
				System.out.println("insert file");

				startFileDialog(EditorMode.MODE_LOADINSERT);
			} else if (cCode == KeyEvent.VK_ESCAPE){
					editorMode = TextMode.EDITOR_COMMAND;	
			} else {
				if (cCode == KeyEvent.VK_ENTER){
					keyboardEnter(k);
				}
			}
		} else if (editorMode == TextMode.EDITOR_EDIT){
			if (cCode == KeyEvent.VK_ENTER){
				keyboardEnter(k);
			} else if (cCode == KeyEvent.VK_TAB){
				newTab();
			} else if (cCode == KeyEvent.VK_DELETE){
				deleteChar();
			} else if (cCode == KeyEvent.VK_ESCAPE){
				editorMode = TextMode.EDITOR_COMMAND;
				
			} else if (cCode == KeyEvent.VK_BACK_SPACE){
				
				backspaceChar();
				
			} else {
				
				charTyped(k);
			
			
			}
		}
	}

	private void checkBuffer() {
		int removeChar = 1;
		KeyEvent k = commandBuffer.get(0);
		
		char cCode = (char) k.getKeyCode();
		
		//check for repetitions
		if(cCode >= KeyEvent.VK_0 && cCode <= KeyEvent.VK_9 && commandRepetitions == 1 ){
			commandRepetitions = cCode - 48;
			commandBuffer.subList(0, 0).clear();

			
		}
		
		for(int i = 0; i < commandBuffer.size(); i++){
			System.out.print(commandBuffer.get(i).getKeyChar());
		}
		System.out.println("");
		
		
		if(cCode == KeyEvent.VK_ESCAPE){
			editorMode = TextMode.EDITOR_COMMAND;
			commandBuffer.clear();
			removeChar = 0;
		}
		else if(cCode == KeyEvent.VK_K){		//up
			moveUp();
		} else if (cCode == KeyEvent.VK_L){	//right
			moveRight(0);
		} else if (cCode == KeyEvent.VK_J){ 	//down
			moveDown();
		} else if (cCode == KeyEvent.VK_H){
			moveLeft(0);
		} else if (cCode == KeyEvent.VK_X){
//			if(k.getModifiers() == 1){		//shift back to delete line
//				deleteLine();
//			} else if (k.getModifiers() == 2){	//ctrl backspace to delete from cursorX back to 0
//				deleteToStartOfLine();
//			} else {
			for (int i = 0; i < commandRepetitions; i++){
				lines.get(cursorY).delete(cursorX, cursorX + 1);
			}
			commandRepetitions = 1;
		} else if (cCode == KeyEvent.VK_A){
			if(k.getModifiers() == 1){
				cursorX = lines.get(cursorY).length();
			}
			cursorX++;
			if(cursorX > lines.get(cursorY).length()){
				lines.get(cursorY).data.append(" ");
			}
			editorMode = TextMode.EDITOR_EDIT;

		} else if (cCode == KeyEvent.VK_I){
			if(k.getModifiers() == 1){
				editorMode = TextMode.EDITOR_EDIT;
				cursorX = 0;
			} else {
				editorMode = TextMode.EDITOR_EDIT;
			}
		} else if (cCode == KeyEvent.VK_W){
			nextWord();
		} else if (cCode == KeyEvent.VK_O){
			if(k.getModifiers() == 1){
				insertPrevLine();

			} else {
				insertNextLine();
			}
		} else if ( cCode == KeyEvent.VK_D){
			//get next char
			
			char nextChar = '\0';
			if(commandBuffer.size() < 2){
				removeChar = 0;
			} else {
				KeyEvent nextEvent = commandBuffer.get(1);
				nextChar = (char) nextEvent.getKeyCode();
				switch (nextChar){
				case KeyEvent.VK_D:
					//delete line
					for(int i = 0; i < commandRepetitions; i++){
						deleteLine();
					}
					commandRepetitions = 1;
					removeChar = 2;
					break;
				case KeyEvent.VK_W:
					//delete upto next word boundary
					for(int i = 0 ; i < commandRepetitions; i++){
						lines.get(cursorY).delete(cursorX, cursorX + findWordBound());
					}
					commandRepetitions = 1;
					removeChar = 2;
					break;
				default:
					removeChar = 2;
					break;
				}
			}
		} else if (cCode == KeyEvent.VK_P){
			pasteLine();
		
		} else if ( cCode == KeyEvent.VK_Y){
			//get next char
			
			char nextChar = '\0';
			if(commandBuffer.size() < 2){
				removeChar = 0;
			} else {
				KeyEvent nextEvent = commandBuffer.get(1);
				nextChar = (char) nextEvent.getKeyCode();
				switch (nextChar){
				case KeyEvent.VK_Y:
					//delete line
					copyLine();
					removeChar = 2;
					break;
				default:
					removeChar = 2;
					break;
				}
			}
		} else if ( cCode == KeyEvent.VK_R){
			if(commandBuffer.size() < 2){
				
				removeChar = 0;
			} else {
				KeyEvent nextEvent = commandBuffer.get(1);
				String c = "" + KeyEvent.getKeyText(nextEvent.getKeyCode());
				lines.get(cursorY).delete(cursorX, cursorX+1);
				lines.get(cursorY).insert(cursorX, nextEvent.getKeyChar());
				removeChar = 2;
				
			}
		} else if ( cCode == KeyEvent.VK_C){
			//get next char
			
			char nextChar = '\0';
			if(commandBuffer.size() < 2){
				removeChar = 0;
			} else {
				KeyEvent nextEvent = commandBuffer.get(1);
				nextChar = (char) nextEvent.getKeyCode();
				switch (nextChar){
				case KeyEvent.VK_W:
					//delete line
					for (int i = 0; i < commandRepetitions; i++){
						lines.get(cursorY).delete(cursorX, cursorX + findWordBound());
					}
					commandRepetitions = 1;
					editorMode = TextMode.EDITOR_EDIT;
					removeChar = 2;
					break;
				default: 
					removeChar = 2;
					break;
				}
			}
		} else if (cCode == 52){	//dollar
			removeChar = 1;
			cursorX = lines.get(cursorY).length() ;
		} else if (cCode == 54){	//caret
			removeChar = 1;
			cursorX = 0;
		} else if (cCode == 53){ //%
			
			cursorX = findNextBracket(cursorX);
		}
		
		commandBuffer.subList(0, removeChar).clear();
		
	}
	
	private int findNextBracket(int curX) {
		return curX; /*
		String line = lines.get(cursorY).getString();
		char curChar = line.charAt(curX);		
		char[][] valChars = { 	{'(', ')'},
								{'[', ']'},
								{'{', '}'}
		};
		int 
		if(valChars.contains("" + curChar)){
			//search time!
			//search each line for 
			return 1;
		} else {
			return curX;
		}
		*/
	}

	private void insertPrevLine() {
		// TODO Auto-generated method stub
		CodeLine l = new CodeLine(" ");
		lines.add(cursorY, l);
		cursorX = 0;
		
		editorMode = TextMode.EDITOR_EDIT;
		
	}

	private void insertNextLine() {
		// TODO Auto-generated method stub
		CodeLine l = new CodeLine(" ");
		lines.add(cursorY + 1, l);
		cursorX = 0;
		cursorY ++;
		editorMode = TextMode.EDITOR_EDIT;
		
	}

	
	
	
}
