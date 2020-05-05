package at.nsdb.nv;

public abstract class Parameter {
	public static final int populationSize = 400*400;
	
	/*--------------------
	 * path/filename realtiv to the neo4j database i.e. <projectdirectory>\..
	 */	
	private static final String dbPath = "DBs\\personsInTheMiddle";
	private static final String csvNodesImport = "importNodes.csv";  // no path allowed
	private static final String csvRelationsImport = "importRelations.csv"; // no path allowed
	
	// if true, old database will be destroyed, a new one will be created
	public static final boolean createNewDB = true;
	
	// if true, all nodes and relations will be inserted into the database
	// not sensible for much more then 1000 nodes
	// in any case, import files are created
	public static final boolean insertNodesAndRelationsIntoNeo4j = false;
	
	
	
	/*--------------------
	 * get the full filename
	 */
	public static String getDBFileName() {
		String projectDirectory = System.getProperty("user.dir");
		return projectDirectory.substring( 0, projectDirectory.lastIndexOf( "\\") + 1) + dbPath;
	}
	public static String getCSVNodesImportName() {
		String projectDirectory = System.getProperty("user.dir");
		return projectDirectory.substring( 0, projectDirectory.lastIndexOf( "\\") + 1) + csvNodesImport;
	}
	public static String getCSVRelationsImportName() {
		String projectDirectory = System.getProperty("user.dir");
		return projectDirectory.substring( 0, projectDirectory.lastIndexOf( "\\") + 1) + csvRelationsImport;
	}
	
	
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
