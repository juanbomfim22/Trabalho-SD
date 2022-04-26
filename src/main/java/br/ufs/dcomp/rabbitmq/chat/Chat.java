package br.ufs.dcomp.rabbitmq.chat;

import br.ufs.dcomp.rabbitmq.Input;
import br.ufs.dcomp.rabbitmq.proto.PROTO;
import br.ufs.dcomp.rabbitmq.strategies.ActionStrategy;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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

    // Cria duas filas
    channels.get("mensagens").queueDeclare(username, false, false, false, null);
    channels.get("arquivos").queueDeclare(username, false, false, false, null);
  }

  public void waitMessage() throws Exception {
    Consumer consumerMessages = PROTO.handle(channels, "mensagens");
    Consumer consumerFiles = PROTO.handle(channels, "arquivos");

    channels.get("mensagens").basicConsume(username, true, consumerMessages); // a fila tem o mesmo nome do username
    new Thread() {
      @Override
      public void run() {
        try {
          channels.get("arquivos").basicConsume(username + "Files", true, consumerFiles); // a fila tem o mesmo nome do username
        } catch (IOException e) {
          System.out.println(e);
        }
      }
    }.start();
  }

  public void execute(ActionStrategy strategy, String arrow, String input)
    throws Exception {
    strategy.run(channels.get("mensagens"), new Input(arrow, input), username);
  }
}
