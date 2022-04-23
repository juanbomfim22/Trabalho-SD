## Dupla: 

- Ana Letícia Alves Silveira
- Juan Garbellotte Bomfim


### Questionamentos:

#### Sobre o chat
1. O que imprimir quando o prompt está >> e usuário envia uma mensagem?
   1. Envia uma mensagem para ele mesmo, ou
   2. Nada acontece a menos que digite um comando (@, !)
2. O que acontece quando o usuário envia uma mensagem para ele mesmo?
   1. A mensagem aparece na tela, ou
   2. Não aparece nada
3. O que fazer se o usuário "nome" chavear para ele mesmo?
   1. Mostrar o prompt >>, ou
   2. Mostrar o prompt @nome>>
5. O que fazer quando o mesmo usuário está logado em vários "Chats" ao mesmo tempo e recebe uma mensagem? 
   1. Vai a mensagem pra apenas um dos Chats (padrão do RabbitMQ), ou
   2. Todos os Chats recebem a mesma mensagem

#### Sobre os comandos:
O que fazer quando:
- Esquece os parametros necessarios para executar o comando
- Adiciona/Remove usuário de um grupo inexistente
- Adiciona/Remove usuário inexistente de um grupo
- Cria um grupo com nome vazio
- Cria um grupo já existente
- Remove um grupo inexistente
- Chaveia para um grupo sem estar criado e envia mensagem
- Envia mensagem a um usuario inexistente (sem fila)
- Usuário está no grupo e envia uma mensagem a todos: recebe a própria ou não?
- Usuário chaveia para o grupo depois de uma mensagem ter sido enviada
- Usuário está no grupo e o deleta, o que fazer com a seta?
