package br.ufs.dcomp.rabbitmq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Input {
	/*
	 * EX: @marciocosta>> !addUser teste
	 */
	private String fullLine = ""; // @marciocosta>> !addUser teste grupo1
	private String input = ""; // !addUser teste grupo1
	private String name = ""; // marciocosta
	private String prompt = ""; // @marciocosta>>
	private String promptSymbol = ""; // "@"
	private String action = ""; // !addUser
	private String actionSymbol = ""; // !
	private List<String> args = new ArrayList<>(); // [teste, grupo1]
	

	public String getInput() {
		return input;
	}

	public String getFullLine() {
		return fullLine;
	}

	public String getPrompt() {
		return prompt;
	}

	public String getName() {
		return name;
	}

	public String getAction() {
		return action;
	}

	public String getArgs(int i) {
		return args.get(i);
	}

	public String getPromptSymbol() {
		return promptSymbol;
	}
	
	public String getActionSymbol() {
		return actionSymbol;
	}
		
	public boolean startsWithSymbol() {
		return input.startsWith(Symbols.COMMAND) 
				|| input.startsWith(Symbols.USER) 
				|| input.startsWith(Symbols.GROUP);
	}
	
	public boolean isEmpty() {
		return input.isEmpty();
	}
	
	public Input() {}
	
	public Input(String userArrow, String input) {
		this.prompt = userArrow;
		this.input = input;
		this.fullLine = userArrow + input;
		 
		this.name = Arrays.asList(Symbols.COMMAND, Symbols.USER, Symbols.GROUP)
					.stream()
					.reduce(userArrow, (acc, curr) -> acc.replace(curr, ""))
					.replace(Symbols.ARROW, "");

		if(userArrow.startsWith(Symbols.USER)) {
			this.promptSymbol = Symbols.USER;
		}
		if(userArrow.startsWith(Symbols.GROUP)) {
			this.promptSymbol = Symbols.GROUP;
		}
		
		if(input.startsWith(Symbols.USER)){
			this.actionSymbol = input.substring(0,Symbols.USER.length());			
		}
		if(input.startsWith(Symbols.GROUP)){
			this.actionSymbol = input.substring(0,Symbols.GROUP.length());			
		}	
		if(input.startsWith(Symbols.COMMAND)) { 
			this.actionSymbol = input.substring(0,Symbols.COMMAND.length());	
			List<String> parts = Arrays.asList(input.split("\\s+"));
			this.action = parts.get(0).substring(Symbols.COMMAND.length());
			this.args = parts.stream().skip(1).collect(Collectors.toList());
		}
	}
}
