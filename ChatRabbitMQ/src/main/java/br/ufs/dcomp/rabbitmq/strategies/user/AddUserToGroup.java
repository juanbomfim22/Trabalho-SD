package br.ufs.dcomp.rabbitmq.strategies.user;

import com.rabbitmq.client.Channel;

import br.ufs.dcomp.rabbitmq.Input;
import br.ufs.dcomp.rabbitmq.strategies.ActionStrategy;

public class AddUserToGroup implements ActionStrategy {

	@Override
	public void run(Channel channel, Input input, String username) throws Exception {
	    channel.queueBind(input.getArgs().get(0), input.getArgs().get(1), "");	
	}
}
