package br.ufs.dcomp.ChatRabbitMQ;

import java.io.IOException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

public class Queue{ 
	private Channel channel;
	private String queueName;
	
	public String getQueueName() {
		return queueName;
	}
	
	private String currentTimestamp() {
		ZoneId z = ZoneId.of("America/Sao_Paulo");
		ZonedDateTime zdt = ZonedDateTime.now(z);
		return DateTimeFormatter.ofPattern("(dd/MM/yyyy) 'às' HH:mm").format(zdt); 
	}

	public Queue(String queueName, Connection connection, Channel channel) throws Exception {
		this.channel = channel;
		this.queueName = channel.queueDeclare(queueName, false, false, false, null).getQueue();
	}

	public void sendMessage(String message, String destination) throws IOException {
		if(!message.equals("")) {
			String timestamp = currentTimestamp();
			String fullMessage = timestamp + " " + queueName + " diz: " + message;
			channel.basicPublish("", destination, null, fullMessage.getBytes("UTF-8")); 			
		}
	}

	public void waitMessage(String currentArrow) throws Exception {
		Consumer consumer = new DefaultConsumer(channel) {
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties,
					byte[] body) throws IOException {
				String message = new String(body, "UTF-8");
				System.out.print("\033[2K"); // Erase line content
				System.out.println("\r" + message);
				System.out.print(currentArrow);
			}
		};
		channel.basicConsume(queueName, true, consumer);
	}
}
