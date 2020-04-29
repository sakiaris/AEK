package at.nsdb.nv;

public abstract class Parameter {
	public static final int populationSize = 100;
	public static final String dbPath = "DBs/personsInTheMiddle";
	public static final boolean createNewDB = true;
	
	
	/*--------------------
	 * calculate the position of a person in the square- world depending on id
	 */
	public static int getMaxLongitude() {
		return (int) Math.round( Math.sqrt( populationSize));
	}
	public static int getMaxLatitude() {
		return getMaxLongitude();
	}
	
	
	/*--------------------
	 * decide radomly if 2 persons with distance are friend
	 */
	public static boolean isFriend( double distance) {
		return Utils.randomGetDouble() < Math.pow( Math.min( 0.5, 1.0 / distance), 2);
	}
	


}