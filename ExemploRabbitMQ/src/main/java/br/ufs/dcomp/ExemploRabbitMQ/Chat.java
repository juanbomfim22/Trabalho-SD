package br.ufs.dcomp.ExemploRabbitMQ;

import com.rabbitmq.client.*;
import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

 

// Ver caso em que:
// - Esquece os parametros necessarios para executar o comando
// - Adiciona/Remove usuário a/de um grupo inexistente
// - Adiciona/Remove usuário inexistente a/de um grupo
// - Cria um grupo com nome vazio
// - Cria um grupo já existente
// - Remove um grupo inexistente
// - Chaveia para um grupo sem estar criado e envia mensagem
// - Envia mensagem a um usuario inexistente (sem fila)
// - Usuário está no grupo e envia uma mensagem a todos: recebe a própria ou não?
// - Usuário chaveia para o grupo depois de uma mensagem ter sido enviada
// - Usuário está no grupo e o deleta, o que fazer com a seta?

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

    return DateTimeFormatter.ofPattern("(dd/MM/yyyy 'às' HH:mm)").format(zdt);
  }

  private static Channel createUserQ(Connection connection, String queue_name)
    throws Exception {
    Channel channel = connection.createChannel();
    //(queue-name, durable, exclusive, auto-delete, params);
    channel.queueDeclare(queue_name, false, false, false, null);
    return channel;
  }

  private static Connection connectionSetup(
    String host,
    String name,
    String password
  )
    throws Exception {
    ConnectionFactory factory = new ConnectionFactory();
    factory.setHost(host); // IP dá máquina virtual
    factory.setUsername(name);
    factory.setPassword(password);
    factory.setVirtualHost("/");
    Connection connection = factory.newConnection();
    return connection;
  }

  public static void waitMessage(Channel channel, String queue_name)
    throws Exception {
    Consumer consumer = new DefaultConsumer(channel) {
      public void handleDelivery(
        String consumerTag,
        Envelope envelope,
        AMQP.BasicProperties properties,
        byte[] body
      )
        throws IOException {
        MensagemProto.Mensagem contatoMensagem = MensagemProto.Mensagem.parseFrom(
          body
        );
        String emissor = contatoMensagem.getEmissor();
        String data = contatoMensagem.getData();
        String hora = contatoMensagem.getHora();
        String grupo = contatoMensagem.getGrupo();
        MensagemProto.Conteudo conteudo = contatoMensagem.getConteudo();

        byte[] corpoMensagem = conteudo.getCorpo().toByteArray();

        // FORMATAR DISPLAY DE MENSAGEM
        String strMensagem = new String(corpoMensagem, "UTF-8");

        String grupoEmissor = grupo.equals("") ? emissor : emissor + "#" + grupo;

        System.out.print("\033[2K"); // Erase line content
        System.out.println(
          "\r" +
          "(" +
          data +
          " às " +
          hora +
          ") " +
          grupoEmissor +
          " diz: " +
          strMensagem
        );
        System.out.print(arrow);
      }
    };
    //(queue-name, autoAck, consumer);
    channel.basicConsume(queue_name, true, consumer);
  }

  public static void addGroup(Channel channel, String groupName)
    throws Exception {
    if (groupName.trim().equals("")) {
      throw new Exception("Não pode criar um grupo sem nome");
    }
    channel.exchangeDeclare(groupName, "fanout");
  }

  public static void removeGroup(Channel channel, String groupName)
    throws Exception {
    if (groupName.trim().equals("")) {
      throw new Exception("Não pode remover um grupo de nome vazio");
    }
    channel.exchangeDelete(groupName);
  }

  public static void addUsertoGroup(
    Channel channel,
    String userQueue,
    String groupName
  )
    throws Exception {
    channel.queueBind(userQueue, groupName, "");
  }

  public static void delUserFromGroup(
    Channel channel,
    String userQueue,
    String groupName
  )
    throws Exception {
    channel.queueUnbind(userQueue, groupName, "");
  }

  public static byte[] createMensagemProto(
    String emissor,
    String data,
    String hora,
    String grupo,
    MensagemProto.Conteudo conteudo
  ) {
    MensagemProto.Mensagem.Builder builderMensagem = MensagemProto.Mensagem.newBuilder();
    builderMensagem.setEmissor(emissor);
    builderMensagem.setData(data);
    builderMensagem.setHora(hora);
    builderMensagem.setGrupo(grupo);
    builderMensagem.setConteudo(conteudo);
    MensagemProto.Mensagem contatoMensagem = builderMensagem.build();
    byte[] buffer = contatoMensagem.toByteArray(); //retorna a mensagem em bytes a ser enviada
    return buffer;
  }

  public static MensagemProto.Conteudo createConteudoProto(
    String tipo,
    byte[] corpo,
    String nome
  ) {
    MensagemProto.Conteudo.Builder bConteudo = MensagemProto.Conteudo.newBuilder();
    bConteudo.setTipo(tipo);
    bConteudo.setCorpo(com.google.protobuf.ByteString.copyFrom(corpo));
    bConteudo.setNome(nome);
    MensagemProto.Conteudo contatoConteudo = bConteudo.build();
    return contatoConteudo;
  }

  public static void main(String[] argv) throws Exception {
    Connection connection = connectionSetup(
      "3.95.134.139",
      "juanbomfim22",
      "juanbomfim22"
    );

    String user, current_queue, input, current_exchange = "";

    user = current_queue = getUser();
    System.out.print(Chat.arrow);

    Channel channel = createUserQ(connection, current_queue);

    waitMessage(channel, current_queue);

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
      if (input.startsWith("#")) {
        Chat.arrow = input + ">> ";
        String group_tmp = input.replace("#", "");

        current_exchange = group_tmp;

        System.out.print(Chat.arrow);
        continue;
      }
      if (input.contains("!addGroup")) { // O nome do grupo não pode ser vazio
        String group_tmp = input.substring((input.lastIndexOf(" ")) + 1);
        addGroup(channel, group_tmp);
        addUsertoGroup(channel, user, group_tmp);

        System.out.println("Grupo " + group_tmp + " criado");
        System.out.print(Chat.arrow);
        continue;
      }

      if (input.contains("!addUser")) {
        String group_tmp = input.substring((input.lastIndexOf(" ")) + 1);

        addUsertoGroup(
          channel,
          input.split(" ")[1],
          input.substring((input.lastIndexOf(" ")) + 1)
        );

        System.out.println(
          "Usuario " + input.split(" ")[1] + " adicionado ao grupo " + group_tmp
        );
        System.out.print(Chat.arrow);
        continue;
      }

      if (input.contains("!delFromGroup")) {
        delUserFromGroup(
          channel,
          input.split(" ")[1],
          input.substring((input.lastIndexOf(" ")) + 1)
        );

        System.out.println(
          "Usuario " +
          input.split(" ")[1] +
          " removido do grupo " +
          input.substring((input.lastIndexOf(" ")) + 1)
        );
        System.out.print(Chat.arrow);

        continue;
      }

      if (input.contains("!removeGroup")) {
        String group_tmp = input.substring((input.lastIndexOf(" ")) + 1);
        removeGroup(channel, group_tmp);

        System.out.println("Grupo " + group_tmp + " removido");
        System.out.print(Chat.arrow);
        continue;
      }

      String date = getDate();
      MensagemProto.Conteudo conteudo = createConteudoProto(
        "text/plain",
        input.getBytes("UTF-8"),
        ""
      ); //mensagens sempre do tipo text/plain e sem nome
      byte[] mensagemProto = createMensagemProto(
        user,
        date.split(" ")[0].replace("(", ""),
        date.split(" ")[2].replace(")", ""),
        current_exchange,
        conteudo
      );
      channel.basicPublish(
        current_exchange,
        current_queue,
        null,
        mensagemProto
      );
      System.out.print(Chat.arrow);
    }
    channel.close();
    connection.close();
    scanner.close();
    System.exit(0);
  }
}
