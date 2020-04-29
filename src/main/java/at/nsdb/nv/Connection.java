package at.nsdb.nv;


/*--------------------
 * connection (in neo4j relationship) between 2 persons
 */
public class Connection {
	private Person from;
	private Person to;
	
	
	public String toString() {
		return String.format( "%s <---> %s", from, to);
	}
	

	/*--------------------
	 * Setters, Getters
	 */
	public Person getFrom() {
		return from;
	}

	public Person getTo() {
		return to;
	}
	
	/*--------------------
	 * constructor
	 */
	public Connection( Person from, Person to) {
		this.from = from;
		this.to = to;
	}
	
	
	
}
