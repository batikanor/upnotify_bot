
package upnotify_bot;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

/**
 * Main class of the upnotify-bot project, here the telegram bot API will be initialized, bot will be initialized and registered.
 */
public class Main {
	
	// If the bot becomes widely used,raise this multiplier
	public static final int THREAD_PER_CORE = 10; 


	/**
	 * The main method that eventually makes the whole bot function
	 * @param args input arguments
	 */
	public static void main(String[] args) {
		
		
		// Instantiate the TelegramBots API by RubenLagus		
        TelegramBotsApi telegramBotsApi;

        // Register the bot
		try {
			telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
			telegramBotsApi.registerBot(new UpnotifyBot());
		} catch (TelegramApiException e) {
			// TODO logging
			e.printStackTrace();
		}

	}

}
