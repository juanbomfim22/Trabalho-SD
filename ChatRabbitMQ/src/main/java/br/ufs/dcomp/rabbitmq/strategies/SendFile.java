package br.ufs.dcomp.rabbitmq.strategies;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.rabbitmq.client.Channel;

import br.ufs.dcomp.rabbitmq.FormattedDate;
import br.ufs.dcomp.rabbitmq.Input;
import br.ufs.dcomp.rabbitmq.proto.MensagemProto;
import br.ufs.dcomp.rabbitmq.proto.PROTO;

public class SendFile extends Thread implements ActionStrategy {
	private String currentQueue;
	private String currentExchange;
	
	private Channel channel;
	private Input input;
	private String username; 

	public SendFile(String currentQueue, String currentExchange) {
		this.currentQueue = currentQueue;
		this.currentExchange = currentExchange;
	}

	public void run() {
		try {
			FormattedDate date = new FormattedDate();
			String path = input.getArgs().get(0);
			String mime = Files.probeContentType(Paths.get(path));
			byte[] array = Files.readAllBytes(Paths.get(path));
	
			MensagemProto.Conteudo conteudo = PROTO.createConteudoProto(mime, array, "");
			byte[] mensagemProto = PROTO.createMensagemProto(username, 
					date.getDay(), date.getHour(), currentExchange, conteudo);
			channel.basicPublish(currentExchange, currentQueue, null, mensagemProto);
			System.out.println("\nArquivo " + " foi enviado para @" +  input.getName() + "!");
			System.out.print(input.getPrompt());
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
 	}
 

	@Override
	public void run(Channel channel, Input input, String username) throws Exception {
		String recepient = input.getName();
		if(recepient.equals("")) {
			// Significa que a seta está >>, envie para o próprio user
			recepient = username;
		}
		System.out.println("Enviando \"" + input.getArgs().get(0) + "\" para " + recepient);
		this.channel = channel;
		this.input = input;
		this.username = username;
		this.start();		
	}
}
