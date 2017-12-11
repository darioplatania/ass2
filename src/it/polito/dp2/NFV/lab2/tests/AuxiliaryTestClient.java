package it.polito.dp2.NFV.lab2.tests;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;

public class AuxiliaryTestClient {

	private static String base_url;
	Client client;
	
	int initialNumberOfNodes;

	public AuxiliaryTestClient() throws AuxiliaryTestClientException{
		init(ClientBuilder.newClient());
	}

	public AuxiliaryTestClient(Client client) throws AuxiliaryTestClientException {
		init(client);
	}
	
	private void init(Client client) throws AuxiliaryTestClientException {
		this.client=client;
		base_url = System.getProperty("it.polito.dp2.NFV.lab2.URL")+"/data";
		if (base_url==null)
			throw new AuxiliaryTestClientException("URL property not set");
		initialNumberOfNodes = getCurrentNumberOfNodes();		
	}

	private int getCurrentNumberOfNodes() throws AuxiliaryTestClientException {
		try {
			String response =client.target(base_url)
					.path("currentNodes").request(MediaType.TEXT_PLAIN).get(String.class);
			return Integer.valueOf(response);
		} catch (Exception e) {
			throw new AuxiliaryTestClientException("Unable to get current number of nodes from service");
		}
	}

	public int getAddedNodes() throws AuxiliaryTestClientException {
		return getCurrentNumberOfNodes()-initialNumberOfNodes;
	}

}
