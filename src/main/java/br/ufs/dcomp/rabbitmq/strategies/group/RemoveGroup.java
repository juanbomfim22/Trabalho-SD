package br.ufs.dcomp.rabbitmq.strategies.group;

import com.rabbitmq.client.Channel;

import br.ufs.dcomp.rabbitmq.Input;
import br.ufs.dcomp.rabbitmq.strategies.ActionStrategy;
import br.ufs.dcomp.rabbitmq.strategies.user.AddUserToGroup;

public class RemoveGroup implements ActionStrategy {
	
	@Override
	public void run(Channel channel, Input input, String username) throws Exception{
		channel.exchangeDelete(input.getArgs().get(0));
	}

}