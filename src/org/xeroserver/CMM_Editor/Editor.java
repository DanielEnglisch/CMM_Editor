package org.xeroserver.CMM_Editor;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Document;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;

import org.xeroserver.cmm.CMM_Frontend;

import com.oracle.truffle.cmm.parser.Node;

public class Editor {

	private static GUI gui = null;
	private static File file = new File(".", "test.cmm");
	public static boolean visualizeRequest = false;
	private static boolean hasErrors = false;
	private static Node mainNode = null;
	private static CMM_Frontend frontend = null;

	public static PrintStream stdout = System.out;
	public static InputStream stdin = System.in;

	public static Highlighter.HighlightPainter myHighlightPainter = new MyHighlightPainter(new Color(238, 130, 238));

	public static void run() {

		Thread th = new Thread(new Program(mainNode, file.getName(), frontend.getParser()));
		th.start();

	}

	public static void open(File f) {
		String contents = "";

		BufferedReader in = null;

		try {
			in = new BufferedReader(new FileReader(f));

			while (in.ready()) {
				contents += in.readLine() + "\n";
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		gui.getEditorArea().setText(contents);
		setSave(f);
	}

	public static void setSave(File f) {
		file = f;
	}

	public static void main(String[] args) throws BadLocationException {

		gui = new GUI();

		gui.getEditorArea().setText("void Main(){" + "\n\n\n" + "}");

		new Timer().scheduleAtFixedRate(new TimerTask() {

			@Override
			public void run() {

				save();

				checkErrors();

				colorTypes();

			}

		}, 0, 250);

	}

	public static void highlight(String pattern) {
		// First remove all old highlights

		JTextPane textComp = gui.getEditorArea();

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

	public static void removeHighlights() {
		JTextPane textComp = gui.getEditorArea();
		Highlighter hilite = textComp.getHighlighter();
		Highlighter.Highlight[] hilites = hilite.getHighlights();
		for (int i = 0; i < hilites.length; i++) {
			if (hilites[i].getPainter() instanceof MyHighlightPainter) {
				hilite.removeHighlight(hilites[i]);
			}
		}
	}


	public static void colorTypes() {
		removeHighlights();

		setHighlightColor(new Color(238, 130, 238));

		highlight("int ");
		highlight("double ");
		highlight("float ");
		highlight("bool ");
		highlight("void ");
		highlight("char ");
		highlight("string ");

		highlight(" int ");
		highlight(" double ");
		highlight(" float ");
		highlight(" bool ");
		highlight(" void ");
		highlight(" char ");
		highlight(" string ");

		setHighlightColor(Color.GREEN);

		highlight("const ");

		setHighlightColor(Color.YELLOW);
		highlight("print(");
		highlight("read()");
		highlight("readLine()");

	}

	public static void save() {
		String content = gui.getEditorArea().getText();

		BufferedWriter out = null;

		try {
			out = new BufferedWriter(new FileWriter(file));
			out.write(content);
			out.flush();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void setHighlightColor(Color c) {
		myHighlightPainter = new MyHighlightPainter(c);
	}

	public static void checkErrors() {
		
		CMM_Frontend front = new CMM_Frontend(file);
		frontend = front;

		try {
			front.parse();

		} catch (Exception e) {
		}

		JTextPane textArea = gui.getEditorArea();

		Highlighter highlighter = textArea.getHighlighter();

		if (front.getErrorCount() != 0) {
			hasErrors = true;
			
			HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.RED);

			HashMap<Integer, String> errors = front.getErrorList();

			for (Integer line : errors.keySet()) {
				line--;

				try {
					int p0 = textArea.getDocument().getDefaultRootElement().getElement(line).getStartOffset();
					int p1 = textArea.getDocument().getDefaultRootElement().getElement(line).getEndOffset();

					highlighter.addHighlight(p0, p1, painter);
				} catch (Exception e) {

				}

			}

			String cosoleContent = "Found " + front.getErrorCount() + " errors:" + "\n";
			for (String err : errors.values()) {
				cosoleContent += err + "\n";
			}

			gui.getConsoleArea().setText(cosoleContent);
		}

		else {
			// setWorkingMainTree
			mainNode = front.getMainNode();

			// Update once
			if (hasErrors) {
				highlighter.removeAllHighlights();
				gui.getConsoleArea().setText("No errors found!");
				hasErrors = false;
			}

			if (visualizeRequest) {
				if (!hasErrors) {
					JList<String> list = new JList<String>();
					String[] arr = new String[front.getProcedures().size()];

					list.setListData(front.getProcedures().toArray(arr));

					list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					list.setSelectedIndex(0);

					JOptionPane.showMessageDialog(null, list, "Select Procedure", JOptionPane.PLAIN_MESSAGE);

					String name = list.getSelectedValue();

					front.visualize(name);
				} else {
					JOptionPane.showMessageDialog(null, "Please fix all errors before visualizing!");
				}

				visualizeRequest = false;
			}

		}
	}

}

class MyHighlightPainter extends DefaultHighlighter.DefaultHighlightPainter {
	public MyHighlightPainter(Color color) {
		super(color);
	}
}
