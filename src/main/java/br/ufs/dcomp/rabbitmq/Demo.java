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

	private static Chat chat; // eh o Context
	private static ActionStrategy strategy; // eh a interface Strategy

	private static void init() throws Exception {
		System.out.print("User: ");
		String user = scanner.nextLine().trim();
//		currentQueue = sender; // Descomentar para que o usuário receba mensagens dele mesmo
		chat = new Chat(user, "44.202.121.76", "juanbomfim22", "juanbomfim22");
		chat.channelSetup();
		chat.waitMessage();

		System.out.print(currentArrow);
	}

	public static void main(String[] argv) throws Exception {
		String input = "";
		init();

		while (!input.equals("exit")) {
			input = scanner.nextLine().trim();

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
			else { 
				if (input.startsWith("@") || input.startsWith("#")) {
					if(input.startsWith("@")) {
						currentQueue = input.substring(1);
						currentExchange = "";
					} else {
						currentQueue = "";
						currentExchange = input.substring(1);
					}
					currentArrow = input + Input.arrow;
					System.out.print(currentArrow);
					continue;
				} else {
					// Comando normal
					strategy = new SendMessage(currentQueue, currentExchange);
				}
			}
				
			if (strategy != null && !input.equals("")) {
				chat.execute(strategy, currentArrow, input);
			}
			System.out.print(currentArrow);
		}
		Chat.getChannel().get("mensagens").close();
		Chat.getConnection().close();
		scanner.close();
		System.exit(0);
	}
}
