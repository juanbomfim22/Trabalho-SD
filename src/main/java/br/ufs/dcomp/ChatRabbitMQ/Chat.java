package br.ufs.dcomp.ChatRabbitMQ;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class Chat {

	private static final Scanner scanner = new Scanner(System.in);
	private static final String arrow = ">> ";
	private String currentArrow;
	
	private Connection connection;	
	private Queue sendQueue;

	public Connection getConnection() {
		return connection;
	}
	
	public Queue getSendQueue() {
		return sendQueue;
	}

	public void setSendQueue(Queue sendQueue) {
		this.sendQueue = sendQueue;
	}

	
	public String getCurrentArrow() {
		return currentArrow;
	}

	public void setCurrentArrow(String currentArrow) {
		this.currentArrow = currentArrow;
	}

	public Chat(String username, String host, String name, String password) throws Exception {
		this.currentArrow = Chat.arrow;
		this.connection = connectionSetup(host, name, password);
		this.setSendQueue(new Queue(username, connection));
	}
	
	private void init() throws Exception {
//		System.out.print(currentArrow);
		sendQueue.waitMessage();
//		System.out.print(currentArrow);
	}
	
	
	/////
	private Connection connectionSetup(String host, String name, String password) throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(host); // IP da máquina virtual
		factory.setUsername(name);
		factory.setPassword(password);
		factory.setVirtualHost("/");
		return factory.newConnection();
	}
	
	public String currentDate() {
		ZoneId z = ZoneId.of("America/Sao_Paulo");
		ZonedDateTime zdt = ZonedDateTime.now(z);
		return DateTimeFormatter.ofPattern("(dd/MM/yyyy) 'às' HH:mm").format(zdt);
	}
	//////
	
	public static void main(String[] argv) throws Exception {
		System.out.print("User: ");		
		String message = "";
		String username = scanner.nextLine().trim();
		
		Chat chat = new Chat(username, "54.86.170.54", "juanbomfim22", "juanbomfim22");
		chat.init();
		
		while (!message.equals("exit")) {
			message = scanner.nextLine().trim();
			if (message.startsWith("@")) {
				chat.setCurrentArrow(message + Chat.arrow);
				chat.setSendQueue(new Queue(message.replace("@", ""), chat.connection));
			} else {
				chat.sendQueue.sendMessage(message, chat.currentDate(), chat.sendQueue.getQueueName());
			}
			System.out.print(chat.currentArrow);
		}
		chat.sendQueue.getChannel().close();
		chat.sendQueue.getConnection().close();
		scanner.close();
	}
}
