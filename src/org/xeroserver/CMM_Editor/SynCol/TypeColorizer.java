package org.xeroserver.CMM_Editor.SynCol;

import java.awt.Color;
import java.awt.event.KeyEvent;

import javax.swing.JTextPane;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public class TypeColorizer {
	private static int bufferState = 0;

	private static int INT = 0;

	public static void check(KeyEvent e, JTextPane pane) {
		char c = e.getKeyChar();

		if (bufferState == 0 && c == 'i') {
			bufferState++;
		} else if (bufferState == 1 && c == 'n') {
			bufferState++;
		} else if (bufferState == 2 && c == 't') {
			colorize(INT, pane, pane.getCaretPosition());
		} else {
			bufferState = 0;
			colorize(e.getKeyChar(), pane.getCaretPosition(), pane);
		}

	}

	private static void colorize(char c, int loc, JTextPane pane) {
		StyledDocument document = pane.getStyledDocument();
		StyleContext context = new StyleContext();

		// build a style
		Style styleBLACK = context.addStyle("c_black", null);

		// set some style properties
		StyleConstants.setForeground(styleBLACK, Color.BLACK);
		StyleConstants.setFontFamily(styleBLACK, "Arial");
		StyleConstants.setFontSize(styleBLACK, 24);

		// add some data to the document

		pane.setLogicalStyle(styleBLACK);
		pane.addStyle("black", styleBLACK);

		try {
			document.insertString(loc, "" + c, styleBLACK);
			System.out.println("inserted black: " + c);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			document.remove(loc, 1);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private static void colorize(int type, JTextPane pane, int loc) {
		StyledDocument document = pane.getStyledDocument();
		StyleContext context = new StyleContext();

		// build a style
		Style styleRED = context.addStyle("c_red", null);

		// set some style properties
		StyleConstants.setForeground(styleRED, Color.RED);

		// add some data to the document
		try {
			document.insertString(loc - 2, "int", styleRED);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		try {
			document.remove(loc, 3);
		} catch (BadLocationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}