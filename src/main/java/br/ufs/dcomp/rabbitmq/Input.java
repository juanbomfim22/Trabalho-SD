package br.ufs.dcomp.rabbitmq;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class Input {
	public static final String arrow = ">> ";
	public static final List<String> specialChars = Arrays.asList("@", "#", "!");
	public static final List<String> actions = Arrays.asList("addGroup", "addUser", "delFromGroup", "removeGroup",
			"upload");

	/*
	 * EX: @marciocosta>> !addUser teste
	 */
	private String fullLine = ""; // @marciocosta>> !addUser teste grupo1
	private String input = ""; // !addUser teste grupo1
	private String name = ""; // marciocosta
	private String prompt = ""; // @marciocosta>>
	private String action = ""; // !addUser
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

	public List<String> getArgs() {
		return args;
	}

	public boolean isValidCommand() {
		return actions.contains(action);
	}
	
	public Input(String input) {
		this("", input);
	}
	
	public Input(String arrow, String input) {
		this.input = input;
		if(input.startsWith("!")) { 
			List<String> parts = Arrays.asList(input.split("\\s+"));
			this.prompt = arrow;
			this.action = parts.get(0).substring(1);
			this.fullLine = arrow + input;
	
			// Remove todos os specialChars e setas do prompt
			this.name = specialChars.stream().reduce(arrow, (acc, curr) -> acc.replace(curr, "")).replaceAll(">", "")
					.strip();
			this.args = parts.stream().skip(1).collect(Collectors.toList());
		}
		else {
			if(input.startsWith("@") || input.startsWith("#")) {
				this.prompt = input;
			} else {
				this.prompt = arrow;
			}
			this.name = arrow.substring(1);
			this.fullLine = arrow + input;
		}
	}

//	public Input(String... values) {
//		this.args = Arrays.asList(values);
//	}
}
