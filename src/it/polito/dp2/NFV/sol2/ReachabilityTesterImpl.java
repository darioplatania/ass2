package it.polito.dp2.NFV.sol2;

import java.io.StringWriter; 
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;


import it.polito.dp2.NFV.*;
import it.polito.dp2.NFV.lab2.AlreadyLoadedException;
import it.polito.dp2.NFV.lab2.ExtendedNodeReader;
import it.polito.dp2.NFV.lab2.NoGraphException;
import it.polito.dp2.NFV.lab2.ReachabilityTester;
import it.polito.dp2.NFV.lab2.ReachabilityTesterException;
import it.polito.dp2.NFV.lab2.ServiceException;
import it.polito.dp2.NFV.lab2.UnknownNameException;
import it.polito.dp2.NFV.sol2.Node;




public class ReachabilityTesterImpl implements ReachabilityTester {
	
	private NfvReader monitor;
	private static DateFormat dateFormat;
	private final String PROPERTY = "it.polito.dp2.NFV.lab2.URL";
	private String BaseURI ;
	private HashMap<String, Node> Neo4JNodes;
	private Client client;
	
	
	public ReachabilityTesterImpl() throws ReachabilityTesterException {
        // create the basic URL as a String
		BaseURI = System.getProperty(PROPERTY);

        if (BaseURI == null) {
            throw new ReachabilityTesterException("Property not setted!");
        }

        BaseURI += "/data";

        /*Init Client*/
		client = ClientBuilder.newClient();
      
        try {
            NfvReaderFactory factory = NfvReaderFactory.newInstance();
            monitor = factory.newNfvReader();
        } catch (NfvReaderException e) {
            throw new ReachabilityTesterException("Cannot instantiate NfvReaderFactory");
        }

        // initialize node list
        Neo4JNodes = new HashMap<>();
    }


	@Override
	public void loadGraph(String nffgName) throws UnknownNameException, AlreadyLoadedException, ServiceException {
		// TODO Auto-generated method stub
		
		// retrieve nffg for the random generator
        NffgReader nffgReader = monitor.getNffg(nffgName);

        // if nffg doesn't exist
        if (nffgReader == null) {
            throw new UnknownNameException();
        }
        
     // upload all node
        for (NodeReader node : nffgReader.getNodes()) {
            NodePropertiesCreate(node.getName());
        }

        for (NodeReader node : nffgReader.getNodes()) {
            for (LinkReader link : node.getLinks()) {
                Relationshipcreate(node.getName(), link.getDestinationNode().getName());
            }
        }

        // correct load
        nffgName = nffgName;
		
	}

	@Override
	public Set<ExtendedNodeReader> getExtendedNodes(String nffgName)
			throws UnknownNameException, NoGraphException, ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean isLoaded(String nffgName) throws UnknownNameException {
		// TODO Auto-generated method stub
		return false;
	}
	
	
/*
 * 
 * MY Function
 * 
 */

	private void NodePropertiesCreate(String nodeName) throws ServiceException {
		
		Node node = new Node();
	
		//a node has to be created for each NFFG node, with a property named “name” whose value is the NFFG name;
		Property name_property = new Property();
		Properties pr = new Properties();
		
		name_property.setName("name");
		name_property.setValue(nodeName);
		
		pr.getProperty().add(name_property);
		
		node.setProperties(pr);
		
		Labels labels = new Labels();
		String label = "Node";
		labels.getLabel().add(label);

		/*Post node with properties*/
		Response resp = client.target(BaseURI+"/node").request(MediaType.APPLICATION_XML).post(Entity.entity(node,MediaType.APPLICATION_XML),Response.class);
		
		if(resp.getStatus()!=201)
			throw new ServiceException("Node not create" + resp.getStatus());

		node.setId(resp.readEntity(Node.class).getId());
		/*mi serve per poi poter fare le relationship*/
		Neo4JNodes.put(name_property.getValue(), node);
		
		/*Post node with label*/
		resp = client.target(BaseURI+"/node/"+ node.getId()  +"/labels").request(MediaType.APPLICATION_XML).post(Entity.entity(labels,MediaType.APPLICATION_XML),Response.class);
		
		if(resp.getStatus()!=204)
			throw new ServiceException("Label is not created" + resp.getStatus());
		
	}
	
	
	private void Relationshipcreate(String srcNode, String destNode) throws ServiceException {

        Relationship rel = new Relationship();
        rel.setType("ForwardsTo");
        rel.setDstNode(Neo4JNodes.get(destNode).getId());

         /* Insert Relationship */
        Response  resp = client.target(BaseURI+"/node/"+Neo4JNodes.get(srcNode).getId()+"/relationships").request(MediaType.APPLICATION_XML).post(Entity.entity(rel,MediaType.APPLICATION_XML),Response.class);

        if ((resp.getStatus() != 200) && (resp.getStatus() != 201)) {
            throw new ServiceException("(!-3) Error creating relationship");
        }
    }

        
 }
	

	



	


