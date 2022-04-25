package br.ufs.dcomp.rabbitmq;

import java.util.Scanner;

import br.ufs.dcomp.rabbitmq.chat.Chat;
import br.ufs.dcomp.rabbitmq.strategies.ActionStrategy;
import br.ufs.dcomp.rabbitmq.strategies.SendFile;
import br.ufs.dcomp.rabbitmq.strategies.SendMessage;
import br.ufs.dcomp.rabbitmq.strategies.group.AddGroup;
import br.ufs.dcomp.rabbitmq.strategies.group.RemoveGroup;
import br.ufs.dcomp.rabbitmq.strategies.user.AddUserToGroup;
import br.ufs.dcomp.rabbitmq.strategies.user.DelFromGroup;

public class Demo {
	private static final Scanner scanner = new Scanner(System.in);

	public static String currentArrow = Input.arrow;
	private static String currentQueue = "";
	private static String currentExchange = "";
	private static String input = "";

	private static Chat chat; // eh o Context
	private static ActionStrategy strategy; // eh a interface Strategy

	private static void init() throws Exception {
		System.out.print("User: ");
		String user = scanner.nextLine().trim();
//		currentQueue = sender; // Descomentar para que o usuário receba mensagens dele mesmo
		chat = new Chat(user, "54.165.121.210", "juanbomfim22", "juanbomfim22");
		chat.channelSetup();
		chat.waitMessage();

		System.out.print(currentArrow);
	}

	private static void closeAll() throws Exception {
		for (var pair : Chat.getChannels().entrySet()) {
			pair.getValue().close();
		}
		Chat.getConnection().close();
		scanner.close();
		System.exit(0);
	}

	public static void main(String[] argv) throws Exception {
 		init();
		while (!input.equals("exit")) {
			input = scanner.nextLine().trim();
						
			if (input.startsWith("@")) {
				currentQueue = input.substring(1);
				currentExchange = "";
				currentArrow = input + Input.arrow;
			} else if(input.startsWith("#")){
				currentQueue = "";
				currentExchange = input.substring(1);
				currentArrow = input + Input.arrow;
			} else if(input.startsWith("!")) {
				// TODO: Se digitar um comando inexistente, retornar mensagem e não deixar executar
				// a strategy
				if (input.startsWith("!addGroup")) {
					strategy = new AddGroup();
				}
				if (input.startsWith("!addUser")) {
					strategy = new AddUserToGroup();
				}
				if (input.startsWith("!removeGroup")) {
					strategy = new RemoveGroup();
				}
				if (input.startsWith("!delFromGroup")) {
					strategy = new DelFromGroup();
				}
				if (input.startsWith("!upload")) {
					strategy = new SendFile(currentQueue, currentExchange);
				}  
			}
			else {
				strategy = new SendMessage(currentQueue, currentExchange);
			}

			if (strategy != null && !input.matches("(@|#)(.*)") && !input.isEmpty()) {
				chat.execute(strategy, currentArrow, input);
			}
			System.out.print(currentArrow);
		}
		closeAll();
	}
}
