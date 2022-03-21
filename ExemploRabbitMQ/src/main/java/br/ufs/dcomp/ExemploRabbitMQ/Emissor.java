package br.ufs.dcomp.ExemploRabbitMQ;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class Emissor {

  // static: cria apenas um Scanner e compartilha em todas
  //         as instâncias do Emissor
  // final:  não pode alterar a referência (objeto), ou o método
  //         mas pode mudar o conteúdo do objeto
  // public final class Emissor: não pode ser estendida, herdadaA
  private static final Scanner sc = new Scanner(System.in);

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

  public static void main(String[] argv) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("44.202.217.222"); // Alterar
    factory.setUsername("juanbomfim22"); // Alterar
    factory.setPassword("juanbomfim22"); // Alterar
    factory.setVirtualHost("/");
    Connection connection = factory.newConnection();

    /* Perguntas */
    // O que fazer quando estiver >> e o usuario digitar uma mensagem?
    //  - Enviar para ele mesmo + mostra msg
    //  - Não faz nada
    // O que acontece quando o usuário chaveia para ele mesmo?
    
    String user, current_queue, input;

    user = current_queue = getUser();
    System.out.print(Receptor.arrow);

    Receptor.waitMessage(current_queue);

    Channel channel = createUserQ(connection, current_queue);

    Scanner scanner = new Scanner(System.in);

    while (true) {
      input = scanner.nextLine().trim(); // deve ser redefinida a String input = .. toda vez do laço
      if (input.equals("exit")) break;
      if (input.equals("")) { 
          System.out.print(Receptor.arrow);
          continue;   
      }
      if (input.startsWith("@")) {
        Receptor.arrow = input + ">> ";
        current_queue = input.replace("@", "");
        channel = createUserQ(connection, current_queue);
        System.out.print(Receptor.arrow);
        continue;
      }
     
        String date = getDate();
        String message = date + " " + user + " diz: " + input; // " " é o separador
        channel.basicPublish(
          "",
          current_queue,
          null,
          message.getBytes("UTF-8")
        );
        System.out.print(Receptor.arrow);
    }
      channel.close();
      connection.close();
      scanner.close();
      System.exit(0);

      
  }
}
