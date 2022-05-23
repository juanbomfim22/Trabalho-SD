package br.ufs.dcomp.rabbitmq.strategies.group;

import com.rabbitmq.client.Channel;

import br.ufs.dcomp.rabbitmq.chat.Input;
import br.ufs.dcomp.rabbitmq.strategies.ActionStrategy;
import br.ufs.dcomp.rabbitmq.strategies.user.AddUserToGroup;

public class AddGroup implements ActionStrategy {
	
	@Override
	public void run(Channel channel, Input input) throws Exception{
			String exchange = input.getArgs(0);	
			String source = input.getSource();
			channel.exchangeDeclare(exchange, "topic");
		    System.out.println("Declarando grupo: " + exchange);
		    
		    String runCommand = "!addUser " + source + " " + exchange;
			new AddUserToGroup().run(channel, new Input("",runCommand, source));			
	}
}
