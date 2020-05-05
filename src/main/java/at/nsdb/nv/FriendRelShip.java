package at.nsdb.nv;


/*--------------------
 * connection (in neo4j relationship) between 2 persons
 */
public class FriendRelShip {
	private int fromId;
	private int toId;
	
	
	public String toString() {
		return String.format( "%6d <---> %6d", fromId, toId);
	}
	
	public static String cypherHeaderForImport() {
		return "id1,id2";
	}
	public String cypherDataForImport() {
		return String.format( "%d,%d", fromId, toId);
	}
	
	// relation type between nodes in neo4J
	public static String getNeo4jType() {
		return "KNOWS";
	}
	

	/*--------------------
	 * Setters, Getters
	 */
	public int getFromId() {
		return fromId;
	}

	public int getToId() {
		return toId;
	}
	
	/*--------------------
	 * constructor
	 */
	public FriendRelShip( int fromId, int toId) {
		this.fromId = fromId;
		this.toId = toId;
	}
	
	
	
}
