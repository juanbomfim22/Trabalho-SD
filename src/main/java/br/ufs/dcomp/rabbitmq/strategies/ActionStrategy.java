package br.ufs.dcomp.rabbitmq.strategies;

import com.rabbitmq.client.Channel;

import br.ufs.dcomp.rabbitmq.Input;

public interface ActionStrategy {
	void run(Channel channel, Input input, String username) throws Exception; // retorna a nova seta do chat >>
}