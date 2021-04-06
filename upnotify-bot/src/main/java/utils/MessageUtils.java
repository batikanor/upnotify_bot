package utils;


import java.net.HttpURLConnection;
import java.net.URL;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import upnotify_bot.UpnotifyBot;


/**
 * Handles the functions with which telegram messages are sent. Kind of like 'front end'
 * 
 */
public class MessageUtils {
	
	private static MessageUtils single_instance = null;
	
	public static MessageUtils getMessageUtils() {
		if (single_instance == null) {
			single_instance = new MessageUtils();
			System.out.println("Instance of 'MessageUtils' has been created");
		}
		return single_instance;
		
	}
	// Has only a private constructor, so that only one instance can exist
	private MessageUtils() {}
	
	
	/**
	 * Answers with our debug message, containing thread info together with all the data from
	 * the update. Removes the debug message in @WAIT_UNTIL_MESSAGE_DELETE seconds
	 * @param ub Reference to our bot object.
	 * @param threadId ID of the thread within the pool.
	 * @param chatId ID of the chat that the debug message is to be sent to.
	 * @param update the update that is to be converted to string and printed.
	 * @return true if message had been sent successfully, false otherwise.
	 */
	public boolean sendDebugMessage(UpnotifyBot ub, String threadId, String chatId, Update update) {
		String debugText = "ok\n" 
				+ "thread ID:" + threadId + "\n Message: \n"
				+ update;
		SendMessage debugMessage = new SendMessage(chatId, debugText); // Create a SendMessage object with mandatory fields
		Message mg;
	
		try {
			mg = ub.execute(debugMessage); 
        } catch (TelegramApiException e) {
            //TODO logging
        	e.printStackTrace();
            return false;
        }
		
		// wait for given time
		try {
			System.out.println("Thread " + threadId + " is Waiting for " + Config.getConfig().WAIT_UNTIL_MESSAGE_DELETE + " seconds.");
			
			Thread.sleep(Config.getConfig().WAIT_UNTIL_MESSAGE_DELETE);
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
        
		// remove debug message
		DeleteMessage dm = new DeleteMessage();
		dm.setChatId(chatId);
		dm.setMessageId(mg.getMessageId());
        try {
			ub.execute(dm); // executeAsync is similar to execute, but doesn't validate if the message has arrived to telegram.. e.g. we don't really care if it is actually removed here...
		} catch (TelegramApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        // remove the debugmsg command message
        dm.setMessageId(update.getMessage().getMessageId());
        try {
			ub.execute(dm);
		} catch (TelegramApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        return true;
	}
	
	/**
	 * @TODO MOVE THE WEB RELATED CODE TO A DIFFERENT CLASS
	 * @PROBLEM https://tau.edu.tr doesn't work cuz it is not signed etc
	 * @return
	 */
	public boolean checkSiteHTTPResponse(UpnotifyBot ub, String threadId, String chatId, String url){
		String code = WebUtils.getWebUtils().getHTTPResponseFromUrl(url);
		SendMessage sm = new SendMessage(chatId, "Response code for " + url + " is as follows: " + code);
		try {
			ub.execute(sm);
		} catch (TelegramApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	
	}
	
	public boolean checkIfHTMLBodyStatic(UpnotifyBot ub, String chatId, String url){
		WebUtils wu = WebUtils.getWebUtils();
		String body = wu.getHTMLBodyStringFromUrl(url);
		try {
			Thread.sleep(Config.getConfig().WAIT_STATIC_CHECK);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String bodyNew = wu.getHTMLBodyStringFromUrl(url);
		
		
		
		SendMessage sm = new SendMessage(chatId, 
				String.valueOf(body.contentEquals(bodyNew)));
		try {
			ub.execute(sm);
		} catch (TelegramApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		return true;
		
		
		
	}
	
	
	
	
}