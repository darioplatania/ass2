package it.polito.dp2.NFV.sol2;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.GenericType;

import it.polito.dp2.NFV.HostReader;
import it.polito.dp2.NFV.LinkReader;
import it.polito.dp2.NFV.NffgReader;
import it.polito.dp2.NFV.NodeReader;
import it.polito.dp2.NFV.VNFTypeReader;
import it.polito.dp2.NFV.lab2.ExtendedNodeReader;
import it.polito.dp2.NFV.lab2.NoGraphException;

import it.polito.dp2.NFV.lab2.ServiceException;

public class ExtendedNodeReaderImpl implements ExtendedNodeReader {
	
	private HostReader hosts;
	private NffgReader nffgs;
	private VNFTypeReader vnfs;		
	private HashMap<String,LinkReader> links;
	private String name;
	private String nodeId;
	private String BaseURI;
	private Client client;
	private final static String PROPERTY = "it.polito.dp2.NFV.lab2.URL";
	private HashMap<String,HostReader> host = new HashMap<>();
	
	
	
	
public ExtendedNodeReaderImpl(NodeReader nodereader, String nodeId) throws NoGraphException,ServiceException {
	
	this.name = nodereader.getName();
	this.hosts = nodereader.getHost();
	this.links = new HashMap<String,LinkReader>();
	this.nffgs = nodereader.getNffg();
	this.vnfs = nodereader.getFuncType();
	//this.target = target;
	this.nodeId = nodeId;
	
	BaseURI = System.getProperty(PROPERTY);
	
	BaseURI += "/data";
	
	/*Init Client*/
	client = ClientBuilder.newClient();
  
	
	try{
		getReachableHosts();
		
	}catch(ServiceException | NoGraphException e) {
		System.out.println(e.getMessage() + "Exception getReachableHosts");
	}
	
	
}

	@Override
	public VNFTypeReader getFuncType() {
		// TODO Auto-generated method stub
		return this.vnfs;
	}

	@Override
	public HostReader getHost() {
		// TODO Auto-generated method stub
		return this.hosts;
	}

	@Override
	public Set<LinkReader> getLinks() {
		// TODO Auto-generated method stub
		return new LinkedHashSet<LinkReader>(this.links.values());
	}

	@Override
	public NffgReader getNffg() {
		// TODO Auto-generated method stub
	    return this.nffgs;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return this.name;
	}

	@Override
	public Set<HostReader> getReachableHosts() throws NoGraphException, ServiceException {
		// TODO Auto-generated method stub
		
		Response resp = client.target(BaseURI+"/node/"+ nodeId + "/reachableNodes?relationshipTypes=ForwardsTo&NodeLabel=Node").request(MediaType.APPLICATION_XML).get(Response.class);
		
		if(resp.getStatus()!= 200)
			throw new ServiceException("getExtendedNodes Failed");
		
		List<Node> list_node = resp.readEntity(new GenericType<List<Node>>(){});
		
		Iterator<Node> iterator = list_node.iterator();
		
		while(iterator.hasNext()) {
			Node node = (Node)iterator.next();
			//System.out.println("NODO ID: "+ node.getId());
			
			for( NodeReader node_r : this.nffgs.getNodes()) {
				if(node_r.getName().equals(node.getProperties().getProperty().get(0).value)) {
					host.put(node_r.getHost().getName(), node_r.getHost());
				}
					
			}
		}
		
		//includere host del nodo di partenza perchè può dare wrong numbers	
		//host.put(this.hosts.getName(), this.hosts);
		
		return new LinkedHashSet<HostReader>(this.host.values());
	}

}
