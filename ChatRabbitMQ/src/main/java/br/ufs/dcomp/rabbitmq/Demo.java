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

//		currentQueue = sender; // Descomentar para que o usuário recebe mensagens dele mesmo

		chat = new Chat(user, "44.201.165.213", "juanbomfim22", "juanbomfim22");
		chat.startConnection();
		chat.waitMessage();

		System.out.print(currentArrow);
	}
	
	private static boolean isValidCommand(String input) {
		int index = input.indexOf(" ");
		if(index == -1) {
			return false;
		}
		return input.startsWith("!") && Input.actions.contains(input.substring(1,index));
	}
	
	public static void main(String[] argv) throws Exception {
		String input = "";
		init();

		while (true) {
			input = scanner.nextLine().trim();
//			Input betterInput = new Input(currentArrow + input);
			
			if (input.equals("exit"))
				break; 
			
			if(input.startsWith("@") || input.startsWith("#")) {
				currentArrow = input + Input.arrow; 
				System.out.print(currentArrow);
				continue;
			} 
			
			if(isValidCommand(input)) {
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
				if(input.startsWith("!upload")) {
					strategy = new SendFile(currentQueue, currentExchange);
				}
			
			} else {
				// Qualquer outra mensagem válida
				String chaveamento = currentArrow.substring(1).replace(Input.arrow, "");
				if(currentArrow.startsWith("@")) {
					currentQueue = chaveamento;
					currentExchange = ""; 
				}
				if(currentArrow.startsWith("#")) {
					// aqui não está verificando se o usuario pertence ao grupo!!
					// ou seja, qualquer um pode enviar mensagem mesmo sem pertencer ao grp (TODO)
					currentQueue = "";
					currentExchange = chaveamento;
				}  
				strategy = new SendMessage(currentQueue, currentExchange);
			}
			if(strategy != null && !input.equals("")) {
				String fullLine = currentArrow + input;
				chat.execute(strategy, fullLine);	
			}
			System.out.print(currentArrow);
		}

		Chat.getChannel().get("mensagens").close();
		Chat.getConnection().close();
		scanner.close();
		System.exit(0);
	}
}
