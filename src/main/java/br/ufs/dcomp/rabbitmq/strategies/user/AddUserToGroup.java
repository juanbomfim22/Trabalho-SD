package br.ufs.dcomp.rabbitmq.strategies.user;

import com.rabbitmq.client.Channel;

import br.ufs.dcomp.rabbitmq.Input;
import br.ufs.dcomp.rabbitmq.strategies.ActionStrategy;

public class AddUserToGroup implements ActionStrategy {

	@Override
	public void run(Channel channel, Input input, String username) throws Exception {
		String user = input.getArgs().get(0);
		String exchange = input.getArgs().get(1);
	    channel.queueBind(user, exchange, "");	
	    System.out.println("Adicionando: " + user + " em " + exchange);
	}
}
