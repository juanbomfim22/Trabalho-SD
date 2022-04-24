package br.ufs.dcomp.rabbitmq.strategies;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.rabbitmq.client.Channel;

import br.ufs.dcomp.rabbitmq.FormattedDate;
import br.ufs.dcomp.rabbitmq.Input;
import br.ufs.dcomp.rabbitmq.proto.MensagemProto;
import br.ufs.dcomp.rabbitmq.proto.PROTO;

public class SendMessage implements ActionStrategy {
	private String currentQueue;
	private String currentExchange;

	public SendMessage(String currentQueue, String currentExchange) {
		this.currentQueue = currentQueue;
		this.currentExchange = currentExchange;
	}
	 
	@Override
	public void run(Channel channel, Input input, String username) throws Exception {
			FormattedDate date = new FormattedDate();
			MensagemProto.Conteudo conteudo = PROTO.createConteudoProto("text/plain", input.getFullLine().getBytes("UTF-8"), ""); // mensagens
			byte[] mensagemProto = PROTO.createMensagemProto(username, 
					date.getDay(),
					date.getHour(), currentExchange, conteudo);
			channel.basicPublish(currentExchange, currentQueue, null, mensagemProto);	
	}


}
