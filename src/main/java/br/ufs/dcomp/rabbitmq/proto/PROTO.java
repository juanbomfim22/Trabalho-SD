package br.ufs.dcomp.rabbitmq.proto;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.rabbitmq.client.DeliverCallback;

import br.ufs.dcomp.rabbitmq.Demo;
import br.ufs.dcomp.rabbitmq.Symbols;
import br.ufs.dcomp.rabbitmq.date.FormattedDate;
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
	
	public static byte[] fileBytes(String filename, String username, String currentExchange) throws IOException {
		FormattedDate date = new FormattedDate();
		Path path = Paths.get(filename);
		String mime = Files.probeContentType(path);
		String fileName = path.getFileName().toString();
		byte[] array = Files.readAllBytes(path);
		MensagemProto.Conteudo conteudo = PROTO.createConteudoProto(mime, array, fileName);
		byte[] mensagemProto = PROTO.createMensagemProto(username, date.getDay(), date.getHour(),
				currentExchange, conteudo);
		return mensagemProto;
	}
	
	public static DeliverCallback constructCallback(String channelName) {
		DeliverCallback deliverCallback = (consumerTag, delivery) -> {
			MensagemProto.Mensagem contatoMensagem = MensagemProto.Mensagem.parseFrom(delivery.getBody());
			MensagemProto.Conteudo conteudo = contatoMensagem.getConteudo();
			String emissor = contatoMensagem.getEmissor();
			String data = contatoMensagem.getData();
			String hora = contatoMensagem.getHora();
			String grupo = contatoMensagem.getGrupo();
			String nomeArq = conteudo.getNome();
			byte[] corpoMensagem = conteudo.getCorpo().toByteArray();

			System.out.print("\033[2K"); // Erase line content
						
			if(channelName.equals("mensagens")) {
				String strMensagem = new String(corpoMensagem, "UTF-8"); // FORMATAR DISPLAY DE MENSAGEM
				String grupoEmissor = grupo.equals("") ? emissor : emissor + Symbols.GROUP + grupo;
				System.out.println("\r" + "(" + data + " às " + hora + ") " + grupoEmissor + " diz: " + strMensagem);
			}
			if(channelName.equals("arquivos")) {
				PATH.write("/tmp/Downloads/"+emissor, nomeArq, corpoMensagem);
				System.out.println("\r" + "(" + data + " às " + hora + ") " + "Arquivo \"" + nomeArq + "\" recebido de @"
						+ emissor + "!");
			}
			System.out.print(Demo.currentArrow);
	    };
		return deliverCallback;
	}
}
