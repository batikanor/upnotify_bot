package utils;

import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;

/**
 * This class is constructed only once, and holds variables that correspond to the contents of the Config.properties file.
 * 
 *
 */
public class Config {
	
	enum OS {
		WIN,
		LINUX
	}
	
	// These are all declared as final, so they all MUST be initialized within the constructor.
	
	public final int THREAD_PER_CORE_UPNOTIFY;
	public final int THREAD_PER_CORE_UPDATE;
	public final int WAIT_UNTIL_MESSAGE_DELETE;
	public final int WAIT_STATIC_CHECK;
	public final int WAIT_UNTIL_ERR_MESSAGE_RESEND;
	public final float IMAGE_DIFFERENCE_THRESHOLD;
	public final String PUBLIC_UBLOCK_KEY;
	public final int CHROME_DRIVER_VER;
	public final int DEFAULT_LEVEL;
	public final int[] MIN_WAIT_LEVEL;

	public final String DATABASE_ENGINE;

	
	
	
	public final OS os; 
	
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
		try {
			prop.load(ins);

		} catch (Exception e) {
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
		
		this.THREAD_PER_CORE_UPDATE = Integer.parseInt(prop.getProperty("THREAD_PER_CORE_UPDATE"));
		this.THREAD_PER_CORE_UPNOTIFY = Integer.parseInt(prop.getProperty("THREAD_PER_CORE_UPNOTIFY"));
		this.WAIT_UNTIL_MESSAGE_DELETE = Integer.parseInt(prop.getProperty("WAIT_UNTIL_MESSAGE_DELETE"));
		this.WAIT_STATIC_CHECK = Integer.parseInt(prop.getProperty("WAIT_STATIC_CHECK"));
		this.WAIT_UNTIL_ERR_MESSAGE_RESEND = Integer.parseInt(prop.getProperty("WAIT_UNTIL_ERR_MESSAGE_RESEND"));
		this.IMAGE_DIFFERENCE_THRESHOLD = Float.parseFloat(prop.getProperty("IMAGE_DIFFERENCE_THRESHOLD"));
		this.PUBLIC_UBLOCK_KEY = prop.getProperty("PUBLIC_UBLOCK_KEY");
		this.CHROME_DRIVER_VER = Integer.parseInt(prop.getProperty("CHROME_DRIVER_VER"));
		this.DATABASE_ENGINE = prop.getProperty("DATABASE_ENGINE");
		this.DEFAULT_LEVEL = Integer.parseInt(prop.getProperty("DEFAULT_LEVEL"));
		this.MIN_WAIT_LEVEL = new int[]{
				Integer.parseInt(prop.getProperty("MIN_WAIT_LEVEL_0")),
				Integer.parseInt(prop.getProperty("MIN_WAIT_LEVEL_1")),
				Integer.parseInt(prop.getProperty("MIN_WAIT_LEVEL_2")),
				Integer.parseInt(prop.getProperty("MIN_WAIT_LEVEL_3")),
				Integer.parseInt(prop.getProperty("MIN_WAIT_LEVEL_4")),
				Integer.parseInt(prop.getProperty("MIN_WAIT_LEVEL_5"))
		};
	
		System.out.println();
		//this.os = prop.getProperty("OS").toLowerCase().contentEquals("linux") ? OS.LINUX : OS.WIN;
		String osNameProp = prop.getProperty("OS").toLowerCase();
		String osNameJava = System.getProperty("os.name").toLowerCase();
		String chosenOs;
		if (osNameProp.startsWith(osNameJava.substring(0, 3))) {
			chosenOs = osNameProp;
		}
		else {
			System.out.println("Your Config.properties file states the operation system as: \n" + 
		osNameProp + 
		"\nbut the program has detected your operation system to be: \n" +
		osNameJava + 
		"\nThe program will work with the program-detected OS by default. \n" + 
		"Input 'p' if you would like to enforce the program to use the OS stated in the properties file.\n" +
		"Otherwise just press enter.");	
		
			Scanner sc = new Scanner(System.in);
			chosenOs = sc.nextLine().startsWith("p") ? osNameProp : osNameJava;
			sc.close();
		}
		this.os = chosenOs.startsWith("lin") ? OS.LINUX : OS.WIN;
		
	}
}
