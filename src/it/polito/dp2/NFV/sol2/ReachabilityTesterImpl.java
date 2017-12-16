package it.polito.dp2.NFV.sol2;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
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
	private final static String PROPERTY = "it.polito.dp2.NFV.lab2.URL";
	private String BaseURI;
	private static HashMap<String, Node> Neo4JNodes;
	private static HashMap<String, Node> Neo4JHost;
	private static HashMap<String, NodeReader> Nodes;
	private static HashMap<String, NffgReader> NffgMap;
	private HashMap<String, ExtendedNodeReaderImpl> ext_nodemap;
	private Client client;
	private String nffg = null;
	
	
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
        Neo4JHost = new HashMap<>();
        Nodes = new HashMap<>();
        NffgMap = new HashMap<>();
        ext_nodemap = new HashMap<>();
    }


	@Override
	public void loadGraph(String nffgName) throws UnknownNameException, AlreadyLoadedException, ServiceException {
		// TODO Auto-generated method stub
		
        /*Start isLoaded*/
        boolean res_nffg = isLoaded(nffgName);
        if(res_nffg == true)
        		throw new AlreadyLoadedException("Nffg Loaded Exception!");
	
		/*Retrieve nffg for the random generator*/
        NffgReader nffgReader = monitor.getNffg(nffgName);
      
        /*Correct Load*/
        NffgMap.put(nffgReader.getName(), nffgReader);
        
        /*Upload all node*/
        for (NodeReader node : nffgReader.getNodes()) {
	        	if(node != null) {
	        		boolean res_node = NodePropertiesCreate(node.getName());
		        	if(res_node == true) {
		        		Nodes.put(node.getName(), node);
		        		printBlankLine();
		            HostPropertiesCreate(node.getHost().getName());
		            printBlankLine();
		            NodeHostRel(node.getName(),node.getHost().getName());
		        }
	        	}
        }
        
       	for(Map.Entry<String, NodeReader> node : Nodes.entrySet()){	
            for (LinkReader link : node.getValue().getLinks()) {       
            		printBlankLine();          		
                NodeLinkRel(node.getValue().getName(), link.getDestinationNode().getName());
            }
        }

		
	}

	@Override
	public Set<ExtendedNodeReader> getExtendedNodes(String nffgName) throws UnknownNameException, NoGraphException, ServiceException {
		// TODO Auto-generated method stub
			
		/*Retrieve nffg for the random generator*/
		if(nffgName==null)
			throw new UnknownNameException("Nffg Null");
		
		NffgReader nffgReader = NffgMap.get(nffgName);
        
        if(nffgReader == null)
        		throw new UnknownNameException("Nffg Null getExtendedNodes");
        
        for(NodeReader nodeReader : nffgReader.getNodes()) {
        		if(nodeReader != null) {
        			if(Neo4JNodes.get(nodeReader.getName()) != null) {
        				String nodeId = Neo4JNodes.get(nodeReader.getName()).getId();
        				ExtendedNodeReaderImpl ext_node = new ExtendedNodeReaderImpl(nodeReader,nodeId);
        				ext_nodemap.put(ext_node.getName(), ext_node);
        			}
        		}
        }
        
		return new LinkedHashSet<ExtendedNodeReader>(ext_nodemap.values());
	}

	@Override
	public boolean isLoaded(String nffgName) throws UnknownNameException {
		// TODO Auto-generated method stub
		System.out.println("*** INIT isLoaded ***");
		
		if(nffgName == null)
			throw new UnknownNameException("Nffg error null!");
		
		if(monitor.getNffg(nffgName) == null)
			throw new UnknownNameException("Nffg error null!");;
		
		if(NffgMap.containsKey(nffgName)) 
			return true;
			
		return false;	
	}
	
	
/*
 * **********************
 * My Function
 * **********************
 */

	private boolean NodePropertiesCreate(String nodeName) throws ServiceException {
		System.out.println("*** INIT NODE PROPERTIES CREATE ***");
		
		if(Neo4JNodes.get(nodeName)==null) {
			
			printBlankLine();
			System.out.println("NON ESISTE IL NODO");
			
			Node node = new Node();
		
			//a node has to be created for each NFFG node, with a property named “name” whose value is the NFFG name;
			Property name_property = new Property();
			Properties pr = new Properties();
			
			name_property.setName("name");
			name_property.setValue(nodeName);
			
			pr.getProperty().add(name_property);
			
			node.setProperties(pr);
			
			Labels labels = new Labels();
			labels.getLabel().add("Node");
			node.setLabels(labels);
	
			/*Post properties*/
			Response resp = client.target(BaseURI+"/node").request(MediaType.APPLICATION_XML).post(Entity.entity(node,MediaType.APPLICATION_XML),Response.class);
			
			if(resp.getStatus()!=201)
				throw new ServiceException("Node not create" + resp.getStatus());
	
			node.setId(resp.readEntity(Node.class).getId());
			
			/*Post label*/
			resp = client.target(BaseURI+"/node/"+ node.getId()  +"/labels").request(MediaType.APPLICATION_XML).post(Entity.entity(labels,MediaType.APPLICATION_XML),Response.class);
			
			if(resp.getStatus()!=204)
				throw new ServiceException("Label is not created" + resp.getStatus());
			
			/*aggiorno la mappa*/
			Neo4JNodes.put(name_property.getValue(), node);
			//Neo4JNodes.put(node.getId(), node);
			System.out.println("DIMENSIONE MAPPA NODI NODEPROP: " + Neo4JNodes.size());
			System.out.println("*** FINISH NODE PROPERTIES CREATE ***");
			
			return true;
		}
		else {
			printBlankLine();
			System.out.println("ESISTE IL NODO");
			return false;
		}
		
	}
	
	
	private void NodeLinkRel(String srcNode, String destNode) throws ServiceException {
		
		System.out.println("*** INIT NODE LINK RELATIONSHIP ***");
		
        Relationship rel = new Relationship();
        rel.setType("ForwardsTo");
        rel.setDstNode(Neo4JNodes.get(destNode).getId());

         /*Post Relationship */
        Response  resp = client.target(BaseURI+"/node/"+Neo4JNodes.get(srcNode).getId()+"/relationships").request(MediaType.APPLICATION_XML).post(Entity.entity(rel,MediaType.APPLICATION_XML),Response.class);

        if ((resp.getStatus() != 200) && (resp.getStatus() != 201)) {
            throw new ServiceException("Error creating relationship");
        }
        System.out.println("*** FINISH NODE LINK RELATIONSHIP ***");
    }
	
	private void HostPropertiesCreate(String hostName) throws ServiceException {
		System.out.println("*** INIT HOST PROPERTIES CREATE ***");
		
		if(Neo4JHost.get(hostName) == null) {
		
			Node node = new Node();
			
			/*a node has to be created for each NFFG node, with a property named “name” whose value is the NFFG name*/
			Property name_property = new Property();
			Properties pr = new Properties();
			
			name_property.setName("name");
			name_property.setValue(hostName);
			
			pr.getProperty().add(name_property);
			
			node.setProperties(pr);
		
			Labels labels = new Labels();
			labels.getLabel().add("Host");
			node.setLabels(labels);
	
			/*Post properties*/
			Response resp = client.target(BaseURI+"/node").request(MediaType.APPLICATION_XML).post(Entity.entity(node,MediaType.APPLICATION_XML),Response.class);
			
			if(resp.getStatus()!=201)
				throw new ServiceException("Node not create" + resp.getStatus());
	
			node.setId(resp.readEntity(Node.class).getId());
	
			/*Post label*/
			resp = client.target(BaseURI+"/node/"+ node.getId()  +"/labels").request(MediaType.APPLICATION_XML).post(Entity.entity(labels,MediaType.APPLICATION_XML),Response.class);
			
			if(resp.getStatus()!=204)
				throw new ServiceException("Label is not created" + resp.getStatus());
			
			/*aggiorno la mappa*/
			Neo4JHost.put(name_property.getValue(), node);
			
			System.out.println("*** FINISH HOST PROPERTIES CREATE ***");
		}

	}
	
	private void NodeHostRel(String srcNode, String destNode) throws ServiceException {
		
		System.out.println("*** INIT NODE-HOST RELATIONSHIP ***");

        Relationship rel = new Relationship();
        rel.setType("AllocatedOn");
        rel.setDstNode(Neo4JHost.get(destNode).getId());
      
         /* Insert Relationship */
        Response  resp = client.target(BaseURI+"/node/"+Neo4JNodes.get(srcNode).getId()+"/relationships").request(MediaType.APPLICATION_XML).post(Entity.entity(rel,MediaType.APPLICATION_XML),Response.class);

        if ((resp.getStatus() != 200) && (resp.getStatus() != 201)) {
            throw new ServiceException("Error creating relationship");
        }
        System.out.println("*** FINISH NODE-HOST RELATIONSHIP ***");
    }
		
	private void printBlankLine() {
		System.out.println(" ");
	}    
 }
	

	



	


