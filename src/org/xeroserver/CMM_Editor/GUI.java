package org.xeroserver.CMM_Editor;


import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.EmptyBorder;

public class GUI extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextArea editor_area;
	private JTextArea console_area;

	/**
	 * Create the frame.
	 */
	public GUI() {
		
		setTitle("C-- Editor");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 699, 560);
		
		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);
		
		JMenu mnSave = new JMenu("File");
		menuBar.add(mnSave);
		
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
		
		JMenuItem mntmVisualize = new JMenuItem("Visualize (Shift-V)");
		mntmVisualize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Editor.visualizeRequest = true;
			}
		});
		mnSave.add(mntmVisualize);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BorderLayout(0, 0));
		
		JScrollPane editor_scroll = new JScrollPane();
		contentPane.add(editor_scroll, BorderLayout.CENTER);
		
		editor_area = new JTextArea();
		editor_area.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				
				if(e.isControlDown())
				{
					if(e.getKeyCode() ==  KeyEvent.VK_O)
					{
						open();
					}else if(e.getKeyCode() ==  KeyEvent.VK_S)
					{
						save();
					}
					else if(e.getKeyCode() ==  KeyEvent.VK_E)
					{
						Editor.visualizeRequest = true;
					}

				}
			}
		});
		editor_area.setTabSize(2);
		editor_area.setFont(new Font("Arial", Font.PLAIN, 20));
		editor_scroll.setViewportView(editor_area);
		
		JScrollPane console_scroll = new JScrollPane();
		
		console_area = new JTextArea();
		console_area.setTabSize(2);
		console_area.setEditable(false);
		console_area.setPreferredSize(new Dimension((int)console_area.getSize().getWidth(), (int) console_area.getSize().getHeight() +100));
		console_scroll.setViewportView(console_area);
		contentPane.add(console_scroll,BorderLayout.SOUTH);

		
		setVisible(true);
	}
	
	public JTextArea getEditorArea() {
		return editor_area;
	}
	public JTextArea getConsoleArea() {
		return console_area;
	}
	
	private void save()
	{
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setCurrentDirectory(new File("."));
		fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int returnValue = fileChooser.showSaveDialog(null);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
          File selectedFile = fileChooser.getSelectedFile();
          Editor.setSave(selectedFile);
        }
	}
	
	private void open()
	{
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