package br.ufs.dcomp.rabbitmq.proto;

import java.io.IOException;
import java.util.Map;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import br.ufs.dcomp.rabbitmq.Demo;
import br.ufs.dcomp.rabbitmq.util.PATH;

public final class PROTO{
 
	public static MensagemProto.Conteudo createConteudoProto(String tipo, byte[] corpo, String nome) {
		MensagemProto.Conteudo.Builder bConteudo = MensagemProto.Conteudo.newBuilder();
		bConteudo.setTipo(tipo);
		bConteudo.setCorpo(com.google.protobuf.ByteString.copyFrom(corpo));
		bConteudo.setNome(nome);
		MensagemProto.Conteudo contatoConteudo = bConteudo.build();
		return contatoConteudo;
	}

	public static byte[] createMensagemProto(String sender, String data, String hora, String grupo,
			MensagemProto.Conteudo conteudo) {
		MensagemProto.Mensagem.Builder builderMensagem = MensagemProto.Mensagem.newBuilder();
		builderMensagem.setEmissor(sender);
		builderMensagem.setData(data);
		builderMensagem.setHora(hora);
		builderMensagem.setGrupo(grupo);
		builderMensagem.setConteudo(conteudo);
		MensagemProto.Mensagem contatoMensagem = builderMensagem.build();
		byte[] buffer = contatoMensagem.toByteArray(); // retorna a mensagem em bytes a ser enviada
		return buffer;
	}
	
	public static Consumer handle(Map<String, Channel> channels, String channelName) {
		Consumer consumer =  new DefaultConsumer(channels.get(channelName)) { 
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
				byte[] body) throws IOException {
					MensagemProto.Mensagem contatoMensagem = MensagemProto.Mensagem.parseFrom(body);
					MensagemProto.Conteudo conteudo = contatoMensagem.getConteudo();
	 
					String emissor = contatoMensagem.getEmissor();
					String data = contatoMensagem.getData();
					String hora = contatoMensagem.getHora();
					String grupo = contatoMensagem.getGrupo();
					String nome = conteudo.getNome();
					byte[] corpoMensagem = conteudo.getCorpo().toByteArray();

					System.out.print("\033[2K"); // Erase line content
					if(channelName.equals("mensagens")) {
						String strMensagem = new String(corpoMensagem, "UTF-8"); // FORMATAR DISPLAY DE MENSAGEM
						String grupoEmissor = grupo.equals("") ? emissor : emissor + "#" + grupo;
						System.out.println("\r" + "(" + data + " às " + hora + ") " + grupoEmissor + " diz: " + strMensagem);
					}
					if(channelName.equals("arquivos")) {
						PATH.write("/tmp/", nome, corpoMensagem);
						System.out.println("\r" + "(" + data + " às " + hora + ") " + "Arquivo \"" + nome + "\" recebido de @"
								+ emissor + "!");
					}
					System.out.print(Demo.currentArrow);
				}
			};
		return consumer;
	}
}
