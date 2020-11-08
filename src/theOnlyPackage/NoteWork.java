package theOnlyPackage;

import java.awt.Color;
import java.util.ArrayList;
import java.io.File;
import java.util.LinkedList;
import java.awt.Graphics;
import java.util.Scanner;
import java.awt.Dimension;
import java.awt.Font;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JFrame;

public class NoteWork extends JFrame{    
    LinkedList< Pair< String, ArrayList<Integer> > > lines;
    File file;
    int x0 = 50, y0 = 50;
    int rowCount, columnCount;
    int startLine = 0, startSection = 0, startChar = 0;
    boolean wordWrapIsOn = false;
    boolean showLineNumber = true;
    
    public NoteWork (String title) {
        super(title);
        setBounds(100, 100, 1000, 600);
        setMinimumSize(new Dimension(200, 100));
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
        setFocusTraversalKeysEnabled(false);
        HandleKey KBListener = new HandleKey(this);
        addKeyListener(KBListener);
        newFile();
    }
    
    public void sepNumFind (int lineNum) {
        String str = lines.get(lineNum).getFirst();
        ArrayList<Integer> res = new ArrayList<Integer>();
        lines.get(lineNum).setSecond(res);
        
        if (str.length() == 0) {
            res.add(0);
            res.add(0);
            return;
        }
        
        res.add(0);
        if (!wordWrapIsOn) {
            res.add(str.length());
            return;
        }
        
        for (int f1 = 0, f2; f1 < str.length(); ) {
            f2 = f1;
            int cutPlace = -1;
            
            while (f2 < str.length() && (f2 - f1) < columnCount) {
                if ( f2 == str.length() - 1 || Character.isWhitespace(str.charAt(f2)) || Character.isWhitespace( str.charAt(f2 + 1) ) )
                    cutPlace = f2;
                ++f2;
            }
            
            if (cutPlace == -1)
                res.add(f2);
            else
                res.add(cutPlace + 1);
            
            f1 = res.get(res.size() - 1);
        }
        
        
    }
    
    public void drawLines(Graphics g) {
        for (int i = 0; i <= Math.min(rowCount, lines.size() - startLine - 1); ++i) {
            String tempLine = lines.get(startLine + i).getFirst();
            if (showLineNumber) {
            		Color tempColor = g.getColor();
            		g.setColor(Color.GRAY);
            		g.drawString("" + (startLine + i + 1), 7 , y0 + i * 13);
            		g.setColor(tempColor);
            }
            if (startChar >= tempLine.length())
                continue;
            tempLine = tempLine.substring( startChar, Math.min( startChar + columnCount, tempLine.length() ) );
            g.drawString(tempLine, x0 , y0 + i * 13);
        }
        
    }
    
    public void drawLinesWrap(Graphics g) {
        int typedLinesNum = 0;
        int tempLine = startLine;
        int tempSection = startSection;
        while (typedLinesNum <= rowCount && tempLine < lines.size()) {
            String str = lines.get(tempLine).getFirst();
            if (showLineNumber && (str.length() == 0 || tempSection == 0)) {
        		Color tempColor = g.getColor();
	        		g.setColor(Color.GRAY);
	        		g.drawString("" + (tempLine + 1), 7 , y0 + typedLinesNum * 13);
        			g.setColor(tempColor);
            }
            if (str.length() == 0) {
                typedLinesNum++;
                tempLine++;
                tempSection = 0;
                continue;
            }
            int bgn = lines.get(tempLine).getSecond().get(tempSection);
            int end = lines.get(tempLine).getSecond().get(tempSection + 1);
            str = str.substring(bgn, end);
            
            g.drawString(str, x0 , y0 + typedLinesNum * 13);
            typedLinesNum++;
            
            tempSection++;
            if (tempSection == lines.get(tempLine).getSecond().size() - 1) {
                tempLine++;
                tempSection = 0;
            }
        }
    }
    
    @Override
    public void paint(Graphics g) {
        super.paint(g);
        rowCount = (getSize().height - y0) / 13;
        columnCount = (getSize().width - x0) / 7;

        g.setFont(new Font("Monospaced", Font.PLAIN, 12));
        
        if (wordWrapIsOn) {
            sepNumFind(cursorLine);
            cursorSection = -1;
            for (int i = 1; i < lines.get(cursorLine).getSecond().size(); ++i)
                if (cursorChar < lines.get(cursorLine).getSecond().get(i)) {
                    cursorSection = i - 1;
                    break;
                }
            if (cursorSection == -1)
                cursorSection = 0;
            startLine = cursorLine;
            startSection = cursorSection;
        } else {
            if (cursorLine < startLine || startLine + rowCount <= cursorLine)
                startLine = Math.max(0, cursorLine - rowCount + 1);
            if (cursorChar < startChar || startChar + columnCount <= cursorChar)
                startChar = Math.max(0, cursorChar - columnCount + 1);
        }
        
        for (int i = startLine; i < lines.size() && i < startLine + rowCount + 10; i++)
            sepNumFind(i);
        
        if (wordWrapIsOn) {
            drawLinesWrap(g);
            drawCursorWrap(g);
        } else {
            drawLines(g);
            drawCursor(g);
        }        
    }
    
    //<FILE>
    boolean changed = false;
    JFileChooser dialog = new JFileChooser();
    
    void saveFileAs() {
        if (dialog.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            file = dialog.getSelectedFile();
            writeFile();
        }
    }
    
    void saveFile() {
        if (file == null)
            if (dialog.showSaveDialog(this) == JFileChooser.APPROVE_OPTION)
                file = dialog.getSelectedFile();
            else
                return;
        writeFile();
    }
    
    void openFile() {
        saveIfChanged();
        if (dialog.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            file = dialog.getSelectedFile();
            startLine = startChar = startSection = cursorSection = 0;
            cursorLine = cursorChar = 0;
            changed = false;
            readFile();
        }
    }
    
    void writeFile () {
        try {
            FileWriter w = new FileWriter(file);
            for (Pair<String, ArrayList<Integer>> p : lines) {
                String line = p.getFirst();
                w.write(line + "\n");
            }
            w.close();
            changed = false;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    void readFile() {
        try {
            Scanner r = new Scanner(file);
            lines = new LinkedList<Pair<String, ArrayList<Integer>>>();
            System.gc();
            if (!r.hasNextLine()) {
                lines.add(new Pair<String, ArrayList<Integer>>("", null));
                r.close();
                return;
            }
            while (r.hasNextLine()) {
                lines.add(new Pair<String, ArrayList<Integer>>(tabReplace(r.nextLine()), null));
            }
            r.close();
            repaint();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private String tabReplace(String str) {
        StringBuilder res = new StringBuilder(str);
        int index = 0;
        while (index < res.length()) {
            if (res.charAt(index) == '\t') {
                res.replace(index, index + 1, "    ");
                index += 3;
            }
            ++index;
        }
        return res.toString();
    }
    
    void newFile() {
        saveIfChanged();
        file = null;
        lines = new LinkedList<Pair<String, ArrayList<Integer>>>();
        lines.add(new Pair<String, ArrayList<Integer>>("", null));
        startLine = startChar = startSection = cursorSection = 0;
        cursorLine = cursorChar = 0;
        changed = false;
        repaint();
    }
    
    void saveIfChanged() {
        if (changed)
            if (JOptionPane.showConfirmDialog(this, "Save?", "Save", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                saveFile();
                changed = false;
            }
        
    }
    //</FILE>
    
    
    //<CURSOR>
    int cursorLine = 0, cursorChar = 0;
    
    
    void drawCursor(Graphics g) {
        Color tmpColor = g.getColor();
        g.setColor(Color.RED);
        g.drawLine((cursorChar - startChar) * 7 + x0, (cursorLine - startLine) * 13 + y0 - 11,
                   (cursorChar - startChar) * 7 + x0, (cursorLine - startLine) * 13 + y0 + 3);
        g.setColor(tmpColor);
    }
    
    void cursorLeft() {
        if (cursorChar == startChar) {
            if (cursorChar == 0) {
                if (cursorLine == startLine) {
                    if (startLine == 0)
                        return;
                    startLine--;
                }
                int strLen = lines.get(cursorLine - 1).getFirst().length();
                cursorLine--;
                cursorChar = strLen;
                startChar = Math.max(0, strLen - columnCount);
            }
            else {
                startChar--;
                cursorChar--;
            }
        }
        else
            cursorChar--;
        
        repaint();
    }
    
    void cursorRight() {
        int strLen = lines.get(cursorLine).getFirst().length();
        if (cursorChar == strLen) {
            if (cursorLine == lines.size() - 1)
                return;
            cursorChar = 0;
            startChar = 0;
            cursorLine++;
            if (cursorLine == startLine + rowCount)
                startLine++;
        }
        else {
            cursorChar++;
            if (cursorChar == startChar + columnCount)
                startChar++;
        }
        
        repaint();
    }
    
    void cursorUp() {
        if (startLine == cursorLine) {
            if (startLine == 0)
                return;
            startLine--;
        }
        
        int strLen = lines.get(cursorLine - 1).getFirst().length();
        cursorLine--;
        if (strLen < cursorChar) {
            cursorChar = strLen;
            startChar = Math.min(startChar, strLen);
        }
        
        repaint();
    }
    
    void cursorDown() {
        if (cursorLine == lines.size() - 1)
            return;
        if (cursorLine == startLine + rowCount)
            startLine++;
        cursorLine++;
        int strLen = lines.get(cursorLine).getFirst().length();
        if (cursorChar > strLen) {
            cursorChar = strLen;
            startChar = Math.min(startChar, strLen);
        }
        
        repaint();
    }
    
    // _________________wrapMod________________________
    
    int cursorSection = 0;
    
    
    void drawCursorWrap (Graphics g) {
        cursorSection = -1;
        for (int i = 1; i < lines.get(cursorLine).getSecond().size(); ++i)
            if (cursorChar < lines.get(cursorLine).getSecond().get(i)) {
                cursorSection = i - 1;
                break;
            }
        if (cursorSection == -1)
            cursorSection = 0;
        
        int xCount = cursorChar - lines.get(cursorLine).getSecond().get(cursorSection);
        int yCount = 0;
        
        if (startLine == cursorLine)
            yCount += cursorSection - startSection + 1;
        else {
            yCount += (lines.get(startLine).getSecond().size() - 1) - (startSection + 1) + 1;
            for (int i = startLine + 1; i < cursorLine; ++i)
                yCount += lines.get(i).getSecond().size() - 1;
            yCount += cursorSection + 1;
        }
        
        Color tmpColor = g.getColor();
        g.setColor(Color.RED);
        g.drawLine(xCount * 7 + x0, (yCount - 1) * 13 + y0 - 11,
                   xCount * 7 + x0, (yCount - 1) * 13 + y0 + 3);
        g.setColor(tmpColor);
    }
    
    void cursorLeftWrap () {
        
        
    }
    
    //<CURSOR>
    
    public void typeing(char keyChar) {
        Pair< String, ArrayList<Integer> > line = lines.get(cursorLine);
        if (cursorChar == 0) {
            line.setFirst(keyChar + line.getFirst());
        }
        else if (cursorChar == line.getFirst().length()) {
            line.setFirst(line.getFirst() + keyChar);
        }
        else {
            StringBuilder res = new StringBuilder();
            res.append(line.getFirst().substring(0, cursorChar));
            res.append(keyChar);
            res.append(line.getFirst().substring(cursorChar));
            line.setFirst(res.toString());
        }
        cursorRight();
        changed = true;
    }
    
    public void wrapSwitch() {
        startLine = cursorLine;
        if (wordWrapIsOn) {
            startChar = cursorChar;
            wordWrapIsOn = false;
        }
        else {
            sepNumFind(cursorLine);
            ArrayList<Integer> nums = lines.get(cursorLine).getSecond();
            startSection = 0;
            while(true) {
                if (nums.get(startSection) <= cursorChar)
                    break;
                startSection++;
            }
            wordWrapIsOn = true;
        }
        repaint();
    }
    
    public void removeLine() {
        if (lines.size() == 1) {
            lines.getFirst().setFirst("");
            cursorChar = startChar = startSection = 0;
        }
        else if (cursorLine == lines.size() - 1) {
            cursorUp();
            cursorChar = startChar = startSection = 0;
            lines.removeLast();
        }
        else {
            cursorChar = startChar = startSection = 0;
            lines.remove(cursorLine);
        }
        repaint();
        changed = true;
    }
    
    public void goToLine() {
        String str = JOptionPane.showInputDialog(this, "Go to line: (a-z to cancel)", cursorLine + 1);
        if (!str.matches("\\d+"))
            return;
        int tmp = Integer.parseInt(str);
        if (tmp > lines.size() || tmp == 0)
        		return;
        startLine = cursorLine = tmp - 1;
        startChar = cursorChar = startSection = 0;
        repaint();
    }
    
    int foundLine = -1, foundChar = 0;
    String findQuery = "";
    
    public void findText() {
    		findQuery = JOptionPane.showInputDialog(this, "Find: ", findQuery);
    		foundLine = -1;
    	    for (int i = 0; i < lines.size(); i++) {
    	    		int tmp = lines.get(i).getFirst().indexOf(findQuery);
    	    		if (tmp != -1) {
    	    			foundLine = i;
    	    			foundChar = tmp;
    	    			startLine = cursorLine = foundLine;
    	    			cursorChar = startChar = foundChar;
    	    			repaint();
    	    			break;
    	    		}
    	    }
    }
    
    public void findNext() {
    		if (foundLine == -1)
    			return;
		foundChar += findQuery.length();
		if (foundChar >= lines.get(foundLine).getFirst().length()) {
			if (foundLine < lines.size() - 1)
				return;
			foundLine++;
			foundChar = 0;
		}
		for (int i = foundLine; i < lines.size(); i++) {
			if (i > foundLine)
				foundChar = 0;
	    		int tmp = lines.get(i).getFirst().indexOf(findQuery, foundChar);
	    		if (tmp != -1) {
	    			foundLine = i;
	    			foundChar = tmp;
	    			startLine = cursorLine = foundLine;
	    			cursorChar = startChar = foundChar;
	    			repaint();
	    			return;
	    		}
		}
    }
    
    public void backSpace() {
        int tempLine = cursorLine, tempChar = cursorChar;
        cursorLeft();
        String str = lines.get(tempLine).getFirst();
        if (tempChar != 0) {
            StringBuilder res = new StringBuilder(str.substring(0,  tempChar - 1));
            res.append(str.substring(tempChar));
            lines.get(tempLine).setFirst(res.toString());
        } else {
            if (tempLine != 0) {
                str = lines.get(tempLine - 1).getFirst();
                if (str.length() > 0) {
                    lines.get(tempLine - 1).setFirst(str + lines.get(tempLine).getFirst());
                    lines.remove(tempLine);
                }
                else
                    lines.remove(tempLine - 1);
            }
            else
            	return;
        }
        repaint();
        changed = true;
    }
    
    public void delete() {
    	String str = lines.get(cursorLine).getFirst();
        if (cursorChar != str.length()) {
            StringBuilder res = new StringBuilder(str.substring(0,  cursorChar));
            res.append(str.substring(cursorChar + 1));
            lines.get(cursorLine).setFirst(res.toString());
        }
        else if (lines.size() - 1 > cursorLine) {
            str = lines.get(cursorLine + 1).getFirst(); 
            if (str.length() > 0)
                lines.get(cursorLine).setFirst(lines.get(cursorLine).getFirst() + str);
            else
                lines.remove(cursorLine + 1);
        }
        else
        		return;
        repaint();
        changed = true;
    }
    
    public void enter() {
        String str = lines.get(cursorLine).getFirst();
        lines.get(cursorLine).setFirst(str.substring(0, cursorChar));
        if (cursorLine != lines.size() - 1)
            lines.add(cursorLine + 1, new Pair<String, ArrayList<Integer>>(str.substring(cursorChar), null));
        else
            lines.addLast(new Pair<String, ArrayList<Integer>>(str.substring(cursorChar), null));
        cursorRight();
        changed = true;
    }
    
    public void showLineNumberSwitch() {
    		showLineNumber = !showLineNumber;
    		repaint();
    }
    
}
