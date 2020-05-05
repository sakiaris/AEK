package at.nsdb.nv;

public class Person {
	
	
	// Attributes of class Person
	private int id, longitude, latitude;
	
	public String toString() {
		return String.format( "id=%9d pos=%4d/%4d", id, longitude, latitude);
	}
	
	public static String cypherHeaderForImport() {
		return "id,longitude,latitude";
	}
	public String cypherDataForImport() {
		return String.format( "%d, %d, %d", id, longitude, latitude);
	}
		
	// label of the node in neo4j
	public static String getNeo4jLabel() {
		return "Person";
	}
	
	public double distance( Person p) {
		return Math.sqrt( Math.pow( this.longitude - p.longitude, 2) + Math.pow( this.latitude - p.latitude, 2));
	}
	
	
	/*--------------------
	 * Setters, Getters
	 */
	public int getId() {
		return id;
	}

	public int getLongitude() {
		return longitude;
	}

	public int getLatitude() {
		return latitude;
	}


	/*--------------------
	 * constructor
	 */
	public Person( int id) {
		super();
		this.id = id;
		this.longitude = Math.floorMod( id, Parameter.getMaxLongitude()) + 1;
		this.latitude = Math.floorDiv( id, Parameter.getMaxLatitude()) + 1;
	}
}
