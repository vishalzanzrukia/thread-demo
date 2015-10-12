package executors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import executors.ConcurrencyDemo.Sites;



/**
 * One client (associated with Stanford University) is interested to give me Java Development work by reference by only LinkedIn.
 * 
 * This class will demonstrate the usage of multi-threading concept with the real world example.<BR>
 * <B>Goal : </B>Minimize the waiting/blocking time to complete the all schedules parallel tasks.<BR>
 * <B>Tasks : </B>To download the list of sites/pages in parallel.<BR>
 * <B>Notes</B> : <BR>
 * <B>1 : </B>We will not download the any of page/site actually, <BR>
 * but will we will just block the current thread using #{@link Thread#sleep(long)} for specified download time at #{@link Sites}.<BR>
 * <B>2 : </B>Please do not expect proper exception handling in this, example. Our goal here is something else.
 * 
 * @author vishal.zanzrukia
 *
 */
public class ConcurrencyDemo {

	
	/**
	 * This will indicate the mode in which main method will execute the problem to demonstrate the feature.
	 * 
	 * @author vishal.zanzrukia
	 *
	 */
	enum TestSuite{
		
		JAVA_7_BLOCKING,
		JAVA_7_NON_BLOCKING,
		JAVA_8_NON_BLOCKING;
		
		public static TestSuite getCurrentMode(){
			return JAVA_7_BLOCKING;
		}
	}
	
	/**
	 * This is the list of pages/sites for which we want to download the web-page. I have declared dummy random download time associated with each page/site, for just testing.
	 * 
	 * @author vishal.zanzrukia
	 *
	 */
	enum Sites{
		
		/**google pages */
		Google_page1("www.google.com/page1", 100),
		Google_page2("www.google.com/page2", 200),
		Google_page3("www.google.com/page3", 300),
		Google_page4("www.google.com/page4", 400),
		Google_page5("www.google.com/page5", 450),
		
		/** amazon pages */
		Amazon_page1("www.amazon.com/page1", 15000), /** to explain worst case I am setting highest time across all the sites, all below sites needs to wait until this completes */
		Amazon_page2("www.amazon.com/page2", 520),
		Amazon_page3("www.amazon.com/page3", 530),
		Amazon_page4("www.amazon.com/page4", 540),
		Amazon_page5("www.amazon.com/page5", 550),
		Amazon_page6("www.amazon.com/page6", 600),
		
		/** youtube pages */
		Youtube_page1("www.youtube.com/page1", 1000),
		Youtube_page2("www.youtube.com/page2", 1010),
		Youtube_page3("www.youtube.com/page3", 1030),
		Youtube_page4("www.youtube.com/page4", 1050),
		Youtube_page5("www.youtube.com/page5", 1070),
		Youtube_page6("www.youtube.com/page6", 1080),
		Youtube_page7("www.youtube.com/page7", 1100),
		Youtube_page8("www.youtube.com/page8", 1200),
		Youtube_page9("www.youtube.com/page9", 1300),
		Youtube_page10("www.youtube.com/page10", 1400),
		Youtube_page11("www.youtube.com/page11", 1500),
		
		/** yahoo pages */
		Yahoo_page1("www.yahoo.com/page1", 5100), 
		Yahoo_page2("www.yahoo.com/page2", 5200),
		Yahoo_page3("www.yahoo.com/page3", 5300),
		Yahoo_page4("www.yahoo.com/page4", 5400),
		Yahoo_page5("www.yahoo.com/page5", 5500),
		Yahoo_page6("www.yahoo.com/page6", 5600),
		
		
		/** ending the class declaration for enum */
		;
		
		private String url;
		private Integer time;
		
		private Sites(String url, Integer millis){
			this.url = url;
			this.time = millis;
		}
		
		public String getSiteURL(){
			return this.url;
		}
		
		public long getTime(){
			return this.time;
		}
		
		/**
		 * download the site
		 * 
		 * @param site
		 * @return
		 */
		public Sites downloadSite(){
			try {
				System.out.println("Starting to download page ------------> "+this.getSiteURL());
				/** just blocking the current thread with specified time */
				Thread.sleep(this.getTime());
				System.out.println("Completed to download page ------------> "+this.getSiteURL());
			} catch (Exception e) {
				e.printStackTrace();
			}
			return this;
		}
		
		
		@Override
		public String toString() {
			return new StringBuffer(this.name())
			.append(":[url=")
			.append(this.url)
			.append(", time=")
			.append(this.time)
			.append("]")
			.toString();
		}
	}
	
	/**
	 * extracting the data from site
	 * 
	 * @param site
	 * @return
	 */
	public static void extractDataFromSite(Sites site){
		System.out.println("Extracting the data from : " + site);
	}
	
	/**
	 * Java 7 code example with blocking
	 * 
	 * @throws Exception
	 */
	private static void processTasksWithBlockingUsingJava7() throws Exception{
		final ExecutorService pool = Executors.newFixedThreadPool(5);
		List<Future<Sites>> contentsFutures = new ArrayList<>(Sites.values().length);
		
		Map<Sites, LoggableEvent> events = LoggableEvent.generateEvents(Sites.values());
		
		for (final Sites site : Sites.values()) {
		    final Future<Sites> contentFuture = pool.submit(new Callable<Sites>() {
		        @Override
		        public Sites call() throws Exception {
		        	return site.downloadSite();
		        }
		    });
		    contentsFutures.add(contentFuture);
		}
		
		long totalBlockingTime = 0;
		
		for (Future<Sites> contentFuture : contentsFutures) {
		    Sites site = contentFuture.get();
		    totalBlockingTime = logDownloadStatatics(events, totalBlockingTime, site);
		}
		
		logFinalBlockTime(totalBlockingTime);
	}
	
	/**
	 * Java 7 code example with non-blocking
	 * 
	 * @throws Exception
	 */
	private static void processTasksWithoutBlockingUsingJava7() throws Exception{
		final ExecutorService pool = Executors.newFixedThreadPool(5);
		Map<Sites, LoggableEvent> events = LoggableEvent.generateEvents(Sites.values());
		
		final ExecutorCompletionService<Sites> completionService = new ExecutorCompletionService<>(pool);
		for (final Sites site : Sites.values()) {
		    completionService.submit(new Callable<Sites>() {
		        @Override
		        public Sites call() throws Exception {
		        	return site.downloadSite();
		        }
		    });
		}
		
		long totalBlockingTime = 0;
		
		pool.shutdown();
		
		 while (!pool.isTerminated()) {
		    Sites site = completionService.take().get();
		    totalBlockingTime = logDownloadStatatics(events, totalBlockingTime, site);
		}
		
		logFinalBlockTime(totalBlockingTime);
	}
	
	private static void processTasksWithoutBlockingUsingJava8() throws Exception{
		
		Map<Sites, LoggableEvent> events = LoggableEvent.generateEvents(Sites.values());
		
		Stream.of(Sites.values()).forEach(site -> {
			CompletableFuture.supplyAsync(site::downloadSite)
			.thenAccept(ConcurrencyDemo::extractDataFromSite);
		});
	}
	
	private static void logFinalBlockTime(long totalBlockingTime) {
		System.out.println("\n\n\nThe total waiting time for all the sites : "+LoggableEvent.getTimeDiffString(totalBlockingTime));
	}

	private static long logDownloadStatatics(Map<Sites, LoggableEvent> events, long totalBlockingTime, Sites site) {
		LoggableEvent event =  events.get(site);
		event.done();
		System.out.println(event.toString());
		long diff = event.getDiff() - site.getTime();
		totalBlockingTime += diff;
		System.out.println("Waiting/blocking time for "+ site.getSiteURL() +" is : " + LoggableEvent.getTimeDiffString(diff));
		System.out.println("=========================");
		return totalBlockingTime;
	}
	
	public static void main(String[] args) throws Exception {
		
		switch (TestSuite.getCurrentMode()) {
		case JAVA_7_BLOCKING:
			System.out.println(TestSuite.JAVA_7_BLOCKING + " is running..!");
			processTasksWithBlockingUsingJava7();
			break;
		case JAVA_7_NON_BLOCKING:
			System.out.println(TestSuite.JAVA_7_NON_BLOCKING + " is running..!");
			processTasksWithoutBlockingUsingJava7();
			break;
		case JAVA_8_NON_BLOCKING:	
			System.out.println(TestSuite.JAVA_8_NON_BLOCKING + " is running..!");
			break;
		default:
			break;
		}
		
		System.exit(0);
	}
}



/**
 * @author vishal.zanzrukia
 *
 * The class which help us to log any type of event based on start and end time.
 */
class LoggableEvent{
	
	private String name;
	private long startTime;
	private long endTime;
	
	LoggableEvent(String name){
		this.name = name;
		setStartTime(System.currentTimeMillis());
	}
	
	/**
	 * Generate multiple events based on sites.
	 * 
	 * @param sites
	 * @return
	 */
	public static Map<Sites, LoggableEvent> generateEvents(Sites[] sites){
		Map<Sites, LoggableEvent> events = new HashMap<>();
		
		for(Sites site : sites){
			events.put(site, new LoggableEvent(site.name()));
		}
		
		return events;
	}
	
	public void done(){
		setEndTime(System.currentTimeMillis());
	}
	
	@Override
	public String toString() {
		return new StringBuffer("The event ")
		.append(this.name)
		.append(" has been completed in ")
		.append(getTimeDiffString(getDiff()))
		.toString();
	}

	public long getEndTime() {
		return endTime;
	}

	public void setEndTime(long endTime) {
		this.endTime = endTime;
	}

	public long getStartTime() {
		return startTime;
	}

	public void setStartTime(long startTime) {
		this.startTime = startTime;
	}
	
	public long getDiff(){
		return this.getEndTime() - this.getStartTime();
	}
	
	public static String getTimeDiffString(long milliseconds) {
		if(milliseconds < 1000){
			return milliseconds + " millis";
		}
		else{
			long seconds = TimeUnit.MILLISECONDS.toSeconds(milliseconds);
			milliseconds -= TimeUnit.SECONDS.toMillis(seconds);
	        long millis = TimeUnit.MILLISECONDS.toMillis(milliseconds);
			return String.format("%d seconds, %d millis", seconds, millis);
		}
	}
}