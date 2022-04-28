package br.ufs.dcomp.rabbitmq.chat;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import br.ufs.dcomp.rabbitmq.strategies.ActionStrategy;
import br.ufs.dcomp.rabbitmq.util.PROTO;

/*
 * Doesn't know the concrete action method (strategy) user has
 * picked. It uses common strategy interface to delegate running command
 * to strategy object. It can be used to save to database.
 */
public class Chat {

  private String username; // obs: o nome do usuario User: eh o nome da fila queue
  private String host;
  private String name;
  private String password;

  private static Map<String, Channel> channels = new HashMap<>();
  private static Connection connection;

  public static Map<String, Channel> getChannels() {
    return channels;
  }

  public static Connection getConnection() {
    return connection;
  }

  public Chat(String username, String host, String name, String password) {
    this.username = username;
    this.host = host;
    this.name = name;
    this.password = password;
  }

  public void channelSetup() throws IOException, Exception {
    if (connection == null) {
      ConnectionFactory factory = new ConnectionFactory();
      factory.setHost(host); // IP da máquina virtual
      factory.setUsername(name);
      factory.setPassword(password);
      factory.setVirtualHost("/");
      connection = factory.newConnection();
    }
    // Cria dois canais
    channels.put("mensagens", connection.createChannel());
    channels.put("arquivos", connection.createChannel());

    // Cria duas filas em dois canais distintos para cada usuário
    channels.get("mensagens").queueDeclare(username, false, false, false, null);
    channels.get("arquivos").queueDeclare(username+".arquivos", false, false, false, null);
  }

  public void waitMessage() throws Exception {
	 DeliverCallback consumerMessages = PROTO.constructCallback("mensagens");
	 DeliverCallback consumerFiles = PROTO.constructCallback("arquivos");
	 channels.get("mensagens").basicConsume(username, true, consumerMessages, consumerTag -> {}); // a fila tem o mesmo nome do username
	 channels.get("arquivos").basicConsume(username+".arquivos", true, consumerFiles, consumerTag -> {}); // a fila tem o mesmo nome do username
  }

  public void execute(ActionStrategy strategy, String arrow, String input)
    throws Exception {	   
	  strategy.run(channels.get("mensagens"), new Input(arrow, input), username);
  }
}
