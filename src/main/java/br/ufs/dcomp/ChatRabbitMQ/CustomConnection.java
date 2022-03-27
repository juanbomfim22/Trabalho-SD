package br.ufs.dcomp.ChatRabbitMQ;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class CustomConnection { 
	private Connection connection;
	private Channel channel;

	public Connection getConnection() {
		return connection;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	private Connection connectionSetup(String host, String name, String password) throws Exception{
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost(host); // IP da máquina virtual
		factory.setUsername(name);
		factory.setPassword(password);
		factory.setVirtualHost("/");
		return factory.newConnection();
	}

	public CustomConnection(String host, String name, String password) throws Exception{
		this.connection = connectionSetup(host, name, password);
		this.channel = connection.createChannel();
	}

}
