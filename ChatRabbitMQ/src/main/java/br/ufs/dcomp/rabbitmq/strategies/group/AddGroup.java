package br.ufs.dcomp.rabbitmq.strategies.group;

import com.rabbitmq.client.Channel;

import br.ufs.dcomp.rabbitmq.Input;
import br.ufs.dcomp.rabbitmq.strategies.ActionStrategy;
import br.ufs.dcomp.rabbitmq.strategies.user.AddUserToGroup;

public class AddGroup implements ActionStrategy {
	
	@Override
	public void run(Channel channel, Input input, String username) throws Exception{
		String exchange = input.getArgs().get(0);
	    channel.exchangeDeclare(exchange,"fanout");
	    channel.exchangeDeclare(exchange+"Files","fanout");
	    new AddUserToGroup().run(channel, new Input(">> ", "!addUser "+  username + " " + exchange), null);
	    new AddUserToGroup().run(channel, new Input(">> ", "!addUser "+  username+"Files" + " " + exchange+"Files"), null);
	}
 

}
