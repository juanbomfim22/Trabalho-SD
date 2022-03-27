package br.ufs.dcomp.ChatRabbitMQ;

import static com.github.stefanbirkner.systemlambda.SystemLambda.tapSystemOut;
import static com.github.stefanbirkner.systemlambda.SystemLambda.withTextFromSystemIn;
import static org.junit.Assert.assertEquals;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

class TestChat {
	private static Chat chat;
	/*
	 *
	 * 
	 * 
	 * 
	 */

	@BeforeAll
	static void setUpOnce() throws Exception {
		String username = "tarcisiorocha";
		chat = new Chat(username, "54.86.170.54", "juanbomfim22", "juanbomfim22");
	}

	@AfterAll
	static void tearDownOnce() throws Exception {
		chat.getSendQueue().getChannel().close();
		chat.getSendQueue().getConnection().close();
	}
	
	@Test
	void declaresQueue() throws Exception {
		Queue queue = new Queue("tarcisiorocha", chat.getConnection());
		assertEquals("tarcisiorocha", queue.getQueueName());
	}
	
	@Test 
	void sendsMessage() throws Exception {
		Queue queue = new Queue("joaosantos", chat.getConnection());
		String message = "Olá, João!!!";
		queue.sendMessage(message, "(01/01/1970 às 00:00)", "joaosantos");
		assertEquals(1, queue.getMessageCount());
	}
	
	@Test
	void waitsMessage() throws Exception {
		Queue queue1 = new Queue("joaosantos", chat.getConnection());
		Queue queue2 = new Queue("marciocosta", chat.getConnection());
		
		queue1.sendMessage("Vamos sair hoje?", "(01/01/1970 às 00:00)", "marciocosta");
		String text = tapSystemOut(() -> {
			queue2.waitMessage();
		});
		assertEquals("(01/01/1970 às 00:00) marciocosta diz: Vamos sair hoje?" , text);
	}
	
//	@Test
//	void Scanner_reads_text_from_System_in() throws Exception {
//		withTextFromSystemIn("first line", "second line").execute(() -> {
//			Scanner scanner = new Scanner(System.in);
//			scanner.nextLine();
//			assertEquals("second line", scanner.nextLine());
//		});
//	}
//
//	@Test
//	void givenTapSystemOut_whenInvokePrintln_thenOutputIsReturnedSuccessfully() throws Exception {
//
//		String text = tapSystemOut(() -> {
//			System.out.print("some text");
//		});
//		assertEquals("some text", text);
//	}
//
//	@Test
//	@DisplayName("1 + 1 = 2")
//	void addsTwoNumbers() throws Exception {
//		Chat rabbit = new Chat();
//		rabbit.getUser();
//		String text = tapSystemOut(() -> {
//			System.out.println("some text");
//		});
//		assertEquals("some text", text);
//		// Calculator calculator = new Calculator();
////		assertEquals(2, calculator.add(1, 1), "1 + 1 should equal 2");
//	}
//
//	@ParameterizedTest(name = "{0} + {1} = {2}")
//	@CsvSource({ "0,    1,   1", "1,    2,   3", "49,  51, 101", "1,  100, 101" })
//	void add(int first, int second, int expectedResult) {
////		Calculator calculator = new Calculator();
////		assertEquals(expectedResult, calculator.add(first, second),
////				() -> first + " + " + second + " should equal " + expectedResult);
//	}
}
