package br.ufs.dcomp.ExemploRabbitMQ;

import com.rabbitmq.client.*;

import java.io.IOException;

public class Receptor {
  public static String arrow = ">> ";
  
  public static void waitMessage(String queue_name) throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost("44.202.217.222"); // Alterar
    factory.setUsername("juanbomfim22"); // Alterar
    factory.setPassword("juanbomfim22"); // Alterar factory.setVirtualHost("/");  
    Connection connection = factory.newConnection();
    Channel channel = connection.createChannel();

    /*
     * A fila deve ser declarada aqui no Receptor também
     * Caso ela já exista, ignora o código
     * Caso não exista, cria a fila
     */
                      //(queue-name, durable, exclusive, auto-delete, params); 
    channel.queueDeclare(queue_name, false,   false,     false,       null);
    
    //System.out.println(" [*] Esperando recebimento de mensagens...");

    Consumer consumer = new DefaultConsumer(channel) {
      /*
       * Por dentro da função, existe um loop para o recebimento de mensagens
       */
      public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body)           throws IOException {

        String message = new String(body, "UTF-8");
      
        System.out.print("\033[2K"); // Erase line content 
        System.out.println("\r" + message);
        System.out.print(arrow);
                        //(deliveryTag,              multiple);
        //channel.basicAck(envelope.getDeliveryTag(), false);
      }
    };
                      //(queue-name, autoAck, consumer);    
    channel.basicConsume(queue_name, true,    consumer);
  }
}