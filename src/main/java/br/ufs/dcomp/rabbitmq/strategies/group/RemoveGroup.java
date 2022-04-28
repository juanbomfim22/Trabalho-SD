package br.ufs.dcomp.rabbitmq.strategies.group;

import com.rabbitmq.client.Channel;

import br.ufs.dcomp.rabbitmq.chat.Input;
import br.ufs.dcomp.rabbitmq.strategies.ActionStrategy;

public class RemoveGroup implements ActionStrategy {
	
	@Override
	public void run(Channel channel, Input input, String username) throws Exception{
		channel.exchangeDelete(input.getArgs(0));
	}

}
