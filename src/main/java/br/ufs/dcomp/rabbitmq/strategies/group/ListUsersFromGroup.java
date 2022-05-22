package br.ufs.dcomp.rabbitmq.strategies.group;

import com.rabbitmq.client.Channel;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

//import com.google.gson.Gson;
import br.ufs.dcomp.rabbitmq.chat.Input;
import br.ufs.dcomp.rabbitmq.strategies.ActionStrategy;
import br.ufs.dcomp.rabbitmq.strategies.user.AddUserToGroup;

public class ListUsersFromGroup implements ActionStrategy {
	
	@Override
	public void run(Channel channel, Input input, String username) throws Exception{
			String exchange = input.getArgs(0);	
			try {
            
            String username2 = "leticia";
            String password2 = "rabbit";
     
            String usernameAndPassword = username2 + ":" + password2;
            String authorizationHeaderName = "Authorization";
            String authorizationHeaderValue = "Basic " + java.util.Base64.getEncoder().encodeToString( usernameAndPassword.getBytes() );
     
            // Perform a request
            //link do nome do DNS do load balancer
            String restResource = "http://rabbitmq-sd-t01-lb-1ad061f6c2e651b9.elb.us-east-1.amazonaws.com";
            Client client = ClientBuilder.newClient();
            Response resposta = client.target( restResource )
            	.path("/api/exchanges/%2f/"+exchange+"/bindings/source") // lista todos os binds que tem o exchange "ufs" como source	
            	.request(MediaType.APPLICATION_JSON)
                .header( authorizationHeaderName, authorizationHeaderValue ) // The basic authentication header goes here
                .get();     // Perform a post with the form values
           
            if (resposta.getStatus() == 200) { //requisição atendida com sucesso
            	String json = resposta.readEntity(String.class);
            	
        JSONParser parser = new JSONParser();

        try {
            Object obj = parser.parse(json);
            JSONArray users = (JSONArray) obj;

            for (int i = 0; i < users.size(); i++) {
                JSONObject user = (JSONObject) users.get(i);
                if(!(user.get("destination").toString().contains("arquivos"))){
                    System.out.print(user.get("destination")+", ");
                }
            }
            System.out.print("\n");

        } catch (Exception e) {
            e.printStackTrace();
        }
            } else {
                System.out.println(resposta.getStatus());
            }   
		} catch (Exception e) {
			e.printStackTrace();
		}			
	}
}
