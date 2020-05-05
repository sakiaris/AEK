package at.nsdb.nv;

import static org.neo4j.configuration.GraphDatabaseSettings.DEFAULT_DATABASE_NAME;

import java.io.File;
import java.io.IOException;
import java.util.Vector;

import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.io.fs.FileUtils;

public class Neo4j {
	private File databaseDirectory;
	private DatabaseManagementService managementService;

	GraphDatabaseService graphDb;
	Relationship relationship;


	/*-----------------------------------------------------------------------------
	/*
	/* constructor, create database
	/* 
	/*-----------------------------------------------------------------------------
	 */
	public Neo4j(File databaseDirectory, boolean createNewDB) {
		super();
		this.databaseDirectory = databaseDirectory;
		try {
			create(createNewDB);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void create(boolean createNewDB) throws IOException {
		if (createNewDB) {
			FileUtils.deleteRecursively(databaseDirectory);
		}

		managementService = new DatabaseManagementServiceBuilder(databaseDirectory).build();
		graphDb = managementService.database(DEFAULT_DATABASE_NAME);
		registerShutdownHook(managementService);
		
		if (createNewDB) {
			// constraint für eindeutige id und Index( id)- Aufbau
			try (Transaction tx = graphDb.beginTx()) {
				tx.execute( "CREATE CONSTRAINT ON (p:Person) ASSERT p.id IS UNIQUE");			
				tx.commit();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}



	/*--------------------
	 * upload one or a Vector of persons into the database
	 */
	public void uploadAPerson(Person person) {
		try (Transaction tx = graphDb.beginTx()) {
			Cypher.uploadAPerson(person, tx);
			tx.commit();
		}
	}
	public void uploadPersons(Vector<Person> persons) {
		try (Transaction tx = graphDb.beginTx()) {
			persons.forEach(p -> Cypher.uploadAPerson(p, tx));
			tx.commit();
		}
	}
	


	/*--------------------
	 * create a friend- relationship between 2 persons
	 */
	public void createAFriendRelShip( FriendRelShip connection) {
		try (Transaction tx = graphDb.beginTx()) {
			Cypher.createAFriendRelShip(connection, tx);
			tx.commit();
		}
	}
	public void createFriendRelShips( Vector<FriendRelShip> connections) {
		int count = 0;
		Utils.logging( String.format( "creating %d relations into database", connections.size()));
		try (Transaction tx = graphDb.beginTx()) {
			for( FriendRelShip c : connections) {	
				Cypher.createAFriendRelShip(c, tx);
				if( Math.floorMod( ++count, 10000) == 0) 
					Utils.logging( String.format( "%d/%d (%.2f%%) done", 
							count, connections.size(), (double) count / connections.size() * 100));
			}
			tx.commit();
		}
	}

	
	/*-----------------------------------------------------------------------------
	/*
	/* Dijkstra shortest path
	/* 
	/*-----------------------------------------------------------------------------
	 */
	public Vector<Person> getShortestPath( FriendRelShip frienRelShip) {
		return Cypher.getShortestPath( graphDb, frienRelShip);
	}
	

	/*--------------------
	 * print number Persons, friend relation- ship
	 */
	public void printStatus() {
		Vector<Person> persons = new Vector<Person>();
		persons = Cypher.downloadAllPersons( graphDb);
		Utils.logging(String.format("%d (%d) nodes found, for example:", 
				persons.size(), Cypher.getNumbPersons( graphDb)));

		// persons.forEach(person -> System.out.println(person));
		for (int i = 1; i <= Math.min( 3, Cypher.getNumbPersons( graphDb)); i++) {
			Utils.logging(persons.elementAt(Utils.randomGetInt(1, persons.size() - 1)));
		}

		Vector<FriendRelShip> friendRelShips = new Vector<FriendRelShip>();
		friendRelShips = Cypher.downloadAllFriendRelShips( graphDb);
		Utils.logging( String.format("%d (%d) relations found, for example:",
			friendRelShips.size(), Cypher.getNumbFriendRelShips( graphDb)));
		for (int i = 1; i <= Math.min( 3, Cypher.getNumbFriendRelShips( graphDb)); i++) {
			Utils.logging( friendRelShips.elementAt(Utils.randomGetInt(1, friendRelShips.size() - 1)));
		}
	}

	
	/*-----------------------------------------------------------------------------
	/*
	/* shutdown database
	/* 
	/*-----------------------------------------------------------------------------
	 */
	void shutDown() {
		// tag::shutdownServer[]
		managementService.shutdown();
		// end::shutdownServer[]
	}

	private static void registerShutdownHook(final DatabaseManagementService managementService) {
		// Registers a shutdown hook for the Neo4j instance so that it
		// shuts down nicely when the VM exits (even if you "Ctrl-C" the
		// running application).
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				managementService.shutdown();
			}
		});
	}
}
