/*----------------------------------------------------
 * How many persons in the middle are necessary to contact everyone in the world
 */

package at.nsdb.nv;

import java.io.File;
import java.io.IOException;

public class Main {
		
	public static void main( final String[] args ) throws IOException
    {	
		
		/*--------------------
		 * create empty neo4j database
		 */
		Utils.logging( String.format( "**** %sopening DB ...", Parameter.createNewDB ? "creating & " : ""));
		Utils.logging( String.format( "database directory = %s", Parameter.getDBFileName()));
		Neo4j neo4j = new Neo4j( new File( Parameter.getDBFileName()), Parameter.createNewDB);
		Utils.logging( String.format( "---- %sopening DB finished", Parameter.createNewDB ? "creating & " : ""));

		
		/*--------------------
		 * create world- population & relations
		 */
		if( Parameter.createNewDB) {
			Utils.logging( "**** creating world ...");
	        new World( neo4j, Parameter.populationSize);
	        Utils.logging( "---- creating world finished\n");
		}
        
        
		/*--------------------
		 * Inhalt der DB andrucken
		 */
        Utils.logging( "**** printing db status/content ...");
		neo4j.printStatus();
		Utils.logging( "---- printing db status/content finished\n");

		
		/*--------------------
		 * Dijkstra, shortest path
		 */
        Utils.logging( "**** dijkstra ...");
        neo4j.getShortestPath( new FriendRelShip( 3, 50));
        Utils.logging( "---- dijkstra finished\n");
        
		
		/*--------------------
		 * shut down
		 */
        Utils.logging( "**** shut down db ...");
        neo4j.shutDown();
        Utils.logging( "---- shut down db finished\n");
    }
}
