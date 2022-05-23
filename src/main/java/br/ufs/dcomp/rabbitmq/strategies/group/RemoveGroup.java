package br.ufs.dcomp.rabbitmq.strategies.group;

import java.util.Map;

import com.rabbitmq.client.Channel;

import br.ufs.dcomp.rabbitmq.chat.Input;
import br.ufs.dcomp.rabbitmq.strategies.ActionStrategy;

public class RemoveGroup implements ActionStrategy {
	
	@Override
	public void run(Map<String, Channel> channels, Input input) throws Exception{
		try {
			channels.get("mensagens").exchangeDelete(input.getArgs(0));
			channels.get("arquivos").exchangeDelete(input.getArgs(0));
		}
		catch(Exception e) {
			System.out.println("[Erro] Falta parâmetro: grupo");
		}
	}

}
