package org.xeroserver.CMM_Editor;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.Truffle;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.cmm.CMMRuntime;
import com.oracle.truffle.cmm.TreeConverter;
import com.oracle.truffle.cmm.nodes.function.FunctionRootNode;
import com.oracle.truffle.cmm.parser.Node;
import com.oracle.truffle.cmm.parser.Parser;

public class Program implements Runnable {

	private JFrame cons = null;
	private Node mainNode = null;
	private String title = "TITLE";
	private Parser parser = null;

	public Program(Node mn, String t, Parser p) {
		this.title = t;
		this.mainNode = mn;
		this.parser = p;

		cons = new JFrame(title);
		cons.setSize(600, 300);
		cons.setResizable(true);
		cons.setLocationRelativeTo(null);

		WindowListener exitListener = new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				closeProgram();
				cons.dispose();

			}
		};

		cons.addWindowListener(exitListener);

		JTextArea area = new JTextArea();
		TextAreaOutputStream taOutputStream = new TextAreaOutputStream(area);

		area.setFont(new Font("Arial", Font.PLAIN, 20));

		PrintStream newOut = new PrintStream(taOutputStream);

		System.setOut(newOut);
		System.setErr(newOut);

		cons.setLayout(new BorderLayout());
		area.setEditable(false);

		cons.add(new JScrollPane(area, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER));

		JTextField tf = new JTextField();
		TextFieldStreamer ts = new TextFieldStreamer(tf);

		tf.addActionListener(ts);
		System.setIn(ts);

		cons.add(tf, BorderLayout.SOUTH);

	}

	@Override
	public void run() {

		cons.setVisible(true);

		FrameDescriptor desc = FrameDescriptor.create();
		CMMRuntime.initialize(parser, desc);
		TreeConverter converter = new TreeConverter();
		FunctionRootNode root = converter.buildTruffleTree(mainNode, desc);
		CallTarget call = Truffle.getRuntime().createCallTarget(root);
		call.call();

		closeProgram();

	}

	private void closeProgram() {

		System.setOut(Editor.stdout);
		System.setErr(Editor.stdout);
		System.setIn(Editor.stdin);

		Thread.currentThread().interrupt();
	}

}

class TextFieldStreamer extends InputStream implements ActionListener {

	private JTextField tf;
	private String str = null;
	private int pos = 0;

	public TextFieldStreamer(JTextField jtf) {
		tf = jtf;
	}

	@Override
	public void actionPerformed(ActionEvent e) {

		str = tf.getText() + "\n";
		pos = 0;
		tf.setText("");
		synchronized (this) {

			this.notifyAll();
		}
	}

	@Override
	public int read() {

		if (str != null && pos == str.length()) {
			str = null;

			return java.io.StreamTokenizer.TT_EOF;
		}

		while (str == null || pos >= str.length()) {
			try {
				synchronized (this) {
					this.wait();
				}
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
		return str.charAt(pos++);
	}
}

class TextAreaOutputStream extends OutputStream {

	private final JTextArea textArea;

	public TextAreaOutputStream(final JTextArea textArea) {
		this.textArea = textArea;
	}

	@Override
	public void write(int b) throws IOException {

		textArea.append((char) b + "");

	}
}