package br.ufs.dcomp.ChatRabbitMQ;

import com.rabbitmq.client.*;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Chat {

  private static final Scanner scanner = new Scanner(System.in);
  private String arrow;
  private Channel channel;
  private Connection connection;
  private String current_queue;
  private String user;

  public Chat() {
    this.arrow = ">> ";
  }

  public String getUser() {
    System.out.print("User: ");
    return scanner.nextLine();
  }
  private void setUser(String user){ 
    this.user = user;
  }

  private String getDate() {
    ZoneId z = ZoneId.of("America/Sao_Paulo");
    ZonedDateTime zdt = ZonedDateTime.now(z);
    return DateTimeFormatter.ofPattern("(dd/MM/yyyy) 'às' HH:mm").format(zdt);
  }

  private void changeChannel(String queue_name) throws Exception {
    channel = connection.createChannel();
    // (queue-name, durable, exclusive, auto-delete, params);
    channel.queueDeclare(queue_name, false, false, false, null);
  }

  private void connectionSetup(String host, String name, String password)
    throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(host); // IP dá máquina virtual
    factory.setUsername(name);
    factory.setPassword(password);
    factory.setVirtualHost("/");
    connection = factory.newConnection();
  }

  private void sendMessage(String msg) throws IOException {
    String date = getDate();
    String message = date + " " + user + " diz: " + msg;
    channel.basicPublish("", current_queue, null, message.getBytes("UTF-8"));
  }

  private void waitMessage(String queue_name)
    throws Exception {
    Consumer consumer = new DefaultConsumer(channel) {
      public void handleDelivery(
        String consumerTag,
        Envelope envelope,
        AMQP.BasicProperties properties,
        byte[] body
      )
        throws IOException {
        String message = new String(body, "UTF-8");
        System.out.print("\033[2K"); // Erase line content
        System.out.println("\r" + message);
        System.out.print(arrow);
      }
    };
    // (queue-name, autoAck, consumer);
    channel.basicConsume(queue_name, true, consumer);
  }

  private void initChat() throws Exception {
    System.out.print(arrow);
    changeChannel(current_queue);
    waitMessage(current_queue);
  }

  public static void main(String[] argv) throws Exception {
    String current_queue, message = "";
    Chat chat = new Chat();
    chat.connectionSetup(
      "172.31.27.201",
      "leticia",
      "rabbit"
    );

    current_queue = chat.getUser();
    chat.setUser(current_queue);
    chat.initChat();

    while (!message.equals("exit")) {
      message = scanner.nextLine().trim();
      if (message.startsWith("@")) {
        chat.arrow = message + ">> ";
        current_queue = message.replace("@", "");
        chat.changeChannel(current_queue);
      } else {
        chat.sendMessage(message);
      }
      System.out.print(chat.arrow);
    }
    chat.channel.close();
    chat.connection.close();
    scanner.close();
  }
}
