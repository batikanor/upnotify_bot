package utils;


import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Instant;
import java.util.ArrayList;

import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;

import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import objects.Request;
import objects.User;
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
			System.out.println("Thread " + threadId + " is Waiting for " + (float)Config.getConfig().WAIT_UNTIL_MESSAGE_DELETE / 1000 + " seconds.");
			
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
//	
//	public boolean insertRequest() {
//		
//	}
	public boolean sendWelcomeMessage(UpnotifyBot ub, String threadId, String chatId, Update update) {
		SendPhoto sp = new SendPhoto();
		sp.setChatId(chatId);
		File file;
		URL url = this.getClass().getClassLoader().getResource("IMAGES/welcome-red-sign-760.png");
		try {
			file = new File(url.toURI());
			sp.setPhoto(new InputFile(file));
		} catch (URISyntaxException e) {
			file = new File(url.getPath());
		} finally {
			//sp.setPhoto(new InputFile( new File ("src/main/resources/IMAGES/welcome-red-sign-760.png")));
			try {
				ub.execute(sp);
			} catch (TelegramApiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				return false;
			}
		}
		return true;

	}
	/**
	 * only some html tags are available, take a look at https://core.telegram.org/bots/api ctrl+f HTML ()
	 * @param ub
	 * @param chatId
	 * @param update
	 * @param upUser
	 */
	public void sendHelpMessage(UpnotifyBot ub, String chatId, Update update, objects.User upUser) {
		// TODO Auto-generated method stub
		
		StringBuilder sb = new StringBuilder();
		sb.append("<b> Your Info </b>");
		sb.append("\n<i>"
				+ "Your level is: <b>" + upUser.checkLevel + "</b> and with that, you can "
				+ "have your upnotify request run once every "
				+ Config.getConfig().MIN_WAIT_LEVEL[upUser.checkLevel] + " minutes or less often if you request so."
				+ "</i>");
		sb.append("\n\n");
		sb.append("\n<b>Bot Info </b>");
		sb.append("\n<u>Commands and resp. Functionalities</u>");
		sb.append("\n<i>(Replace the strings starting with '$' with your own inputs.)</i>");
		sb.append("\n");
		sb.append("\n<code>/help</code> -> <i>Shows help message</i>");
		sb.append("\n<code>/addrequest $url ss sch</code> -> <i>Adds snapshot with given URL </i>");
		sb.append("\n<i>ss = parameter to check for updates by comparing screenshots of given URL  </i>");
		sb.append("\n<i>sch = checks for updates by comparing site content hash of given URL </i>");
		sb.append("\n<code>/addrequest $url ss sch</code> -> <i>Adds snapshot with given URL </i>");
		sb.append("\n<code>/editrequest requestId $url ss sch</code> -> <i>Edits request with given requestId</i> ");
		sb.append("\n<code>/removerequest requestId</code> -> <i>Removes request with given requestId</i> ");
		sb.append("\n<code>/togglerequest requestId</code> -> <i>Toggles activity status of request with given requestId</i> ");
		sb.append("\n<code>/seerequests</code> -> <i>Lists current requests </i> ");
		sb.append("\n<code>/checksite $url</code> -> <i>Checks if a site is reachable or not</i> ");
		sb.append("\n<code>/checkstatic $url</code> -> <i>Checks if a site is static or not</i> ");
		sb.append("\n\n");
		sb.append("\n<u>Latest Updates</u>");
		sb.append("\n<code>Addded '/togglerequest'</code> -> <i>27.06.2021</i> ");
		String helpText = sb.toString();
		System.out.println(helpText);
				
		//		String helpText = "Hi\\!"
//				+ "# \n\n__YOUR INFO__\n"
//					+ "Your level is: " + upUser.checkLevel + "and with that, you can"
//						+ " have your upnotify request run once every "
//						+ Config.getConfig().MIN_WAIT_LEVEL[upUser.checkLevel] + " minutes or less often if you request.\n"
//				+ "# \n\n_BOT INFO_\n"
//					+ "* This bot cares about your privacy. Bot has access to all *private* messages _that you send to it directly_ but in groups, this bot has no access to "
//					+ "messages that don't start with a '/' (messages that are not commands) + to learn more read following: {INSERT_TELEGRAPH_LINK_HERE} "
//					+ "\n This bot will notify you for changes in web pages or web page sections in determined intervals."
//				+ "# \n\n_COMMANDS_"
//					+ "\n/msginfo foo	->	Sends all the info that the bot receives with any message you send it, so that you can know how much of your information is seen by the bot."
//					+ "\n ...blabla"
//				+ "# \n\n_OTHER COMMUNICATION_"
//					+ "hi	->	Bot will send you a welcome photo";
		SendMessage sm = new SendMessage(chatId, helpText);
		sm.setParseMode(ParseMode.HTML);
		try {
			ub.execute(sm);
		} catch (TelegramApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
				
		
	}
	public void addRequestAndSendConfirmation(UpnotifyBot ub, String chatId, Update update, User upUser,
			ArrayList<String> args) {
		objects.Snapshot snap = new objects.Snapshot();
		snap.screenshot = null;
		snap.siteContentHash = null;
		snap.url = args.get(0);
		
		if (args.contains("ss")) {
			// get screenshot
			System.out.println("Request will have a non-null screenshot field!");
			snap.screenshot = WebUtils.getWebUtils().getScreenshotUsingSelenium(snap.url);
		
			
		}
		if (args.contains("sch")) {
			System.out.println("Request will have a non-null siteContentHash field!");
			snap.siteContentHash = WebUtils.getWebUtils().getHTMLBodyStringHash(snap.url);
		}

		
		
		// Requests.telegramId,   Requests.LastCheckUnix, Snapshot.url, Snapshot.screenshot, Snapshot.siteContentHash
		
		boolean success = DatabaseUtils.getDatabaseUtils().addRequest(update.getMessage().getChatId(), Instant.now().getEpochSecond(), snap.url, snap.screenshot, snap.siteContentHash);
		String txt;
		if (success) {
			txt = "Request has been added!";
		} else {
			txt = "An error occured, please try again later";
		}
		SendMessage sm  = new SendMessage();
		sm.setChatId(chatId);
		sm.setText(txt);
		try {
			ub.execute(sm);
		} catch (TelegramApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
    public void sendWarningMessage(UpnotifyBot ub, String threadId, String chatId, int messageId) {
		String txt = "You used that command incorrectly! please refer to '/help'";

		SendMessage sm  = new SendMessage();
		sm.setChatId(chatId);
		sm.setText(txt);
		sm.setReplyToMessageId(messageId);
		try {
			ub.execute(sm);
		} catch (TelegramApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }
    public void sendNotificationMessage(UpnotifyBot ub, Long telegramId, String notificationTxt, BufferedImage notificationIm) {
    	SendMessage sm = new SendMessage();
		sm.setChatId(telegramId.toString());
		sm.setText(notificationTxt);
		Message m = null;
		
		try {
			m = ub.execute(sm);
		} catch (TelegramApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (notificationIm != null) {
			SendDocument sd = new SendDocument();
			sd.setChatId(telegramId.toString());
			sd.setReplyToMessageId(m.getMessageId());
			sd.setDocument(new InputFile(ImageUtils.getImageUtils().convertBufferedImageIntoInputStream(notificationIm), "Difference Image.png"));
			try {
				ub.execute(sd);
			} catch (TelegramApiException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


	}
	
    public void editRequest(UpnotifyBot ub, String chatId, User upUser, ArrayList<String> args) {
		
		Request req = DatabaseUtils.getDatabaseUtils().retrieveRequestFromId(Integer.parseInt(args.get(0)));
		
		SendMessage sm = new SendMessage();
		sm.setChatId(chatId);
		String reply = "";
		
		if(req == null) {
			reply = "No such request";
		}
		else if(req.telegramId - upUser.telegramId != 0) {
			System.out.println("Request id " + req.telegramId);
			System.out.println("In contrary to user id " + upUser.telegramId);
			reply = "This request is not yours";
		}
		else {
			objects.Snapshot snap = new objects.Snapshot();
			snap.snapshotId = req.snapshotId;
			snap.screenshot = null;
			snap.siteContentHash = null;
			snap.url = args.get(1);
			
			if (args.contains("ss")) {
				System.out.println("Request will have a non-null screenshot field!");
				snap.screenshot = WebUtils.getWebUtils().getScreenshotUsingSelenium(snap.url);
			}
			
			if (args.contains("sch")) {
				System.out.println("Request will have a non-null siteContentHash field!");
				snap.siteContentHash = WebUtils.getWebUtils().getHTMLBodyStringHash(snap.url);
			}
			
			boolean success = DatabaseUtils.getDatabaseUtils().editRequest(req,snap);
			
			if(success) {
				reply = "Your request has been successfully edited";
				//Remove the old upnotify and submit the new one
				MultiprocessingUtils.getMultiProcessingUtils().removeUpnotify(req.requestId);
				MultiprocessingUtils.getMultiProcessingUtils().submitUpnotify(ub, req);
			}
			else
				reply = "Something went wrong while editing your request";
			
		}
		
		sm.setText(reply);
		
		try {
			ub.execute(sm);
		} catch (TelegramApiException e) {
			e.printStackTrace();
		}
	}
    public void seeRequests(UpnotifyBot ub, String chatId, User upUser, Integer msgIdToReply) {
		SendMessage sm = new SendMessage();
		sm.setChatId(chatId);
		sm.setReplyToMessageId(msgIdToReply);
		String txt = "Below is a list of your requests: \n";
		System.out.println(upUser.telegramId);
		
		ArrayList<Request> requests = DatabaseUtils.getDatabaseUtils().getRequestsFromTelegramId(upUser.telegramId);
		
		for (Request req : requests) {
			System.out.println(req.toString());
			txt += "\n" + req.toString();
			
		}
		if (requests.isEmpty()) {
			txt += "You don't have any requests";
		}
		sm.setText(txt);
		try {
			ub.execute(sm);
		} catch (TelegramApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public boolean removeRequest(UpnotifyBot ub, String chatId, User upUser, Integer msgIdToReply, String arg) {
		// TODO Auto-generated method stub
		SendMessage sm = new SendMessage();
		sm.setChatId(chatId);
		sm.setReplyToMessageId(msgIdToReply);
		String msgText = "";
		boolean isNum = false;
		int num = -1;
		try{
			  num = Integer.parseInt(arg);
			  isNum = true;
		} catch (NumberFormatException e) {
			  isNum = false;
		}

		if(isNum) {
			// if (DatabaseUtils.getDatabaseUtils().retrieveRequestFromId(num).telegramId == upUser.telegramId) {
				if (DatabaseUtils.getDatabaseUtils().removeRequestFromId(num, upUser.telegramId)) {
					msgText += "Your request with ID=" + arg + " has been succesfully removed!";
				} else {
					msgText += "There has been an error, your request couldn't be removed. Maybe it doesn't exist at all.. Please try '/seerequests' "; 
				}
				
			// } else {
				// msgText += "This Request is not yours.";
				
			// }
		}else{
			msgText += "The arguments have to be numeric for this command! Please try again";
		}
		
		sm.setText(msgText);
		
		try {
			ub.execute(sm);
		} catch (TelegramApiException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		return true;
	
		
	}
	public boolean toggleRequest(UpnotifyBot ub, String chatId, User upUser, Integer msgIdToReply, String arg) {
		
		SendMessage sm = new SendMessage();
		sm.setChatId(chatId);
		sm.setReplyToMessageId(msgIdToReply);
		String msgText;

		boolean isNum = false;
		int num = -1;
		try{
			  num = Integer.parseInt(arg);
			  isNum = true;
		} catch (NumberFormatException e) {
			  isNum = false;
		}
	
		if (isNum) {
			if (DatabaseUtils.getDatabaseUtils().toggleRequestFromId(num, upUser.telegramId)){
				msgText = "The activity status of your request with ID=" + arg + " has been succesfully toggled! Use '/seerequests' to see the current stance.";
			} else {
				msgText = "There has been an error, your request couldn't be toggled. Maybe it doesn't exist at all.. Please try '/seerequests' "; 
			}
				
		} else {
			msgText = "The arguments have to be numeric for this command! Please try again";
		}
		sm.setText(msgText);
		
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