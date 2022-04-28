package br.ufs.dcomp.rabbitmq.date;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class FormattedDate {
	private ZonedDateTime zdt;
	
	public FormattedDate() {
		this.zdt = currentDate();
	}
	
	private ZonedDateTime currentDate() {
		ZoneId z = ZoneId.of("America/Sao_Paulo");
		ZonedDateTime zdt = ZonedDateTime.now(z);
		return zdt;
	}
	
	private String ofPattern(String pattern) {
		return DateTimeFormatter.ofPattern(pattern).format(this.zdt);
	}
	
	public  String getFormattedDate() {
		return ofPattern("(dd/MM/yyyy 'às' HH:mm)");  
	}
	
	public String getHour() {
		return ofPattern("HH:mm");
	}
	
	public String getDay() { 
		return ofPattern("dd/MM/yyyy");
	}
	
}
