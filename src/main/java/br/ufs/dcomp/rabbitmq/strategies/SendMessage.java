package br.ufs.dcomp.rabbitmq.strategies;

import com.rabbitmq.client.Channel;

import br.ufs.dcomp.rabbitmq.chat.Input;
import br.ufs.dcomp.rabbitmq.chat.Symbols;
import br.ufs.dcomp.rabbitmq.date.FormattedDate;
import br.ufs.dcomp.rabbitmq.proto.MensagemProto;
import br.ufs.dcomp.rabbitmq.util.PROTO;

public class SendMessage implements ActionStrategy {
	private String currentQueue="";
	private String currentExchange="";

	public SendMessage(String currentQueue, String currentExchange) {
		this.currentQueue =  currentQueue;
		this.currentExchange = currentExchange;
	}
	 
	@Override
	public void run(Channel channel, Input input, String username) throws Exception {
			if(input.getPrompt().startsWith(Symbols.GROUP)) {
				currentQueue = currentQueue + ".mensagens";
			}
					
			FormattedDate date = new FormattedDate();
			MensagemProto.Conteudo conteudo = PROTO.createConteudoProto("text/plain", input.getInput().getBytes("UTF-8"), ""); // mensagens
			byte[] mensagemProto = PROTO.createMensagemProto(username, 
					date.getDay(),
					date.getHour(), currentExchange, conteudo);
			
 			channel.basicPublish(currentExchange, currentQueue, null, mensagemProto);	
	}
}
