package br.ufs.dcomp.rabbitmq.proto;

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
	
	 
	
}
