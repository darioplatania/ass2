package it.polito.dp2.NFV.lab2.tests;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import it.polito.dp2.NFV.*;
import it.polito.dp2.NFV.lab2.AlreadyLoadedException;
import it.polito.dp2.NFV.lab2.ExtendedNodeReader;
import it.polito.dp2.NFV.lab2.NoGraphException;
import it.polito.dp2.NFV.lab2.ReachabilityTester;
import it.polito.dp2.NFV.lab2.ReachabilityTesterException;
import it.polito.dp2.NFV.lab2.ReachabilityTesterFactory;
import it.polito.dp2.NFV.lab2.ServiceException;
import it.polito.dp2.NFV.lab2.UnknownNameException;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;


public class NFVTests {

	private static NfvReader referenceNfvReader;	// reference data generator
	private static ReachabilityTester testReachabilityTester;	// implementation under test
	private static long testcase;
	private static NffgReader referenceNffgReader;
	private static NodeReader referenceNodeReader;
	private static String referenceNffgName;
	private static int referenceNffgNodeSize;
	private static int referenceHostNodeSize;
	private static 	Set<HostReader> referenceReachableHostReaders;

	private static Client client;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		// Create reference data generator
		System.setProperty("it.polito.dp2.NFV.NfvReaderFactory", "it.polito.dp2.NFV.Random.NfvReaderFactoryImpl");
		referenceNfvReader = NfvReaderFactory.newInstance().newNfvReader();

		// set referenceNFFGName and referenceNffgNodeSize
		Set<NffgReader> referenceNffgs = referenceNfvReader.getNffgs(null);
		if(referenceNffgs.size()!=0){
			TreeSet<NffgReader> rts = new TreeSet<NffgReader>(new NamedEntityReaderComparator());
			rts.addAll(referenceNffgs);
			referenceNffgReader = rts.iterator().next();
			referenceNodeReader = referenceNffgReader.getNodes().iterator().next();
			referenceNffgName = referenceNffgReader.getName();
			referenceNffgNodeSize = referenceNffgReader.getNodes().size();
			referenceReachableHostReaders =  new TreeSet<HostReader>(new NamedEntityReaderComparator());
			setReachableHosts(referenceNodeReader);

			System.out.println("DEBUG: referenceNffgName: "+referenceNffgName);
			System.out.println("DEBUG: referenceNffgNodeSize: "+referenceNffgNodeSize);
		}
		
		// set referenceHostNodeSize
		
		Set<HostReader> referenceHosts = new HashSet<>();
		for (NodeReader nodeR : referenceNffgReader.getNodes()) {
			referenceHosts.add(nodeR.getHost());
		}
		//Set<HostReader> referenceHosts = referenceNfvReader.getHosts();
		referenceHostNodeSize = referenceHosts.size();		
		System.out.println("DEBUG: referenceHostNodeSize: "+referenceHostNodeSize);

		// read testcase property
		Long testcaseObj = Long.getLong("it.polito.dp2.NFV.Random.testcase");
		if (testcaseObj == null)
			testcase = 0;
		else
			testcase = testcaseObj.longValue();

		client = ClientBuilder.newClient();
	}

	private static void setReachableHosts(NodeReader nodeReader) {
		Set<NodeReader> visitedNodes = new HashSet<NodeReader>();
		visit(nodeReader, visitedNodes);
	}

	// recursively visits reachable nodes and collects reachable hosts
	private static void visit(NodeReader nodeReader, Set<NodeReader> visitedNodes) {
		if (!visitedNodes.contains(nodeReader)) {
			visitedNodes.add(nodeReader);
			HostReader hr = nodeReader.getHost();
			if (hr!=null)
				referenceReachableHostReaders.add(hr);
			Set<LinkReader> links = nodeReader.getLinks();
			for (LinkReader lr:links) {
				visit(lr.getDestinationNode(), visitedNodes);
			}
		}	
	}

	@Before
	public void setUp() throws Exception {
		assertNotNull(referenceNffgName);
	}

	// method for comparing two non-null strings    
	private void compareString(String rs, String ts, String meaning) {
		assertNotNull("NULL "+meaning, ts);
		assertEquals("Wrong "+meaning, rs, ts);		
	}

	private void createClient() throws ReachabilityTesterException {
		// Create client under test
		try {
			testReachabilityTester = ReachabilityTesterFactory.newInstance().newReachabilityTester();
		} catch (FactoryConfigurationError fce) {
			fce.printStackTrace();
		}
		assertNotNull("Internal tester error during test setup: null reference", referenceNfvReader);
		assertNotNull("Could not run test: the implementation under test generated a null ReachabilityTester", testReachabilityTester);
	}

	@Test
	public final void testLoad() throws AuxiliaryTestClientException {
		System.out.println("DEBUG: starting testLoad");
		try {
			// create client under test
			createClient();
			
			// create additional client for tracking added nodes
			AuxiliaryTestClient ct = new AuxiliaryTestClient(client);
			
			// check initially graph is not loaded
			assertEquals("Initially no graph should be loaded", false, testReachabilityTester.isLoaded(referenceNffgName));
			
			// load graph
			testReachabilityTester.loadGraph(referenceNffgName);

			// check right number of nodes has been created
			assertEquals("Wrong number of nodes", referenceNffgNodeSize+referenceHostNodeSize, ct.getAddedNodes());
			
			// check finally graph results loaded
			assertEquals("Finally graph should result loaded", true, testReachabilityTester.isLoaded(referenceNffgName));

		} catch (ReachabilityTesterException | UnknownNameException | ServiceException | AlreadyLoadedException e) {
			fail("Unexpected exception thrown: "+e.getClass().getName());
		}
	}

	@Test
	public final void testReachability() {
		System.out.println("DEBUG: starting testReachability");
		try {
			// create client under test
			createClient();
			
			// load graph
			testReachabilityTester.loadGraph(referenceNffgName);
			
			// get extended node readers
			Set<ExtendedNodeReader> enodes = testReachabilityTester.getExtendedNodes(referenceNffgName);
			assertNotNull("the implementation under test generated a null set of extended node readers", enodes);
			
			// look for one that has the reference name
			ExtendedNodeReader found=null;
			for (ExtendedNodeReader reader:enodes) {
				if (reader!=null && referenceNodeReader.getName().equals(reader.getName()))
					found = reader;
			}
			
			assertNotNull("An ExtendedNodeReader with the expected name was not found in the returned set", found);
			assertNotNull("The implenentation under test returned a null set of reachable hosts", found.getReachableHosts());
			// check that the number of elements matches
			assertEquals("wrong Number of reachable hosts", found.getReachableHosts().size(), referenceReachableHostReaders.size());

			TreeSet<HostReader> tts = new TreeSet<HostReader>(new NamedEntityReaderComparator());
			tts.addAll(found.getReachableHosts());
			Iterator<HostReader> titer = tts.iterator();
			Iterator<HostReader> riter = referenceReachableHostReaders.iterator();
			
			while(riter.hasNext() && titer.hasNext()){
				HostReader rhost = riter.next();
				HostReader thost = titer.next();
				compareString(rhost.getName(),thost.getName(),"host name");
			}
			
		} catch (ReachabilityTesterException | UnknownNameException | ServiceException | NoGraphException | AlreadyLoadedException e) {
			fail("Unexpected exception thrown: "+e.getClass().getName());
		}
	}

	@Test(expected = UnknownNameException.class)
	public final void testWrongGetExtendedHosts() throws ReachabilityTesterException, UnknownNameException, ServiceException, NoGraphException, AlreadyLoadedException {
		System.out.println("DEBUG: starting testWrongReachability");
			// create client under test
			createClient();
			
			// load graph
			testReachabilityTester.loadGraph(referenceNffgName);

			// try to execute load an existing NF-FG with name referenceNffgName and call testReachability using nodes that do not belong to the graph
			testReachabilityTester.getExtendedNodes("nonExistingNffg");
	}

	@Test(expected = UnknownNameException.class)
	public final void testWrongLoad() throws ReachabilityTesterException, UnknownNameException, ServiceException, AlreadyLoadedException {
		System.out.println("DEBUG: starting testWrongLoad");
			// create client under test
			createClient();
			
			// try to load a non-existing NF-FG 
			testReachabilityTester.loadGraph("nonexistingNFFGName");
	}

	@Test(expected = AlreadyLoadedException.class)
	public final void testDoubleLoad() throws ReachabilityTesterException, UnknownNameException, ServiceException, AlreadyLoadedException {
		System.out.println("DEBUG: starting testWrongLoad");
			// create client under test
			createClient();
			
			// load graph
			testReachabilityTester.loadGraph(referenceNffgName);
			
			// load graph again
			testReachabilityTester.loadGraph(referenceNffgName);
	}

}

class NamedEntityReaderComparator implements Comparator<NamedEntityReader> {
    public int compare(NamedEntityReader f0, NamedEntityReader f1) {
    	return f0.getName().compareTo(f1.getName());
    }
}
