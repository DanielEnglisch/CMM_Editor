package org.xeroserver.CMM_Editor.SynCol;

import java.awt.Color;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;


public class TypeHighlighter
{
	
	
	
	public static Highlighter.HighlightPainter myHighlightPainter = new MyHighlightPainter(new Color(238, 130, 238));

	
	public static void highlight(String pattern, JTextPane textComp) {
		// First remove all old highlights


		try {
			Highlighter hilite = textComp.getHighlighter();

			Document doc = textComp.getDocument();
			String text = doc.getText(0, doc.getLength());
			int pos = 0;

			while ((pos = text.indexOf(pattern, pos)) >= 0) {
				hilite.addHighlight(pos, pos + pattern.length(), myHighlightPainter);
				pos += pattern.length();
			}
		} catch (BadLocationException e) {
		}
	}

	public static void removeHighlights(JTextPane textComp) {
		Highlighter hilite = textComp.getHighlighter();
		Highlighter.Highlight[] hilites = hilite.getHighlights();
		for (int i = 0; i < hilites.length; i++) {
			if (hilites[i].getPainter() instanceof MyHighlightPainter) {
				hilite.removeHighlight(hilites[i]);
			}
		}
	}


	public static void colorTypes(JTextPane pane) {
		removeHighlights(pane);

		setHighlightColor(new Color(238, 130, 238));

		highlight("int ",pane);
		highlight("double ",pane);
		highlight("float ",pane);
		highlight("bool ",pane);
		highlight("void ",pane);
		highlight("char ",pane);
		highlight("string ",pane);

		highlight(" int ",pane);
		highlight(" double ",pane);
		highlight(" float ",pane);
		highlight(" bool ",pane);
		highlight(" void ",pane);
		highlight(" char ",pane);
		highlight(" string ",pane);

		setHighlightColor(Color.GREEN);

		highlight("const ",pane);

		setHighlightColor(Color.YELLOW);
		highlight("printLine(",pane);
		highlight("print(",pane);
		highlight("read()",pane);
		highlight("readLine()",pane);

	}
	
	public static void setHighlightColor(Color c) {
		myHighlightPainter = new MyHighlightPainter(c);
	}
		
}

class MyHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter {
	public MyHighlightPainter(Color color) {
		super(color);
	}
}