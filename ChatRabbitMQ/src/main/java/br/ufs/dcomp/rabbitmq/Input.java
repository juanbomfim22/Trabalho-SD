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
	private String withoutPrompt = ""; // !addUser teste grupo1
	private String name = ""; // marciocosta
	private String prompt = ""; // @marciocosta>>
	private String action = ""; // !addUser
	private List<String> args = new ArrayList<>(); // [teste, grupo1]

	public String getWithoutPrompt() { 
		return withoutPrompt;
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

	public Input(String fullLine) {
//		if (specialChars.contains(fullLine.charAt(0) + "")) {// começa com @,#,!

			List<String> parts = Arrays.asList(fullLine.split(" "));

			this.fullLine = fullLine;
			this.action = parts.get(1);
			this.prompt = parts.get(0);

			// Remove todos os specialChars e setas do prompt
			this.name = specialChars.stream().reduce(parts.get(0), (acc, curr) -> acc.replace(curr, ""))
					.replace(arrow.strip(), "");
			this.args = parts.stream().skip(2).collect(Collectors.toList());
//		} else {
//			this.prompt = arrow;
//			this.withoutPrompt = fullLine.replace(arrow, "");
//			this.fullLine = fullLine;
//		}
	}

	public Input(String... values) {
		this.args = Arrays.asList(values);
	}
}
