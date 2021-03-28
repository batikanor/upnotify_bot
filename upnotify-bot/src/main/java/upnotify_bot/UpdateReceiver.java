package upnotify_bot;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import utils.MessageUtils;


/**
 * Receives the updates and handles them depending on the respective attributes of the updates.
 *
 * Instances of this class run on a thread taken from the thread pool, 
 * 
 *
 */
public class UpdateReceiver implements Runnable{
	private UpnotifyBot ub;
	private Update update;
	private Message msg;
	
	
	public UpdateReceiver(UpnotifyBot ub, Update update) {
		this.ub = ub;
		this.update = update;
	}

	
	/**
	 * Gets run on a thread from the pool, handles an update.
	 */
	public void run() {
		String threadId = Long.toString(Thread.currentThread().getId());

		//System.out.println(update);
		
		if (update.hasMessage()) {
			msg = update.getMessage();
			String chatId = msg.getChatId().toString();
			if (msg.hasText()) {
				String msgText = msg.getText();
				
				// Now, depending on the text we have, and maybe the current state of the situation of our conversation within the group (group id) or with the person (from id), we will handle the message
				

				// Direct text handling, without any importance being given to the conversation stance
				if (msgText.contentEquals("debugmsg".toLowerCase())) {
					while (!MessageUtils.getMessageUtils().sendDebugMessage(ub, threadId, chatId, update)) {
						// Logging is to be done within the MessageUtils class, so here printing out would suffice.
						System.out.println("Error whilst sending the message, trying again...");
					}
				
				} else if (msgText.startsWith("Check site".toLowerCase())) {
					MessageUtils.getMessageUtils().checkSiteHTTPResponse(ub, threadId, chatId, update.getMessage().getText().substring(10));
				}		
			}
		}	
	}
}