package br.ufs.dcomp.rabbitmq.strategies;

import com.rabbitmq.client.Channel;

import br.ufs.dcomp.rabbitmq.chat.Input;

public interface ActionStrategy {
	void run(Channel channel, Input input) throws Exception; // retorna a nova seta do chat >>
}
