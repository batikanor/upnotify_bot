package utils;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.telegram.telegrambots.meta.api.objects.Update;

import objects.Request;
import upnotify_bot.UpdateReceiver;
import upnotify_bot.UpnotifyBot;
import upnotify_bot.UpnotifyReceiver;


interface MultiprocessingUtilsInterface {
	/**
	 * Returns the size of the thread pool which is potentially about to be created
	 * @param tpc THREAD_PER_CORE
	 * @return int corresponding to the size of the thread pool that is to be created
	 */
	public int getThreadPoolSize(int tpc);
	
	/**
	 * Returns the core count of cores within the processor
	 * @return Count of cores within the processor
	 */
	public int getCoreCount();
	
	/**
	 * Submits updates to the thread pool.
	 * @param ub bot instance
	 * @param update the whole update object
	 * 
	 */
	public void submitUpdate(UpnotifyBot ub, Update update);
	
	/**
	 * Submits upnotifies to their respective thread pool
	 * 
	 */
//	public void submitUpnotify();
}

/**
 * Handles the multiprocessing needs such as the thread pool structure beneath the bot
 * Has a private constructor and only one instance of it is allowed, therefore this is a singleton.
 *
 */
public class MultiprocessingUtils implements MultiprocessingUtilsInterface {
	
	private static MultiprocessingUtils single_instance = null;
	
	public static MultiprocessingUtils getMultiProcessingUtils() {
		
		if (single_instance == null) {
			single_instance = new MultiprocessingUtils();
			System.out.println("Instance of 'MultiprocessingUtils' has been created");
		}
		return single_instance;
	}
	/**
	 *  Runs new updates received
	 */
	private ExecutorService executor;
	/**
	 *  Handles already existing upnotify requests
	 */
	private ExecutorService upnotifyExecutor;
	/*
	 * 	Map to map request ids with threads, so we can find which request is running on which thread
	 * */
	private Map<Integer, UpnotifyReceiver> upnotifyMap = new HashMap<Integer, UpnotifyReceiver>();
	
	private MultiprocessingUtils() {
		// tps: thread pool size
		int tps_update = getThreadPoolSize(Config.getConfig().THREAD_PER_CORE_UPDATE);
		int tps_upnotify = getThreadPoolSize(Config.getConfig().THREAD_PER_CORE_UPNOTIFY);
		this.executor = Executors.newFixedThreadPool(tps_update);
		this.upnotifyExecutor = Executors.newFixedThreadPool(tps_upnotify);
		
	}
	
	/**
	 * Returns the size of the thread pool which is potentially about to be created
	 * @param tpc THREAD_PER_CORE
	 * @return int corresponding to the size of the thread pool that is to be created
	 */
	public int getThreadPoolSize(int tpc) {
		return getCoreCount() * tpc;
	}
	/**
	 * Returns the count of cores within the processor
	 * @return Count of cores within the processor
	 */
	public int getCoreCount() {
		return Runtime.getRuntime().availableProcessors();
	}
	
	/**
	 * Submits updates to the thread pool.
	 * @param ub reference to the bot instance
	 * @param update the whole update object
	 * 
	 */
	public void submitUpdate(UpnotifyBot ub, Update update) {
		System.out.println("Submitting the update to the thread pool");
		executor.submit(new UpdateReceiver(ub, update));
	}
	
	
	public void submitUpnotify(UpnotifyBot ub, Request upnotify) {
		System.out.println("Submitting the upnotify request with id " + upnotify.requestId + " to the thread pool");
		UpnotifyReceiver rc = new UpnotifyReceiver(ub, upnotify);
		upnotifyMap.put(upnotify.requestId, rc);
		upnotifyExecutor.submit(rc);
	}
	
	public void removeUpnotify(int requestId) {

		// if doesn't contain, probably inactive
		if (upnotifyMap.containsKey(requestId)){
		//	shutdown will not effect immediately
			upnotifyMap.get(requestId).shutdown();
			upnotifyMap.remove(requestId);
		}
		System.out.println("Upnotify Request with requestId " + requestId + " is removed");
	}
}
