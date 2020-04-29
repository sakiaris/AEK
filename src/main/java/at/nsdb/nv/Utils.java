package at.nsdb.nv;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;


/*--------------------
 * some utilities
 */
public abstract class Utils {

	static Random random = new Random();
	
	/*--------------------
	 * randomGetInt( 10, 12) means randomly 10, 11 or 12
	 */
	public static int randomGetInt( int fromInt, int toInt) {
		return random.nextInt( toInt - fromInt + 1) + fromInt;
	}
	public static double randomGetDouble() {
		return random.nextDouble();
	}
	
	
	/*--------------------
	 * logging
	 */
	public static void logging( Object o) {
	    DateFormat formatter = new SimpleDateFormat( "HH:mm:ss");
		System.out.println( String.format( "%s: %s", formatter.format(new Date()), o));
	}
	
}
