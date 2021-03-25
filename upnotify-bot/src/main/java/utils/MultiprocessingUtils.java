package utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.telegram.telegrambots.meta.api.objects.Update;

import upnotify_bot.UpdateReceiver;
import upnotify_bot.UpnotifyBot;


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
	
	private ExecutorService executor;
	
	private MultiprocessingUtils() {
		int tps = getThreadPoolSize(Config.getConfig().THREAD_PER_CORE);
		this.executor = Executors.newFixedThreadPool(tps);
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
	 * Returns the core count of cores within the processor
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
		System.out.println("Submtitting the update to the thread pool");
		executor.submit(new UpdateReceiver(ub, update));
	}
}
