package br.ufs.dcomp.rabbitmq.strategies.group;

import com.rabbitmq.client.Channel;

import br.ufs.dcomp.rabbitmq.chat.Input;
import br.ufs.dcomp.rabbitmq.strategies.ActionStrategy;
import br.ufs.dcomp.rabbitmq.strategies.user.AddUserToGroup;

public class AddGroup implements ActionStrategy {
	
	@Override
	public void run(Channel channel, Input input, String username) throws Exception{
			String exchange = input.getArgs(0);	
			channel.exchangeDeclare(exchange, "topic");
		    System.out.println("Declarando grupo: " + exchange);
			new AddUserToGroup().run(channel, new Input("","!addUser "+  username + " " + exchange), null);			
	}
}
