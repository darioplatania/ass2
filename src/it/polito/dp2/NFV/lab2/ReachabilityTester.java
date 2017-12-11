package it.polito.dp2.NFV.lab2;

import java.util.Set;

/**
 * An interface for getting information about which IN hosts
 * are reachable from an NF-FG node of the DP2-NFV system.
 * A graph representing the NF-FGs and IN hosts and their relationships
 * must first be loaded. The load operation may fail. 
 * After having successfully loaded a graph, the user can get
 * a set of ExtendedNodeReader by which reachable hosts can be obtained.
 *
 */
public interface ReachabilityTester {
	
	/**
	 * Loads the graph (for a single NF-FG).
	 * If the graph was already successfully loaded, it is not loaded again and an AlreadyLoadedException is thrown.
	 * @param nffgName	the name of the NF-FG for which nodes have to be loaded
	 * @throws UnknownNameException if the name of the NF-FG is unknown or null
	 * @throws AlreadyLoadedException	if the graph has already been loaded.
	 * @throws ServiceException	if any other error occurs when trying to upload the graph. The load operation may have been executed partially in this case.
	 */
	void loadGraph(String nffgName) throws UnknownNameException, AlreadyLoadedException, ServiceException;
	
	/**
	 * Returns a set of objects that can be used for reading information about NF-FG nodes and
	 * for getting reachable hosts.
	 * @param nffgName	the name of the NF-FG for which nodes have to be returned
	 * @return a set of ExtendedNodeReader objects
	 * @throws UnknownNameException if the name of the NF-FG is unknown or null
	 * @throws NoGraphException if no graph has been loaded for the requested NF-FG
	 * @throws ServiceException	if any other error occurs when trying to create the ExtendedNodeReader objects.
	 */
	Set<ExtendedNodeReader> getExtendedNodes(String nffgName) throws UnknownNameException, NoGraphException, ServiceException;
	
	/**
	 * Checks if the graph has already been successfully loaded.
	 * This is a local operation that can never fail.
	 * @return	true if the graph has already been successfully loaded, false otherwise.
	 * @throws UnknownNameException if the name of the NF-FG is unknown or null
	 */
	boolean isLoaded(String nffgName) throws UnknownNameException;
}
