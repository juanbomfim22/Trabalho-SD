package br.ufs.dcomp.rabbitmq.strategies.group;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import br.ufs.dcomp.rabbitmq.PropertiesReader;

public abstract class AbstractListInfo {
	public abstract void action(String json);
	
	public String generateAuthHeaderValue(String username, String password) {
		String usernameAndPassword = username + ":" + password;
		String authorizationHeaderValue = "Basic "
				+ java.util.Base64.getEncoder().encodeToString(usernameAndPassword.getBytes());
		return authorizationHeaderValue;
	}

	public String getJsonFromPath(String restResource, String path) {
		String authorizationHeaderName = "Authorization";
		String authorizationHeaderValue = generateAuthHeaderValue(
				PropertiesReader.getProperty("rabbit.username"), 
				PropertiesReader.getProperty("rabbit.password"));
		
		Client client = ClientBuilder.newClient();
		Response resposta = client.target(restResource)
				.path(path)
				.request(MediaType.APPLICATION_JSON)
				.header(authorizationHeaderName, authorizationHeaderValue)
				.get();																														// the

		if (resposta.getStatus() != 200) return null;
		return resposta.readEntity(String.class);
	}
}
