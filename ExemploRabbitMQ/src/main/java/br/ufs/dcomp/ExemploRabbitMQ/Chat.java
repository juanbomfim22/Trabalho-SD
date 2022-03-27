package br.ufs.dcomp.ExemploRabbitMQ;

import com.rabbitmq.client.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.io.IOException;

public class Chat {
  
  private static final Scanner sc = new Scanner(System.in);
  public static String arrow = ">> ";

  private static String getUser() {
    System.out.print("User: ");
    return sc.nextLine();
  }

  private static String getDate() {
    ZoneId z = ZoneId.of("America/Sao_Paulo");
    ZonedDateTime zdt = ZonedDateTime.now(z);

    return DateTimeFormatter.ofPattern("(dd/MM/yyyy) 'às' HH:mm").format(zdt);
  }

  private static Channel createUserQ(Connection connection, String queue_name)
    throws Exception {
      Channel channel = connection.createChannel();
      //(queue-name, durable, exclusive, auto-delete, params);
      channel.queueDeclare(queue_name, false, false, false, null);
      return channel;
  }
  
  private static Connection connectionSetup(String host, String name, String password) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(host); // IP dá máquina virtual
    factory.setUsername(name); 
    factory.setPassword(password); 
    factory.setVirtualHost("/");  
    Connection connection = factory.newConnection();
    return connection;
  }
  
  public static void waitMessage(Channel channel,String queue_name )throws Exception{
    Consumer consumer = new DefaultConsumer(channel) {
      public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)  throws IOException {
        String message = new String(body, "UTF-8");
        System.out.print("\033[2K"); // Erase line content 
        System.out.println("\r" + message);
        System.out.print(arrow);
      }
    };
                      //(queue-name, autoAck, consumer);    
    channel.basicConsume(queue_name, true,    consumer);
  }

  public static void main(String[] argv) throws Exception {
    Connection connection = connectionSetup("172.31.27.201","leticia","rabbit");
    
    String user, current_queue, input;

    user = current_queue = getUser();
    System.out.print(Chat.arrow);
    
    Channel channel = createUserQ(connection, current_queue);

    waitMessage(channel,current_queue);

    Scanner scanner = new Scanner(System.in);

    while (true) {
      input = scanner.nextLine().trim(); 
      if (input.equals("exit")) break;
      if (input.equals("")) { 
          System.out.print(Chat.arrow);
          continue;   
      }
      if (input.startsWith("@")) {
        Chat.arrow = input + ">> ";
        current_queue = input.replace("@", "");
        channel = createUserQ(connection, current_queue);
        System.out.print(Chat.arrow);
        continue;
      }
     
        String date = getDate();
        String message = date + " " + user + " diz: " + input; 
        channel.basicPublish(
          "",
          current_queue,
          null,
          message.getBytes("UTF-8")
        );
        System.out.print(Chat.arrow);
    }
      channel.close();
      connection.close();
      scanner.close();
      System.exit(0);

      
  }
}