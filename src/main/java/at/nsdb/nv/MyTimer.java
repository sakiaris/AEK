package at.nsdb.nv;

import java.time.ZonedDateTime;

public class MyTimer {
	long startTime;
	
	
	
	/*--------------------
	 * constructor
	 */
	public MyTimer() {
		startTime = ZonedDateTime.now().toInstant().toEpochMilli();
	}
	
	
	
	/*--------------------
	 * elapsed time in ms or String
	 */
	public long elapsedTime() {
		return ZonedDateTime.now().toInstant().toEpochMilli() - startTime;
	}

	
}
