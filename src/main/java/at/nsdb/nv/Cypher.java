package at.nsdb.nv;

import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import org.neo4j.graphalgo.GraphAlgoFactory;
import org.neo4j.graphalgo.PathFinder;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.PathExpander;
import org.neo4j.graphdb.PathExpanders;
import org.neo4j.graphdb.Result;
import org.neo4j.graphdb.Transaction;

import io.netty.handler.codec.http2.Http2FrameLogger.Direction;

public class Cypher {
	
	/*--------------------
	 * upload a persons into the database
	 */
	public static void uploadAPerson(Person person, Transaction tx) {
		Node node = tx.createNode(Label.label( Person.getNeo4jLabel()));
		node.setProperty("id", person.getId());
		node.setProperty("longitude", person.getLongitude());
		node.setProperty("latitude", person.getLatitude());
	}
	
	/*--------------------
	 * create a friend- relationship between 2 persons
	 */
	public static void createAFriendRelShip( FriendRelShip friend, Transaction tx) {
		String cypherQ = String.format(
			"MATCH (p:%s {id: %d}), (q:%s {id: %d}) CREATE (p)-[:%s]->(q)", 
			Person.getNeo4jLabel(), friend.getFromId(), Person.getNeo4jLabel(),
			friend.getToId(), FriendRelShip.getNeo4jType());
		tx.execute( cypherQ);
	}
	
	
	
	
	/*--------------------
	 * download all nodes in a Vector<Person>
	 */
	public static Vector<Person> downloadAllPersons( GraphDatabaseService graphDb) {
		Vector<Person> persons;
		try (Transaction tx = graphDb.beginTx()) {
			persons = new Vector<Person>();

			String cypherQ = String.format(
				"MATCH (p:%s) RETURN p", Person.getNeo4jLabel());
			Result result = tx.execute( cypherQ);		
			Iterator<Node> it = result.columnAs("p");
			it.forEachRemaining(node -> {
				persons.add(new Person( (int) node.getProperty("id")));
			});
			tx.commit();
		}
		return persons;
	}
	
	/*--------------------
	 * download all relations in a Vector<Connection>
	 */
	public static Vector<FriendRelShip> downloadAllFriendRelShips( GraphDatabaseService graphDb) {
		Vector<FriendRelShip> friends = new Vector<FriendRelShip>();
		try (Transaction tx = graphDb.beginTx()) {
			
			String cypherQ = String.format(
				"MATCH (p:%s)-[c:%s]->(q) RETURN p, q", 
				Person.getNeo4jLabel(), FriendRelShip.getNeo4jType());
			Result result = tx.execute( cypherQ);
			while( result.hasNext()) {
				Map<String, Object> row = result.next();
				Person p = new Person((int) ((Node) row.get("p")).getProperty("id"));
				Person q = new Person((int) ((Node) row.get("q")).getProperty("id"));
				friends.add(new FriendRelShip(p.getId(), q.getId()));
			}
			tx.commit();
		}
		return friends;
	}
	
	
	
	/*--------------------
	 * how many nodes in the database?
	 */
	public static int getNumbPersons( GraphDatabaseService graphDb) {
		int numbPersons = 0;
		try (Transaction tx = graphDb.beginTx()) {
			String cypherQ = String.format(
				"MATCH (n:%s) RETURN count(n) as count", 
				Person.getNeo4jLabel());
			Result result = tx.execute( cypherQ);
			numbPersons = (int) (long) result.columnAs( "count").next();
			tx.commit();
		}
		return (int) numbPersons;
	}
	
	/*--------------------
	 * how many friend- relation ships in the database?
	 */
	public static int getNumbFriendRelShips( GraphDatabaseService graphDb) {
		int numRelations = 0;
		try (Transaction tx = graphDb.beginTx()) {
			String cypherQ = String.format( 
				"MATCH (:%s)-[r:%s]->(:%s) RETURN count(r) as count",
				Person.getNeo4jLabel(), FriendRelShip.getNeo4jType(), Person.getNeo4jLabel());
			Result result = tx.execute( cypherQ);
			numRelations = (int) (long) result.columnAs( "count").next();
			tx.commit();
		}
		return (int) numRelations;
	}
	
	
	/*-----------------------------------------------------------------------------
	/*
	/* Dijkstra shortest path
	/* 
	/*-----------------------------------------------------------------------------
	 */
	public static Vector<Person> getShortestPath( 
		GraphDatabaseService graphDb, FriendRelShip frienRelShip) {
		Vector<Person> path = new Vector<Person>();
		try (Transaction tx = graphDb.beginTx()) {
			String cypherQ = String.format("MATCH (p1:Person {id:%d}), (p2:Person {id:%d}), " +
					"p=shortestPath( (p1)-[*]-(p2)) " + 
					"RETURN p", 8, 2);
			
			cypherQ = "MATCH (p) return count(p) as count";
			Utils.logging(cypherQ);
			
			try (Result result = tx.execute(cypherQ)) {
				System.out.println( String.format( "Erg %d", (int) (long) result.columnAs( "count").next()));

			} finally {
				tx.commit();
			}
		}
		path.add( new Person( frienRelShip.getFromId()));
		path.add( new Person( frienRelShip.getToId()));
		return path;
	}
	
}
