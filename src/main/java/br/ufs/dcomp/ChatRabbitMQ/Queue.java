package br.ufs.dcomp.ChatRabbitMQ;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.Queue.DeclareOk;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class Queue{ 
	private Channel channel;
	private Connection connection;
	private DeclareOk queue;
	private String queueName;
	
	public String getQueueName() {
		return queue.getQueue();
	}

	public int getMessageCount() {
		return queue.getMessageCount();
	}

	
	
	public Queue(String queueName, Connection connection) throws Exception {
		this.connection = connection;
		this.channel = connection.createChannel();
		this.queue = channel.queueDeclare(queueName, false, false, false, null);
		this.queueName = queueName;
	}

	public Channel getChannel() {
		return channel;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public void setConnection(Connection connection) {
		this.connection = connection;
	}

	public Connection getConnection() {
		return connection;
	}
		
	public DeclareOk getQueue() {
		return queue;
	}

	public void setQueue(DeclareOk queue) {
		this.queue = queue;
	}


	public void sendMessage(String message, String date, String destination) throws IOException {
		String fullMessage = date + " " + getQueueName() + " diz: " + message;
		channel.basicPublish("", destination, null, fullMessage.getBytes("UTF-8")); 
	}

	public void waitMessage() throws Exception {
		Consumer consumer = new DefaultConsumer(channel) {
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
					byte[] body) throws IOException {
				String message = new String(body, "UTF-8");
				System.out.print("\033[2K"); // Erase line content
				System.out.println("\r" + message);
			}
		};
		channel.basicConsume(queueName, true, consumer);
	}
	
	public DeclareOk createQueue(String queueName) throws Exception {
		Channel channel = connection.createChannel(); 
		return channel.queueDeclare(queueName, false, false, false, null);
	}

}
