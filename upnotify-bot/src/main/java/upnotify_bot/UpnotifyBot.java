package upnotify_bot;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import utils.MultiprocessingUtils;


public class UpnotifyBot extends TelegramLongPollingBot {
	String botToken;
	String botUsername;
	
	public UpnotifyBot() {
		System.out.println("Constructing the bot...");
		InputStream ins = ClassLoader.getSystemResourceAsStream("SECRET_KEYS/Keys.properties");
		Properties prop = new Properties();
		try {
			prop.load(ins);
			// If you are helping develop this bot, please ensure that you do not share the following private variables with anyone!
			// (the file including them is always to be ignored while pushing!)
			this.botToken = prop.getProperty("botToken");
			this.botUsername = prop.getProperty("botUsername");
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			System.out.println("Properties file couldn't be loaded");
			e.printStackTrace();
		}

		
	  
		

	}

	public void onUpdateReceived(Update update) {
		// Get the only instance of the MultiprocessingUtils class
		System.out.println("Received a new update!");
		MultiprocessingUtils mpu = new MultiprocessingUtils();
		mpu.submitUpdate(this, update);
		// TODO Auto-generated method stub
		
	}

	public String getBotUsername() {
		// TODO Auto-generated method stub
		return botUsername;
	}

	@Override
	public String getBotToken() {
		// TODO Auto-generated method stub
		return botToken;
	}

}
