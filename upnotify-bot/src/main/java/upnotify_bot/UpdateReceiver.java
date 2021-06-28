package upnotify_bot;


import java.util.ArrayList;
import java.util.Arrays;

import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import utils.Config;
import utils.DatabaseUtils;
//import objects.User;
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
	//private String[] args;
	private ArrayList<String> args = new ArrayList<String>();
	
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
				
				User user = msg.getFrom();
				objects.User upUser;
				if (msg.getChat().getType().contentEquals("private")) {
					upUser = DatabaseUtils.getDatabaseUtils().retrieveUserFromId(user.getId(), user.getUserName());
				} else {
					upUser =  DatabaseUtils.getDatabaseUtils().retrieveUserFromId(msg.getChatId(), msg.getChat().getTitle());
				}
				
				
				String msgText = msg.getText();
				System.out.println("Received text: " + (msgText.length() > 20 ? msgText.subSequence(0, 19)  + "..." : msgText));

				

				/**
				 * The following block will be entered if the message received is a command.
				 * Commands can come in forms such as:
				 *	/msginfo@upnotify_bot
				 *	/msginfo
				 *	/msginfo hey heyyy
				 */
				if (msgText.startsWith("/")) {
					
					boolean withArgs = msgText.contains(" ");
					
	
					command = withArgs ? msgText.substring(1, msgText.indexOf(" ")).toLowerCase() : msgText.substring(1);					
					command = command.replace("@" + ub.botUsername, "");
					System.out.println("Running command: " + command);
					if (withArgs){
						args.addAll(Arrays.asList(msgText.substring(2 + command.length()).split(" ")));
						System.out.println("For args: " + args.toString());
					}
					
					//args = new ArrayList<String>(Arrays.asList(withArgs ? (msgText.substring(2 + command.length()).split(" ")) : null));
					
				
					
					switch (command) {
			
						case "msginfo":
							while (!MessageUtils.getMessageUtils().sendDebugMessage(ub, threadId, chatId, update)) {
								// Logging is to be done within the MessageUtils class, so here printing out would suffice.
								System.out.println("Error whilst sending the message, trying again...");
								try {
									Thread.sleep(Config.getConfig().WAIT_UNTIL_ERR_MESSAGE_RESEND);
								} catch (InterruptedException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
							}
							break;
						case "checksite":
							if (args.isEmpty()) {
								MessageUtils.getMessageUtils().sendWarningMessage(ub, threadId, chatId, msg.getMessageId());
							}
							for (String arg : args) {
								System.out.println("Working with argument: " + arg);
								// Note that a single thread will work with all of them. If we ever want to change this, we could do these controls within OnUpdateReceived function of UpnotifyBot class, or we could have a separate class for these, and the assignment of jobs to threads could be later etc..
								MessageUtils.getMessageUtils().checkSiteHTTPResponse(ub, threadId, chatId, arg);
							}
							break;
						case "checkstatic":
							if (args.isEmpty()) {
								MessageUtils.getMessageUtils().sendWarningMessage(ub, threadId, chatId, msg.getMessageId());
							}
							for (String arg : args) {
								System.out.println("Working with argument: " + arg);
								MessageUtils.getMessageUtils().checkIfHTMLBodyStatic(ub, chatId, arg);
							}
							break;
						case "help":
							
							MessageUtils.getMessageUtils().sendHelpMessage(ub, chatId, update, upUser);
							break;
							
						case "donothing":
							break;
						case "addrequest":
							// /addrequest snapUrl ss sch
							if (args.isEmpty()) {
								MessageUtils.getMessageUtils().sendWarningMessage(ub, threadId, chatId, msg.getMessageId());
							}

							MessageUtils.getMessageUtils().addRequestAndSendConfirmation(ub, chatId, update, upUser, args);
							break;
						case "editrequest":
						if (args.isEmpty()) {
							MessageUtils.getMessageUtils().sendWarningMessage(ub, threadId, chatId, msg.getMessageId());
						}
							// /editrequest requestId newURL sch ss
							// sch yaziyorsa sch yi kontrol edip kaydeder db ye, yazmiyorsa oraya null yazar
							MessageUtils.getMessageUtils().editRequest(ub, chatId, upUser, args);
							break;
						case "seerequests":
							MessageUtils.getMessageUtils().seeRequests(ub, chatId, upUser, msg.getMessageId());
							// see requests, fields and request ids
							break;
						case "removerequest":
						if (args.isEmpty()) {
							MessageUtils.getMessageUtils().sendWarningMessage(ub, threadId, chatId, msg.getMessageId());
						}
							for (String arg : args) {
								System.out.println("Working with argument: " + arg);
								MessageUtils.getMessageUtils().removeRequest(ub, chatId, upUser, msg.getMessageId(), arg);
							}
							break;
						case "togglerequest":
						if (args.isEmpty()) {
							MessageUtils.getMessageUtils().sendWarningMessage(ub, threadId, chatId, msg.getMessageId());
						}
							for (String arg : args) {
								System.out.println("Working with argument: " + arg);
								MessageUtils.getMessageUtils().toggleRequest(ub, chatId, upUser, msg.getMessageId(), arg);
							}
							
					}
				} else {
					switch (msgText) {
					// Direct text handling, without any importance being given to the conversation stance

					case "hi":
						while(!MessageUtils.getMessageUtils().sendWelcomeMessage(ub, threadId, chatId, update)) {
							System.out.println("Error whilst sending the message, trying again...");
							try {
								Thread.sleep(Config.getConfig().WAIT_UNTIL_ERR_MESSAGE_RESEND);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				}
			}
		}	
	}
}
