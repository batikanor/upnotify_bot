package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


/**
 * This class is constructed only once, and holds variables that correspond to the contents of the Config.properties file.
 * 
 *
 */
public class Config {
	
	// These are all declared as final, so they all MUST be initialized within the constructor.
	
	public final int THREAD_PER_CORE;
	public final int WAIT_UNTIL_MESSAGE_DELETE;
	
	private static Config single_instance = null;
	
	public static Config getConfig(){
		
		if (single_instance == null) {

			single_instance = new Config();
			System.out.println("Instance of 'Config' has been created");
		}
		
		return single_instance;
		
	}
	
	private Config() {
		
		InputStream ins = ClassLoader.getSystemResourceAsStream("CONFIGURATION/Config.properties");

		Properties prop = new Properties();
		while (true) {
			try {
				prop.load(ins);
	
				break;
				
			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				System.out.println("Properties file couldn't be loaded");
				e.printStackTrace();
				
				try { // wait a sec
					Thread.sleep(1000);
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} 
				
			}
		}

		this.THREAD_PER_CORE = Integer.parseInt(prop.getProperty("THREAD_PER_CORE"));
		this.WAIT_UNTIL_MESSAGE_DELETE = Integer.parseInt(prop.getProperty("WAIT_UNTIL_MESSAGE_DELETE"));

	}
}
