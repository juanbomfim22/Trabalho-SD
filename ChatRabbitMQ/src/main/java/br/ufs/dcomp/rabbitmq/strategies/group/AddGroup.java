package br.ufs.dcomp.rabbitmq.strategies.group;

import com.rabbitmq.client.Channel;

import br.ufs.dcomp.rabbitmq.Input;
import br.ufs.dcomp.rabbitmq.strategies.ActionStrategy;
import br.ufs.dcomp.rabbitmq.strategies.user.AddUserToGroup;

public class AddGroup implements ActionStrategy {
	
	@Override
	public void run(Channel channel, Input input, String username) throws Exception{
		String exchange = input.getArgs().get(0);
//		System.out.println("Declaring exchange: " + exchange);
	    channel.exchangeDeclare(exchange,"fanout");
	     
	    //>> !addUser username exchange
	    String line = ">> !addUser " + username + " " + exchange;
	    new AddUserToGroup().run(channel, new Input(line), null);
	}
 

}
