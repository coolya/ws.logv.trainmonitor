package ws.logv.trainmonintor.app;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.UUID;

public class Installation {
	private static final String FILENAME = "INSTALLATION";

	private static String ID;
	
	public static String Id()
	{
		if(ID == null)
		{
			File file = new File(FILENAME);
			
			if(file.exists())
			{
				try {
					ID = readFile(file);
				} catch (Exception e) {
					ID = null;
				}
			}
			else
			{
				String id = UUID.randomUUID().toString();
				try {
					file.createNewFile();
					writeFile(file, id.getBytes());
				} catch (Exception e) {
					ID = null;
				}
			}		 
		}		
		return ID;
	}
	
	private static void writeFile(File file, byte[] content) throws Exception
	{		
		FileOutputStream fos = new FileOutputStream(file);
		fos.write(content);
		fos.close();		
	}
	
	private static String readFile(File file) throws Exception
	{
		FileInputStream fis = new FileInputStream(file);
		byte[] buffer = new byte[255];
		fis.read(buffer);
		fis.close();
		return new String(buffer).trim();		
	}
	
	
}
