package org.xeroserver.CMM_Editor;

import java.awt.Color;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.text.BadLocationException;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.Highlighter;
import javax.swing.text.Highlighter.HighlightPainter;

import org.xeroserver.cmm.CMM_Frontend;

public class Editor {
	
	private static GUI gui = null;
	private static File file = new File(".", "test.cmm");
	public static boolean visualizeRequest = false;
	private static boolean hasErrors = false;

	
	public static void open(File f)
	{
		String contents = "";
		
		BufferedReader in = null;
		
		try
		{
			in = new BufferedReader(new FileReader(f));
			
			while(in.ready())
			{
				contents += in.readLine() + "\n";
			}
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
		}
		
		gui.getEditorArea().setText(contents);
		setSave(f);
	}
	
	public static void setSave(File f)
	{
		file = f;
	}
	
	public static void main(String[] args) throws BadLocationException {
		
		gui = new GUI();
		
		
		gui.getEditorArea().setText(
				"void Main(){"
				+ "\n\n\n"
				+ "}");
		
		new Timer().scheduleAtFixedRate(new TimerTask(){

			@Override
			public void run() {
				
				save();
				
				checkErrors();
			}
			
		}, 0, 250);
		
	}
	
	public static void save()
	{
		String content = gui.getEditorArea().getText();
		
		BufferedWriter out = null;
		
		try
		{
			out = new BufferedWriter(new FileWriter(file));
			out.write(content);
			out.flush();
			out.close();
		}
		catch(Exception e){e.printStackTrace();}
	}
	
	public static void checkErrors()
	{
		CMM_Frontend front = new CMM_Frontend(file);
	
		try
		{		front.parse();

			
		}catch(Exception e){}
	
		JTextArea textArea = gui.getEditorArea();


	  Highlighter highlighter = textArea.getHighlighter();
      
  
		
		if(front.getErrorCount() != 0)
		{
			hasErrors = true;
		    HighlightPainter painter = new DefaultHighlighter.DefaultHighlightPainter(Color.pink);

			HashMap<Integer,String> errors = front.getErrorList();
			
			for(Integer line : errors.keySet())
			{
				line--;
			    
			      try {
			    		 int p0 = textArea.getLineStartOffset(line);
					      int p1 = textArea.getLineEndOffset(line);
					      					      
					highlighter.addHighlight(p0, p1, painter);
				} catch (Exception e) {

				}
			   
			    
			}
			
			String cosoleContent = "Found " + front.getErrorCount() + " errors:" + "\n" ;
			 for(String err : errors.values())
		     {
				 cosoleContent += err;
		     }
			 
			 gui.getConsoleArea().setText(cosoleContent);
		}
		
		else
		{
			//Update once
			if(hasErrors)
			{
				highlighter.removeAllHighlights();
				gui.getConsoleArea().setText("No errors found!");
				hasErrors = false;
			}
			
			 
			
			 if(visualizeRequest)
			 {
				 if(!hasErrors)
				 {
					 JList<String> list = new JList<String>();
					 String[] arr = new String[front.getProcedures().size()];
					 
					 
					 
					 list.setListData(front.getProcedures().toArray(arr));
					 
					 list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
					 list.setSelectedIndex(0);
					 
					 JOptionPane.showMessageDialog( null, list, "Select Procedure", JOptionPane.PLAIN_MESSAGE);
					 
					String name = list.getSelectedValue();
					
					
					
					 front.visualize(name);
				 }else
				 {
					 JOptionPane.showMessageDialog(null, "Please fix all errors before visualizing!");
				 }
				 
				 
				 visualizeRequest = false;
			 }

		}
	}
	

}
