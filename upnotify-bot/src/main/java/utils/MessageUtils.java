package utils;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import upnotify_bot.UpnotifyBot;



public class MessageUtils {
	/**
	 * Answers with our debug message, containing thread info together with all the data from
	 * the update.
	 * @param ub Reference to our bot object.
	 * @param threadId ID of the thread within the pool.
	 * @param chatId ID of the chat that the debug message is to be sent to.
	 * @param update the update that is to be converted to string and printed.
	 * @return true if message had been sent successfully, false otherwise.
	 */
	public static boolean sendDebugMessage(UpnotifyBot ub, String threadId, String chatId, Update update) {
		String debugText = "ok\n" 
				+ "thread ID:" + threadId + "\n Message: \n"
				+ update;
		SendMessage debugMessage = new SendMessage(chatId, debugText); // Create a SendMessage object with mandatory fields
        try {
            ub.execute(debugMessage); 
            return true;
        } catch (TelegramApiException e) {
            //TODO logging
        	e.printStackTrace();
            return false;
    
        }
		
		
	}
}