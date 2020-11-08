package theOnlyPackage;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class HandleKey implements KeyListener {
	NoteWork platform;
	
	public HandleKey (NoteWork platform) {
		this.platform = platform;
	}

	@Override
	public void keyTyped(KeyEvent e) {
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		char key = e.getKeyChar();
		int code = e.getKeyCode();
		
		if (e.isMetaDown() || e.isControlDown()) {
			if (!e.isShiftDown()) {
				if (key == 'y' || key == 'Y') {
					//TODO REDO
					return;
				}
				if (key == 'v' || key == 'V') {
					//TODO PASTE
					return;
				}
				if (key == 'x' || key == 'X') {
					//TODO CUT
					return;
				}
				if (key == 'w' || key == 'W') {
					platform.wrapSwitch();
					return;
				}
				if (key == 'c' || key == 'C') {
					//TODO COPY
					return;
				}
				if (key == 's' || key == 'S') {
					platform.saveFile();
					return;
				}
				if (key == 'a' || key == 'A') {
					//TODO SELECTALL
					return;
				}
				if (key == 'k' || key == 'K') {
					platform.goToLine();
					return;
				}
				if (key == 'n' || key == 'N') {
					platform.newFile();
					return;
				}
				if (key == 'f' || key == 'F') {
					platform.findText();
					return;
				}
				if (key == 'z' || key == 'Z') {
					//TODO UNDO
					return;
				}
				if (key == 'd' || key == 'D') {
					platform.removeLine();
					return;
				}
				if (key == 'g' || key == 'G') {
					platform.findNext();
					return;
				}
				if (key == 'r' || key == 'R') {
					//TODO REPLACETEXT
					return;
				}
				if (key == 'o' || key == 'O') {
					platform.openFile();
					return;
				}
				if (key == 'b' || key == 'B') {
					platform.saveFileAs();
					return;
				}
				if (key == 'l' || key == 'L') {
					platform.showLineNumberSwitch();
					return;
				}
				if (key == 'i' || key == 'I') {
					//TODO INSERT
					return;
				}
				
				
				if (code == KeyEvent.VK_UP) {
		        	//TODO CTRL+UP
		            return;
				}
				if (code == KeyEvent.VK_DOWN) {
		        	//TODO CTRL+DOWN
		            return;
				}
				if (code == KeyEvent.VK_HOME) {
		        	//TODO CTRL+HOME
		            return;
				}
				if (code == KeyEvent.VK_END) {
					//TODO CTRL+END
		            return;
				}
			}
			return;//Here?
		}
		if (code == KeyEvent.VK_LEFT) {
            platform.cursorLeft();
            return;
		}
        if (code == KeyEvent.VK_UP) {
        	platform.cursorUp();
            return;
		}
        if (code == KeyEvent.VK_RIGHT) {
        	platform.cursorRight();
            return;
		}
        if (code == KeyEvent.VK_PAGE_UP) {
        	//TODO PAGEUP
        	return;
        }
        if (code == KeyEvent.VK_DOWN) {
        	platform.cursorDown();
            return;
		}
        if (code == KeyEvent.VK_HOME) {
        	//TODO HOME
        	return;
        }
        if (code == KeyEvent.VK_END) {
        	//TODO END
        	return;
        }
        if (code == KeyEvent.VK_PAGE_DOWN) {
        	//TODO PAGEDOWN
        	return;
        }
        if (code == KeyEvent.VK_INSERT) {
			//TODO INSERT
			return;
		}
		if (code == KeyEvent.VK_DELETE) {
        	platform.delete();
			return;
        }
		if (code == KeyEvent.VK_BACK_SPACE) {
			platform.backSpace();
			return;
        }
		if (code == KeyEvent.VK_TAB) {
	        platform.typeing((char) KeyEvent.VK_SPACE);
	        platform.typeing((char) KeyEvent.VK_SPACE);
	        platform.typeing((char) KeyEvent.VK_SPACE);
	        platform.typeing((char) KeyEvent.VK_SPACE);
        	return;
        }
		if (key == KeyEvent.VK_ENTER) {
			platform.enter();
			return;
		}
		if (!e.isActionKey() && !e.isAltDown() && code != KeyEvent.VK_SHIFT && !e.isAltGraphDown() && code != 0 && code != 27)
			platform.typeing(key);
	}

	@Override
	public void keyReleased(KeyEvent e) {
	}
}
