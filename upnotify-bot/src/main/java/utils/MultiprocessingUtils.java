package utils;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.telegram.telegrambots.meta.api.objects.Update;


import upnotify_bot.Main;
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
}

public class MultiprocessingUtils implements MultiprocessingUtilsInterface {
	public static MultiprocessingUtils single_instance = null;
	
	public static MultiprocessingUtils getMultiProcessingUtils() {
		if (single_instance == null) {
			single_instance = new MultiprocessingUtils();
			System.out.println("Instance of mpu has been created");
		}
		return single_instance;
		
	}
	
	private ExecutorService executor;
	
	public MultiprocessingUtils() {
		this.executor = Executors.newFixedThreadPool(getThreadPoolSize(Main.THREAD_PER_CORE));
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
	
	
	public void submitUpdate(UpnotifyBot ub, Update update) {
		executor.submit(new UpdateReceiver(ub, update));
	}
	
	

}
