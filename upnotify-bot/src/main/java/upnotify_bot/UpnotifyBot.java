package upnotify_bot;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import objects.Request;
import utils.DatabaseUtils;
import utils.MultiprocessingUtils;

/**
 * The long polling bot that takes updates implicitly from the (http) telegram bot api.
 *
 */

public class UpnotifyBot extends TelegramLongPollingBot {
	String botToken;
	String botUsername;
	private static UpnotifyBot single_instance = null;
	
	/**
	 * Constructor for the bot class, also handles the private keys file and initializes its variables from it.
	 */
	private UpnotifyBot() {
		System.out.println("Constructing the bot...");
		InputStream ins = ClassLoader.getSystemResourceAsStream("SECRET_KEYS/Keys.properties");
		Properties prop = new Properties();
		try {
			prop.load(ins);
			// If you are helping develop this bot, please ensure that you do not share the following private variables with anyone!
			// (the file including them is always to be ignored while pushing!)
			this.botToken = prop.getProperty("botToken");
			this.botUsername = prop.getProperty("botUsername");
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("Properties file couldn't be loaded");
			e.printStackTrace();
		}

		// Get active upnotify request list from db
		System.out.println("Getting upnotify requests from the database");
		ArrayList<Request> upnotifies = DatabaseUtils.getDatabaseUtils().getRequests();
		for (Request upnotify : upnotifies) {
			if (upnotify.isActive){
				System.out.println("Processing the request with id: " + upnotify.requestId);
				MultiprocessingUtils.getMultiProcessingUtils().submitUpnotify(this, upnotify);
			}

		}
		
		// Remove unbounded snapshots that haven't been removed although their resp. request has been removed
		
		ArrayList<Integer> reqSnapIds = new ArrayList<Integer>();
		for (Request upnotify : upnotifies) {
			reqSnapIds.add(upnotify.snapshotId);
		}


		ArrayList<Integer> snapIds = DatabaseUtils.getDatabaseUtils().getSnapshotIds();

		Set reqSnapIdsSet = new HashSet<Integer>(reqSnapIds);
		Set snapIdsSet = new HashSet<Integer>(snapIds);

		//relative complement of setA in setB
		Set<Integer> differenceSet = new HashSet<Integer>(snapIdsSet);
		differenceSet.removeAll(reqSnapIdsSet);
		System.out.println("difference"  + differenceSet);
		for (int snapId : differenceSet) {
			DatabaseUtils.getDatabaseUtils().removeSnapshotFromId(snapId);
		}




				
		
		
		

	}
	public static UpnotifyBot getUpnotifyBot(){
		
		if (single_instance == null) {

			single_instance = new UpnotifyBot();
			System.out.println("Instance of 'UpnotifyBot' has been created");
		}
		return single_instance;
	}

	/**
	 * This method gets called whenever an update is received.
	 * Then the mpu unit will be used to submit the updates to respective threads within the pool.
	 */
	@Override
	public void onUpdateReceived(Update update) {
		// Get the only instance of the MultiprocessingUtils class
		System.out.println("Received a new update!");
		MultiprocessingUtils.getMultiProcessingUtils().submitUpdate(this, update);	
	}

	/**
	 * Returns the bot username.
	 */
	@Override
	public String getBotUsername() {
		// TODO Auto-generated method stub
		return botUsername;
	}

	/**
	 * Returns the bot token.
	 */
	@Override
	public String getBotToken() {
		// TODO Auto-generated method stub
		return botToken;
	}

}
