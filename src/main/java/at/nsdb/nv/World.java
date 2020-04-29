package at.nsdb.nv;

import java.util.Vector;

public class World {
	private Neo4j neo4j;
	private int maxLongitude, maxLatitude;

	/*--------------------
	 * many persons are born (created) and saved in neo4j
	 */
	private void uploadPersons() {
		Vector<Person> persons = new Vector<Person>();
		for( int id = 1; id <= Parameter.populationSize; id ++) {
			persons.add( new Person( id));
		}
		neo4j.uploadPersons( persons);
	}

	/*--------------------
	 * connections between persons are created
	 * 
	 * timing 1.000 connections, lenovo i5
	 * 1: 20 sec: only 1 commit, no check if relationship exists
	 * 2: 36 sec: only 1 commit, check if relationship exists 
	 */
	private void createConnections() {
		Vector<TupleId> connections = new Vector<TupleId>();
		for( int id1 = 1; id1 <= Parameter.populationSize; id1 ++) {
			for( int id2 = 1; id2 <= Parameter.populationSize; id2 ++) {
				if( id1 != id2) {
					if( Parameter.isFriend( new Person( id1).distance( new Person( id2)))) {
						connections.add( new TupleId( id1, id2));
					}
				}
			}
		}
		neo4j.createRelations( connections);

	}

	/*--------------------
	 * Setters, Getters
	 */
	public int getMaxLongitude() {
		return maxLongitude;
	}
	
	public int getMaxLatitude() {
		return maxLatitude;
	}

	/*--------------------
	 * constructor
	 */
	public World(Neo4j neo4j, int populationSize) {
		super();
		this.maxLongitude = (int) Math.round( Math.sqrt( populationSize));
		this.maxLatitude = this.maxLongitude;
		this.neo4j = neo4j;

		// population is born
		Utils.logging( "part 1: creating nodes ...");
		uploadPersons();
		Utils.logging( "part 1: finished");
		
		// relations are created
		Utils.logging( "part 2: creating relations ...");
		createConnections();
		Utils.logging( "part 2: finished");
	}

}
