package br.ufs.dcomp.rabbitmq.strategies.group;

import java.util.Map;

import com.rabbitmq.client.Channel;

import br.ufs.dcomp.rabbitmq.chat.Input;
import br.ufs.dcomp.rabbitmq.strategies.ActionStrategy;
import br.ufs.dcomp.rabbitmq.strategies.user.AddUserToGroup;

public class AddGroup implements ActionStrategy {
	
	@Override
	public void run(Map<String, Channel> channels, Input input) throws Exception{
		try {
			String exchange = input.getArgs(0);	
			String source = input.getSource();
			
			channels.get("mensagens").exchangeDeclare(exchange, "topic");
			channels.get("arquivos").exchangeDeclare(exchange, "topic");
			
		    System.out.println("Declarando grupo: " + exchange);
		    
		    String runCommand = "!addUser " + source + " " + exchange;
			new AddUserToGroup().run(channels, new Input("",runCommand, source));
		} catch(Exception e) {
			System.out.println("[Erro] Falta parâmetro: grupo");
		}
	}
}
