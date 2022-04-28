package br.ufs.dcomp.rabbitmq.strategies;

import com.rabbitmq.client.Channel;

import br.ufs.dcomp.rabbitmq.Input;
import br.ufs.dcomp.rabbitmq.Symbols;
import br.ufs.dcomp.rabbitmq.proto.PROTO;

public class UploadFile implements ActionStrategy {
	private String currentQueue="";
	private String currentExchange="";

	public UploadFile(String currentQueue, String currentExchange) {
		this.currentQueue = currentQueue;
		this.currentExchange = currentExchange;
	}
	
	@Override
	public void run(Channel channel, Input input, String username) throws Exception {			
		String recipient = !input.getName().equals("") ? input.getName() : username;
		String queue = recipient + ".arquivos";
		System.out.println("Enviando \"" + input.getArgs(0) + "\" para " + recipient);
		new Thread() {
			@Override
			public void run() {
				try {				
					String filename = input.getArgs(0);
					byte[] msgProto = PROTO.fileBytes(filename, username, currentExchange);

//					sleep(10*1000); // atraso proposital
					
					channel.basicPublish(currentExchange, queue, null, msgProto);
					System.out.println("\nArquivo \"" + filename + "\" foi enviado para "+ input.getPromptSymbol() + recipient + "!");
					System.out.print(input.getPrompt());

				} catch (Exception e) {
					System.err.print(e);
				}
			}
		}.start();
	}
}
