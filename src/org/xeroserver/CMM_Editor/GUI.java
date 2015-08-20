package org.xeroserver.CMM_Editor;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;
import javax.swing.text.BadLocationException;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.StyledDocument;

public class GUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextPane editor_area;
	private JTextArea console_area;
	
	public int currentOffset = 0;

	/**
	 * Create the frame.
	 */
	public GUI() {

		setTitle("C-- Editor");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 699, 560);
		setLocationRelativeTo(null);

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu mnSave = new JMenu("File");
		menuBar.add(mnSave);
		//menuBar.add(new JButton("TEST"));

		JMenuItem mntmOpen = new JMenuItem("Open (Strg-O)");
		mntmOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				open();
			}
		});
		mnSave.add(mntmOpen);

		JMenuItem mntmSave = new JMenuItem("Save (Strg-S)");
		mntmSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});
		mnSave.add(mntmSave);

		JMenuItem mntmVisualize = new JMenuItem("Visualize (Strg-E)");
		mntmVisualize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Editor.visualizeRequest = true;
			}
		});
		mnSave.add(mntmVisualize);

		JMenuItem mntmRunstrgr = new JMenuItem("Run (Strg-R)");
		mntmRunstrgr.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Editor.run();

			}
		});
		mnSave.add(mntmRunstrgr);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));

		JScrollPane editor_scroll = new JScrollPane();
		contentPane.add(editor_scroll, BorderLayout.CENTER);

		editor_area = new JTextPane();

		editor_area.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {

				if (e.isControlDown()) {
					if (e.getKeyCode() == KeyEvent.VK_O) {
						open();
					} else if (e.getKeyCode() == KeyEvent.VK_S) {
						save();
					} else if (e.getKeyCode() == KeyEvent.VK_E) {
						Editor.visualizeRequest = true;
					} else if (e.getKeyCode() == KeyEvent.VK_R) {
						Editor.run();
					}

				}
				
				//TypeColorizer.check(e, editor_area);
				
				
			}
			

			
		});
		editor_area.setFont(new Font("Arial", Font.PLAIN, 20));
		


		TextLineNumber tln = new TextLineNumber(editor_area);
		editor_scroll.setRowHeaderView( tln );
		
		editor_scroll.setViewportView(editor_area);

		JScrollPane console_scroll = new JScrollPane();

		console_area = new JTextArea();
		console_area.setTabSize(2);
		console_area.setEditable(false);
		console_area.setFont(new Font("Arial", Font.PLAIN, 20));

		console_area.setPreferredSize(
				new Dimension((int) console_area.getSize().getWidth(), (int) console_area.getSize().getHeight() + 100));
		console_scroll.setViewportView(console_area);
		contentPane.add(console_scroll, BorderLayout.SOUTH);

		setVisible(true);
	}

	public JTextPane getEditorArea() {
		return editor_area;
	}

	public JTextArea getConsoleArea() {
		return console_area;
	}

	private void save() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File("."));
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
		int returnValue = fileChooser.showSaveDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			Editor.setSave(selectedFile);
		}
	}

	private void open() {
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File("."));
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);

		int returnValue = fileChooser.showOpenDialog(null);
		if (returnValue == JFileChooser.APPROVE_OPTION) {
			File selectedFile = fileChooser.getSelectedFile();
			Editor.open(selectedFile);
		}
	}

}


class TypeColorizer
{
	private static int bufferState = 0;
	
	private static int INT = 0;
	
	public static void check(KeyEvent e, JTextPane pane)
	{
		char c = e.getKeyChar();
		
		if(bufferState == 0 && c == 'i')
		{
			bufferState++;
		}else
			if(bufferState == 1 && c == 'n')
			{
				bufferState++;
			}else
				if(bufferState == 2 && c == 't')
				{
					colorize(INT,pane, pane.getCaretPosition());
				}else
				{
					bufferState = 0;
					colorize(e.getKeyChar(), pane.getCaretPosition(), pane);
				}
		
		
	}
	
	private static void colorize(char c, int loc, JTextPane pane)
	{
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
			document.insertString(loc, ""+c, styleBLACK);
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
	
	private static void colorize(int type, JTextPane pane, int loc)
	{
		StyledDocument document = pane.getStyledDocument();
		StyleContext context = new StyleContext();
		
		// build a style
		Style styleRED = context.addStyle("c_red", null);

		// set some style properties
		StyleConstants.setForeground(styleRED, Color.RED);

		// add some data to the document
		try {
			document.insertString(loc-2, "int", styleRED);
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