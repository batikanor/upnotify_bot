package upnotify_bot;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import utils.MessageUtils;



public class UpdateReceiver implements Runnable{
	private UpnotifyBot ub;
	private Update update;
	private Message msg;
	
	
	public UpdateReceiver(UpnotifyBot ub, Update update) {
		// TODO Auto-generated constructor stub
		this.ub = ub;
		this.update = update;
		
		
	
	}


	public void run() {
		String threadId = Long.toString(Thread.currentThread().getId());
		
		
		//System.out.println(update);
		
		if (update.hasMessage()) {
			Message msg = update.getMessage();
			String chatId = msg.getChatId().toString();
			if (msg.hasText()) {
				String msgText = msg.getText();
				
				// Now, depending on the text we have, and maybe the current state of the situation of our conversation within the group (group id) or with the person (from id), we will handle the message
				
				
				// Direct text handling, without any importance being given to the conversation stance
				if (msgText.contentEquals("debugmsg")) {
					while (!MessageUtils.sendDebugMessage(ub, threadId, chatId, update)) {
						// Logging is to be done within the MessageUtils class, so here printing out would suffice.
						System.out.println("Error whilst sending message, trying again...");
					}
				}

				
				
				
				
			}
		}
		
	}
}
