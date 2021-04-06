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
	private String command;
	private String[] args;
	
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
				
				// Commands
				if (msgText.startsWith("/")) {
					
					command = msgText.substring(1, msgText.indexOf(" ")).toLowerCase();
					args = msgText.substring(2 + command.length()).split(" ");
					
					switch (command) {
						case "msginfo":
							while (!MessageUtils.getMessageUtils().sendDebugMessage(ub, threadId, chatId, update)) {
								// Logging is to be done within the MessageUtils class, so here printing out would suffice.
								System.out.println("Error whilst sending the message, trying again...");
							}
						case "checksite":
							for (String arg : args) {
								System.out.println("Working with argument: " + arg);
								// Note that a single thread will work with all of them. If we ever want to change this, we could do these controls within OnUpdateReceived function of UpnotifyBot class, or we could have a separate class for these, and the assignment of jobs to threads could be later etc..
								MessageUtils.getMessageUtils().checkSiteHTTPResponse(ub, threadId, chatId, arg);
							}
						case "checkstatic":
							for (String arg : args) {
								MessageUtils.getMessageUtils().checkIfHTMLBodyStatic(ub, chatId, arg);
							}
							
						case "donothing":
							break;
						
					}
			
					
					
				
				}
				
			
			
				
				
				
				
				
				
			}
		}	
	}
}
