package org.xeroserver.CMM_Editor.GUI;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextPane;
import javax.swing.border.EmptyBorder;

import org.xeroserver.CMM_Editor.Editor;

public class GUI extends JFrame {

	private static final long serialVersionUID = 1L;
	private JPanel contentPane;
	private JTextPane editor_area;
	private JTextArea console_area;

	private TextLineNumber lineView = null;
	private boolean showLineNumbers = true;
	private boolean showConsole = true;

	public GUI() {

		setTitle("C-- Editor v. " + Editor.version);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 699, 560);
		setLocationRelativeTo(null);

		URL iconURL = getClass().getResource("/x0.png");
		// iconURL is null when not found
		ImageIcon icon = new ImageIcon(iconURL);
		setIconImage(icon.getImage());

		JMenuBar menuBar = new JMenuBar();
		setJMenuBar(menuBar);

		JMenu menu_file = new JMenu("File");
		menuBar.add(menu_file);

		JMenu menu_actions = new JMenu("Actions");
		menuBar.add(menu_actions);

		JMenu menu_window = new JMenu("Window");
		menuBar.add(menu_window);

		JMenuItem mi_toggleline = new JMenuItem("Hide line numbers");
		mi_toggleline.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				toggleLineView();

				if (showLineNumbers) {
					mi_toggleline.setText("Hide line numbers");
				} else {
					mi_toggleline.setText("Show line numbers");
				}

			}
		});
		menu_window.add(mi_toggleline);

		JMenuItem mi_toggleconsole = new JMenuItem("Hide console");
		mi_toggleconsole.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {

				toggleConsole();

				if (showConsole) {
					mi_toggleconsole.setText("Hide console");
				} else {
					mi_toggleconsole.setText("Show console");
				}

			}
		});
		menu_window.add(mi_toggleconsole);

		JMenuItem mntmOpen = new JMenuItem("Open (Strg-O)");
		mntmOpen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				open();
			}
		});
		menu_file.add(mntmOpen);

		JMenuItem mntmSave = new JMenuItem("Save (Strg-S)");
		mntmSave.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});
		menu_file.add(mntmSave);

		menu_file.add(new JSeparator());

		JMenuItem mi_about = new JMenuItem("About");
		mi_about.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {

				JOptionPane.showMessageDialog(null,
						"C-- Editor with its AST Visualizer and Parser Frontend (using Coco/R [http://www.ssw.uni-linz.ac.at/Coco/]) developed by Daniel 'Xer0' Englisch. \n Backend developed by Jürger Kerbl with the Oracle's Truffle Framework [http://ssw.jku.at/Research/Projects/JVM/Truffle.html].");
			}
		});
		menu_file.add(mi_about);

		JMenuItem mntmVisualize = new JMenuItem("Visualize (Strg-E)");
		mntmVisualize.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Editor.visualizeRequest = true;
			}
		});
		menu_actions.add(mntmVisualize);

		JMenuItem mntmRunstrgr = new JMenuItem("Run (Strg-R)");
		mntmRunstrgr.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				Editor.run();

			}
		});
		menu_actions.add(mntmRunstrgr);
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

				// TypeColorizer.check(e, editor_area);

			}

		});
		editor_area.setFont(new Font("Arial", Font.PLAIN, 20));

		lineView = new TextLineNumber(editor_area);
		editor_scroll.setRowHeaderView(lineView);

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

	private void toggleLineView() {
		if (showLineNumbers) {
			lineView.hide();
		} else {
			lineView.show();

		}

		showLineNumbers = !showLineNumbers;
	}

	private void toggleConsole() {
		if (showConsole) {
			console_area.setPreferredSize(new Dimension(0, 0));
			console_area.setSize(0, 0);
			console_area.repaint();
			setSize(getWidth(), getHeight() + 1);
			setSize(getWidth(), getHeight() - 1);

		} else {
			console_area.setPreferredSize(new Dimension((int) console_area.getSize().getWidth(),
					(int) console_area.getSize().getHeight() + 100));
			setSize(getWidth(), getHeight() + 1);
			setSize(getWidth(), getHeight() - 1);

		}
		showConsole = !showConsole;
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
