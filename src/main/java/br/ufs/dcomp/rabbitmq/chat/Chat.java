package br.ufs.dcomp.rabbitmq.chat;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import br.ufs.dcomp.rabbitmq.Input;
import br.ufs.dcomp.rabbitmq.proto.MensagemProto;
import br.ufs.dcomp.rabbitmq.strategies.ActionStrategy;

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

	public static Map<String, Channel> getChannel() {
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
		Consumer consumer = new DefaultConsumer(channels.get("mensagens")) {
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
					byte[] body) throws IOException {
				MensagemProto.Mensagem contatoMensagem = MensagemProto.Mensagem.parseFrom(body);
				MensagemProto.Conteudo conteudo = contatoMensagem.getConteudo();

				String emissor = contatoMensagem.getEmissor();
				String data = contatoMensagem.getData();
				String hora = contatoMensagem.getHora();
				String grupo = contatoMensagem.getGrupo();
				String nome = conteudo.getNome();
				
				if(nome.equals("")) nome = "DEFAULT.txt";

				byte[] corpoMensagem = conteudo.getCorpo().toByteArray();

				System.out.print("\033[2K"); // Erase line content

				// Descomentar essas 3 e comentar as 3 debaixo pra testar o arquivo envios
				
				Path path = Paths.get("/tmp/"+nome);
				
				Files.write(path, corpoMensagem);
				System.out.println("\r" + "(" + data + " às " + hora + ") " + "Arquivo \"" + nome + "\" recebido de @"
						+ emissor + "!");

//				String strMensagem = new String(corpoMensagem, "UTF-8"); // FORMATAR DISPLAY DE MENSAGEM
//				String grupoEmissor = grupo.equals("") ? emissor : emissor + "#" + grupo;
//				System.out.println("\r" + "(" + data + " às " + hora + ") " + grupoEmissor + " diz: " + strMensagem);
			}
		};

		channels.get("mensagens").basicConsume(username, true, consumer); // a fila tem o mesmo nome do username
	}

	public void execute(ActionStrategy strategy, String arrow, String input) throws Exception {
		strategy.run(channels.get("mensagens"), new Input(arrow, input), username);
		strategy.run(channels.get("arquivos"), new Input(arrow, input), username);
	}

}
