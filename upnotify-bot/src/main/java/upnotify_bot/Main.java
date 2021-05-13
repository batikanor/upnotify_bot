
package upnotify_bot;

import java.util.ArrayList;


import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import objects.Request;
import utils.DatabaseUtils;
import utils.MultiprocessingUtils;


/**
 * Main class of the upnotify-bot project, here the telegram bot API will be initialized, bot will be instantiated and registered.
 */


public class Main {

	/**
	 * The main method that eventually makes the whole bot function
	 * @param args input arguments
	 */
	public static void main(String[] args) {
		
		// Instantiate the TelegramBots API by RubenLagus, then register the bot
		try {
			TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
			telegramBotsApi.registerBot(new UpnotifyBot());
			
		} catch (TelegramApiException e) {
			// TODO logging
			e.printStackTrace();
		}
		
		// Get active upnotify request list from db
		System.out.println("Getting upnotify requests from the database");
		ArrayList<Request> upnotifies = DatabaseUtils.getDatabaseUtils().getRequests();
		for (Request upnotify : upnotifies) {
			System.out.println("Processing the request with id: " + upnotify.requestId);
			MultiprocessingUtils.getMultiProcessingUtils().submitUpnotify(upnotify);
		}
		
	
		

	}

}
