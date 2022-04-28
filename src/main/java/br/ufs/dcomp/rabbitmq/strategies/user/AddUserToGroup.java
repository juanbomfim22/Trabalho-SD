package br.ufs.dcomp.rabbitmq.strategies.user;

import com.rabbitmq.client.Channel;

import br.ufs.dcomp.rabbitmq.Input;
import br.ufs.dcomp.rabbitmq.strategies.ActionStrategy;

public class AddUserToGroup implements ActionStrategy {

	@Override
	public void run(Channel channel, Input input, String username) throws Exception {
		String userQueue = input.getArgs(0);
		String exchange = input.getArgs(1);
	    channel.queueBind(userQueue, exchange, "*.mensagens"); // deve ter pelo menos um ponto na string (RabbitMQ)
	    channel.queueBind(userQueue+".arquivos", exchange, "*.arquivos"); //  deve ter pelo menos um ponto na string (RabbitMQ)
	    System.out.println("Adicionando " + userQueue + " em: " + exchange);
	}
}
