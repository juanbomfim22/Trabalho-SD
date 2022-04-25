package br.ufs.dcomp.rabbitmq.util;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class PATH {
	public static void write(String caminho, String nomeArquivo, byte[] mensagem) {
		try {
			if(!caminho.endsWith("/")) caminho = caminho + "/";
			if(nomeArquivo.equals("")) nomeArquivo = "DEFAULT.txt";
			
			Path path = Paths.get(caminho + nomeArquivo);
			Path parent = path.getParent();
			
			if(!Files.exists(parent))
				Files.createDirectories(path.getParent());
			Files.write(path, mensagem);
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
//	public static
	
}
