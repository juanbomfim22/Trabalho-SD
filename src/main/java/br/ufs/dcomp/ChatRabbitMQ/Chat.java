package br.ufs.dcomp.ChatRabbitMQ;
import java.util.Scanner;

public class Chat {
	private static final Scanner scanner = new Scanner(System.in);
	private static final String arrow = ">> ";
	private String currentArrow = arrow;
	private Queue sendQueue;
	
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

	public Chat(String username, CustomConnection customConnection) throws Exception {
		this.currentArrow = Chat.arrow;
		this.setSendQueue(new Queue(username, customConnection.getConnection(), customConnection.getChannel()));
	}

	private void init() throws Exception {
		System.out.print(currentArrow);
		sendQueue.waitMessage(currentArrow);
	}

	public static void main(String[] argv) throws Exception {
		System.out.print("User: ");
		String message = "";
		String username = scanner.nextLine().trim();

		CustomConnection custom = new CustomConnection("54.83.142.41", "juanbomfim22", "juanbomfim22");
		
		Chat chat = new Chat(username, custom); 
		chat.init();
		
		while (true) {
			message = scanner.nextLine().trim(); 
			if (message.equals("exit")) break;
			if (message.startsWith("@")) {
				Queue queue = new Queue(message.replace("@", ""), custom.getConnection(), custom.getChannel());
				chat.setCurrentArrow(message + Chat.arrow);
				chat.setSendQueue(queue);
			} else {
				chat.sendQueue.sendMessage(message, chat.sendQueue.getQueueName());
			}
			System.out.print(chat.currentArrow);
		}

		custom.getChannel().close();
		custom.getConnection().close();
		scanner.close();
	}
}
