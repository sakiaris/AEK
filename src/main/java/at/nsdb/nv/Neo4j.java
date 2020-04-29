package at.nsdb.nv;

import static org.neo4j.configuration.GraphDatabaseSettings.DEFAULT_DATABASE_NAME;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;
import org.neo4j.io.fs.FileUtils;


public class Neo4j {
	private File databaseDirectory;
	private DatabaseManagementService managementService;

	GraphDatabaseService graphDb;
	Relationship relationship;
	

	Vector<Person> persons;
	Vector<Connection> connections;

	
	
	/*--------------------
	 * property of the RelType
	 */
	private enum RelTypes implements RelationshipType {
		KNOWS
	}
	
	
	

	/*-----------------------------------------------------------------------------
	/*
	/* constructor, create database
	/* 
	/*-----------------------------------------------------------------------------
	 */
	public Neo4j( File databaseDirectory, boolean createNewDB) {
		super();
		this.databaseDirectory = databaseDirectory;
		try {
			create( createNewDB);
		} catch( IOException e) {
			e.printStackTrace();
		}
	}
	private void create( boolean createNewDB) throws IOException {
		if( createNewDB) FileUtils.deleteRecursively( databaseDirectory);
		
		managementService = new DatabaseManagementServiceBuilder( databaseDirectory).build();
		graphDb = managementService.database (DEFAULT_DATABASE_NAME);
		registerShutdownHook( managementService);
	}
	
	
	
	
	/*-----------------------------------------------------------------------------
	/*
	/* upload, download, db- manipulations
	/* 
	/*-----------------------------------------------------------------------------
	 */
	
	/*--------------------
	 * upload one or a Vector of persons into the database
	 */
	public void uploadAPerson( Person person) {
		try (Transaction tx = graphDb.beginTx()) {
			uploadAPerson( person, tx);
			tx.commit();
		}
	}
	public void uploadPersons( Vector<Person> persons) {
		try (Transaction tx = graphDb.beginTx()) {
			persons.forEach( p -> uploadAPerson( p, tx));
			tx.commit();
		}
	}
	private void uploadAPerson( Person person, Transaction tx) {
			Node node = tx.createNode();
			node.setProperty( "id", person.getId());
			node.setProperty( "longitude", person.getLongitude());
			node.setProperty( "latitude", person.getLatitude());
	}
	
	
	
	/*--------------------
	 * create a relation between 2 persons if not exists
	 */
	public void createARelation( TupleId connection) {
		try (Transaction tx = graphDb.beginTx()) {
			createARelation( connection, tx);
			tx.commit();
		}
	}
	public void createRelations( Vector<TupleId> connections) {
		try (Transaction tx = graphDb.beginTx()) {
			connections.forEach( c -> createARelation( c, tx));
			tx.commit();
		}
	}
	private void createARelation( TupleId connection, Transaction tx) {
		
		// relation even exists?
//		Result result = tx.execute( String.format( "MATCH (p {id: %d})-[r]->(q {id: %d}) " +
//				"RETURN count(r) as count", connection.a, connection.b));
//		long count = (long) result.columnAs( "count").next();
		
		//System.out.println( String.format( "1:%d 2:%d", count1, count2));

		// create relation between id1 and id2 it neither id1-id2 nor id2-id1 exists
		//if( count == 0) {
		
			String cypherQ = String.format( 
					"MATCH (p {id: %d}), (q {id: %d}) " +
					"CREATE (p)-[:%s]->(q)", 
					connection.a, connection.b, RelTypes.KNOWS);
			
			// also works!!
//			String cypherQ = String.format( 
//					"MATCH (p), (q) " +
//					"WHERE p.id = %d AND q.id = %d " +
//					"CREATE (p)-[r:%s]->(q)", 
//					connection.a, connection.b, RelTypes.KNOWS);	
			
			// System.out.println( cypherQ);
			tx.execute( cypherQ);
		//}
	}
	
		
	/*--------------------
	 * download all nodes in a Vector<Person>
	 */
	private void downloadtAllNodes() {
		try (Transaction tx = graphDb.beginTx()) {
			persons = new Vector<Person>();

			Result result = tx.execute( "MATCH (p) RETURN p");
			Iterator<Node> it = result.columnAs( "p");
			it.forEachRemaining(node -> {
				persons.add( new Person( (int) node.getProperty( "id")));
			});
			tx.commit();
		}
	}
	
	
	/*--------------------
	 * download all relations in a Vector<Connection>
	 */
	private void downloadAllRelations() {
		try (Transaction tx = graphDb.beginTx()) {
			connections = new Vector<Connection>();

			Result result = tx.execute("MATCH (p)-[c]->(q) RETURN p, q");
			while (result.hasNext()) {
				Map<String, Object> row = result.next();
				Person p = new Person( (int) ((Node) row.get( "p")).getProperty( "id"));
				Person q = new Person( (int) ((Node) row.get( "q")).getProperty( "id"));
				connections.add(new Connection( p, q));
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
	public void dijkstra( TupleId fromToId) {
		
		try (Transaction tx = graphDb.beginTx()) {

//			String cypherQ = String.format( 
//				"MATCH path = shortestPath (a:A {id:%d})-[*0..10]-(b:B {id:%d})) " + 
//				"RETURN path", findPath.a, findPath.b);
			
//			String cypherQ = 
//				"CALL algo.allShortestPaths.stream('cost', {" +
//				"nodeQuery:'MATCH (n:Loc) RETURN id(n) as id'," +
//				"relationshipQuery:'MATCH (n:Loc)-[r]-(p:Loc) RETURN id(n) as source, id(p) as target, r.cost as weight'," +
//				"graph:'cypher', defaultValue:1.0})";
			
//			String cypherQ = String.format(
//					"MATCH path = shortestPath( (p:Person {Id: %d})-[:%s*1..20]-(q:Person {Id: %d})) " +
//					"RETURN *", 3, RelTypes.KNOWS, 90);
			
//			String cypherQ = String.format( 
//					"MATCH path = shortestPath( (p:Person {id: %d})-[:%s*]-(q:Person {id: %d})) " +
//					"RETURN *", 3, RelTypes.KNOWS, 10);
			
//			String cypherQ = String.format( 
//					"MATCH (KevinB:Person {id: %d}),(Al:Person {id: %d}), " +
//					"p = shortestPath((KevinB)-[:%s*]-(Al)) " +
//					"WHERE ALL (r IN relationships(p) WHERE EXISTS (r.role)) " +
//					"RETURN p", 3, 6, RelTypes.KNOWS);
			
			String cypherQ = String.format( 
					"MATCH (p1: {id: %d}), (p2: {id: %d}), " +
					"path = shortestPath( (p1)-[*]-(p2)) " +
					"WHERE length( path) > 1 " +
					"RETURN path", 2, 40);
			
			
			Utils.logging( cypherQ);
			Result result = tx.execute( cypherQ);
			Utils.logging( String.format( "Query: #columns=%d, at least one row=%s", result.columns().size(), result.hasNext()));
			
			if( (result.columns().size() == 0) || ! result.hasNext()) {
				Utils.logging( "sorry, query above doesn't work!");
				Utils.logging( result.getExecutionPlanDescription());
				Utils.logging( result.getQueryExecutionType());
			} else {
				System.out.println( "****************************  YEAHHH at least one record returned");
			}
			
//			Iterator<Node> it = result.columnAs( "path");
//			it.forEachRemaining( node -> System.out.println( node.getProperty( "path")));
			
			tx.commit();
		}
	}
	
	
	/*-----------------------------------------------------------------------------
	/*
	/* database information
	/* 
	/*-----------------------------------------------------------------------------
	 */
	
	/*--------------------
	 * how many nodes in the database?
	 */
	public int getNumbNodes() {
		int numNodes = 0;
		try (Transaction tx = graphDb.beginTx()) {
			Result result = tx.execute( String.format( "MATCH (n) RETURN count(n) as count"));
			numNodes = (int) (long) result.columnAs( "count").next();
			tx.commit();
		}
		return (int) numNodes;
	}
	
	/*--------------------
	 * how many relations in the database?
	 */
	public int getNumbRelations() {
		int numRelations = 0;
		try (Transaction tx = graphDb.beginTx()) {
			Result result = tx.execute( String.format( "MATCH ()-[r]->() RETURN count(r) as count"));
			numRelations = (int) (long) result.columnAs( "count").next();
			tx.commit();
		}
		return (int) numRelations;
	}

	
	/*--------------------
	 * print number nodes, relations
	 */
	public void printStatus() {
		downloadtAllNodes();
		Utils.logging( String.format( "%d (%d) nodes found, for example:", persons.size(), this.getNumbNodes()));
		
		//persons.forEach(person -> System.out.println(person));
		for( int i = 1; i <= 3; i++) {
			Utils.logging( persons.elementAt( Utils.randomGetInt( 1, persons.size() -1)));
		}
		
		downloadAllRelations();
		Utils.logging( String.format( "%d (%d) relations found, for example:", connections.size(), this.getNumbRelations()));
		for( int i = 1; i <= 3; i++) {
			Utils.logging( connections.elementAt( Utils.randomGetInt( 1, connections.size() -1)));
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

	private static void registerShutdownHook( final DatabaseManagementService managementService) {
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
