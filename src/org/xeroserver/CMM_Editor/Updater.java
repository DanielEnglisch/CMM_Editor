package org.xeroserver.CMM_Editor;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.MessageDigest;

public class Updater {
	
	private String checkSumFile = "http://xeroserver.org/api/cmm/cs_editor.php";
	
	private String localChecksum = "local";
	private String remoteChecksum = "remote";
	
	private boolean offline = false;



	
	public Updater()
	{
		
		if(!isOnline())
		{
			offline = true;
			System.out.println("Host is offline...");
		}
		
		else
		{
			
			localChecksum = getLocalChecksum();
			remoteChecksum = getRemoteChecksum();
		}
		

	}
	
	public boolean isOnline()
	{
		
		 try {
	            //make a URL to a known source
	            URL url = new URL("http://www.google.com");

	            //open a connection to that source
	            HttpURLConnection urlConnect = (HttpURLConnection)url.openConnection();

	            //trying to retrieve data from the source. If there
	            //is no connection, this line will fail
	            urlConnect.getContent();

	        } catch (Exception e) {              
	            return false;
	        }

	        return true;
	}
	
	
	
	public boolean isUpdateAvailable()
	{
		if(offline)
			return false;
		
		return !localChecksum.equals(remoteChecksum);
	}
	

	
	private String getRemoteChecksum()
	{
		String res = "LOL";
		HttpURLConnection connection = null;
		
		try
		{
			URL url = new URL(checkSumFile);
			
				connection = (HttpURLConnection)url.openConnection();
			
			connection.setRequestMethod("GET");
			connection.connect();
			
		    BufferedReader in = new BufferedReader(new InputStreamReader(
                    connection.getInputStream()));
		    String inputLine;
		    while ((inputLine = in.readLine()) != null) 
		    	res = inputLine;
		    in.close();
			
		}catch(Exception ex){}

		
		return res;
		

	}
	
	
	private String getLocalChecksum()
	{
		
		File f = null;
		try {
			f = new File(Editor.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
		} catch (URISyntaxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		String res = "";
		

try
{
	MessageDigest md = MessageDigest.getInstance("SHA1");
    FileInputStream fis = new FileInputStream(f);
    byte[] dataBytes = new byte[1024];
    
    int nread = 0; 
    
    while ((nread = fis.read(dataBytes)) != -1) {
      md.update(dataBytes, 0, nread);
    };

    byte[] mdbytes = md.digest();
   
    //convert the byte to hex format
    StringBuffer sb = new StringBuffer("");
    for (int i = 0; i < mdbytes.length; i++) {
    	sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
    }
    
    
    res = sb.toString();
    fis.close();
    
}
catch(Exception e){}
	

		return res;
	}

}
