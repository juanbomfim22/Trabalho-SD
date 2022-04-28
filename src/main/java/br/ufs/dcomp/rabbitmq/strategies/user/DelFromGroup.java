package br.ufs.dcomp.rabbitmq.strategies.user;

import com.rabbitmq.client.Channel;

import br.ufs.dcomp.rabbitmq.Input;
import br.ufs.dcomp.rabbitmq.strategies.ActionStrategy;

public class DelFromGroup implements ActionStrategy {
	
	@Override
	public void run(Channel channel, Input input, String username) throws Exception{
	    channel.queueUnbind(input.getArgs(0), input.getArgs(1), "");
	}
 

}
