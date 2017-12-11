/**
 * 
 */
package it.polito.dp2.NFV.lab2;

import it.polito.dp2.NFV.NodeReader;

import java.util.Set;

import it.polito.dp2.NFV.HostReader;

/**
 * This interface is a NodeReader that also lets one get reachable Hosts
 *
 */
public interface ExtendedNodeReader extends NodeReader {
	/**
	 * Gets the interfaces for reading information about the hosts that are reachable from this node
	 * @return	a set of objects for reading information about the hosts that are reachable from this host
	 * @throws NoGraphException	if no graph is currently loaded
	 * @throws ServiceException	if any other error occurs when trying to perform the operation
	 */
	Set<HostReader> getReachableHosts() throws NoGraphException, ServiceException;
	
}
