package br.ufs.dcomp.ExemploRabbitMQ;

import com.rabbitmq.client.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;
import java.io.IOException;
//import com.google.protobuf.util.JsonFormat;

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
       MensagemProto.Mensagem contatoMensagem = MensagemProto.Mensagem.parseFrom(body);
       String emissor = contatoMensagem.getEmissor();
       String data = contatoMensagem.getData();
       String hora = contatoMensagem.getHora();
       String grupo = contatoMensagem.getGrupo();
       MensagemProto.Conteudo conteudo = contatoMensagem.getConteudo();
       byte[] corpoMensagem = conteudo.getCorpo().toByteArray();
       String strMensagem= new String(corpoMensagem, "UTF-8");
       /* String message = new String(body, "UTF-8");
        System.out.print("\033[2K"); // Erase line content 
        System.out.println("\r" + message);
        System.out.print(arrow);*/
      }
    };
                      //(queue-name, autoAck, consumer);    
    channel.basicConsume(queue_name, true,    consumer);
  }
  
  public static void createGroup(Channel channel, String groupName) throws Exception{
    channel.exchangeDeclare(groupName,"fanout");
  }
  
  public static void addUsertoGroup(Channel channel, String userQueue, String groupName)throws Exception {
    channel.queueBind(userQueue,groupName,"");
  }
  
  public static byte[] createMensagemProto(String emissor, String data, String hora, String grupo, MensagemProto.Conteudo conteudo){
    MensagemProto.Mensagem.Builder builderMensagem= MensagemProto.Mensagem.newBuilder();
    builderMensagem.setEmissor(emissor);
    builderMensagem.setData(data);
    builderMensagem.setHora(hora);
    builderMensagem.setGrupo(grupo);
    builderMensagem.setConteudo(conteudo);
    MensagemProto.Mensagem contatoMensagem= builderMensagem.build();
    byte [] buffer = contatoMensagem.toByteArray(); //retorna a mensagem em bytes a ser enviada
    
  }
  
  public static MensagemProto.Conteudo createConteudoProto(String tipo, byte[] corpo, String nome){
    MensagemProto.Conteudo.Builder bConteudo= MensagemProto.Conteudo.newBuilder();
    bConteudo.setTipo(tipo);
    bConteudo.setCorpo(corpo);
    bConteudo.setNome(nome);
    MensagemProto.Conteudo contatoConteudo = bConteudo.build();
    return contatoConteudo;
  }
  

  public static void main(String[] argv) throws Exception {
    Connection connection = connectionSetup("172.31.27.201","leticia","rabbit");
    
    String user, current_queue, input, current_exchange="";

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
       /*channel = createUserQ(connection, current_queue); Não precisa criar outro canal*/
        System.out.print(Chat.arrow);
        continue;
      }
      if(input.contains("!addGroup")){
        createGroup(channel, input.substring((input.lastIndexOf(" "))+1));
        addUsertoGroup(channel, user ,input.substring((input.lastIndexOf(" "))+1));
        System.out.println("Grupo "+input.substring((input.lastIndexOf(" "))+1)+" criado");
      }
      
      if(input.contains("!addUser")){
        addUsertoGroup(channel, input.split(" ")[1] ,input.substring((input.lastIndexOf(" "))+1));
      
        System.out.println("Usuario "+input.split(" ")[1]+" adicionado ao grupo "+input.substring((input.lastIndexOf(" "))+1));
      }
     
        String date = getDate();
        String message = date + " " + user + " diz: " + input; 
        channel.basicPublish(current_exchange, current_queue, null, message.getBytes("UTF-8"));
        System.out.print(Chat.arrow);
    }
      channel.close();
      connection.close();
      scanner.close();
      System.exit(0);

  }

}
