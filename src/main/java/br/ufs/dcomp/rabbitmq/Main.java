package br.ufs.dcomp.rabbitmq;

import java.util.Scanner;
import java.util.stream.Stream;

import br.ufs.dcomp.rabbitmq.chat.Chat;
import br.ufs.dcomp.rabbitmq.chat.Symbols;
import br.ufs.dcomp.rabbitmq.strategies.ActionStrategy;
import br.ufs.dcomp.rabbitmq.strategies.UploadFile;
import br.ufs.dcomp.rabbitmq.strategies.SendMessage;
import br.ufs.dcomp.rabbitmq.strategies.group.AddGroup;
import br.ufs.dcomp.rabbitmq.strategies.group.ListGroups;
import br.ufs.dcomp.rabbitmq.strategies.group.RemoveGroup;
import br.ufs.dcomp.rabbitmq.strategies.group.ListUsersFromGroup;
import br.ufs.dcomp.rabbitmq.strategies.user.AddUserToGroup;
import br.ufs.dcomp.rabbitmq.strategies.user.DelFromGroup;

public class Main {
	
	private static final Scanner scanner = new Scanner(System.in);

	public static String currentArrow = Symbols.ARROW;
	private static String currentQueue = "";
	private static String currentExchange = "";
	private static String input = "";

	private static Chat chat; // eh o Context
	private static ActionStrategy strategy; // eh a interface Strategy

	private static void init() throws Exception {
		System.out.print("User: ");
		String user = scanner.nextLine().trim();
//		currentQueue = sender; // Descomentar para que o usuário receba mensagens dele mesmo
		chat = new Chat(user);
		chat.channelSetup(user);
		chat.waitMessage();
		System.out.print(currentArrow);
	}

	private static void close() throws Exception {
		for (var pair : Chat.getChannels().entrySet()) {
			pair.getValue().close();
		}
		Chat.getConnection().close();
		scanner.close(); 
	}

	public static void main(String[] argv) throws Exception {
		init(); 
		while (!input.equals("exit") && !input.equals("restart")) {
			input = scanner.nextLine().trim(); 
			
			// Checa se é igual a algum simbolo especial
			if (!input.isEmpty() && !Stream.of(Symbols.COMMAND, Symbols.GROUP, Symbols.USER).anyMatch(input::startsWith)) {
				strategy = new SendMessage(currentQueue, currentExchange);
			}
			if (input.startsWith(Symbols.USER)) {
				currentQueue = input.substring(Symbols.USER.length());
				currentExchange = "";
				currentArrow = input + Symbols.ARROW;
				strategy = null;
			}
			if (input.startsWith(Symbols.GROUP)) {
				currentQueue = "";
				currentExchange = input.substring(Symbols.GROUP.length());
				currentArrow = input + Symbols.ARROW;
				strategy = null;
			}
			// TODO: Se digitar um comando inexistente, retornar mensagem e não deixar
			// executar
			// a strategy
			if (input.startsWith(Symbols.ADD_GRP)) {
				strategy = new AddGroup();
			}
			if (input.startsWith(Symbols.ADD_USR)) {
				strategy = new AddUserToGroup();
			}
			if (input.startsWith(Symbols.RMV_GRP)) {
				strategy = new RemoveGroup();
			}
			if (input.startsWith(Symbols.DEL_USR)) {
				strategy = new DelFromGroup();
			}
			if (input.startsWith(Symbols.UPL)) { 
				strategy = new UploadFile(currentQueue, currentExchange);
			}
			if (input.startsWith(Symbols.LIST_USERS)) {
				strategy = new ListUsersFromGroup();
			}
			if (input.startsWith(Symbols.LIST_GROUP)) {
				strategy = new ListGroups();
			}
			
			if (strategy != null && !input.isEmpty()) {
				chat.execute(strategy, currentArrow, input);
			}
			System.out.print(currentArrow);			
		}
		close(); 
	}
}
