package com.example.questionnaire.vo;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonProperty;

public class QuizSearchRequest {

	private String title ;
	
	@JsonProperty ("start_date")    // �Ѽ� key �ȷQ�q  startDate �� start_date�A�[�W @JsonProperty
	private LocalDate startDate ;
	
	@JsonProperty ("end_date")
	private LocalDate endDate ;

	public QuizSearchRequest() {
		super();
	}

	public QuizSearchRequest(String title, LocalDate startDate, LocalDate endDate) {
		super();
		this.title = title;
		this.startDate = startDate;
		this.endDate = endDate;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public LocalDate getStartDate() {
		return startDate;
	}

	public void setStartDate(LocalDate startDate) {
		this.startDate = startDate;
	}

	public LocalDate getEndDate() {
		return endDate;
	}

	public void setEndDate(LocalDate endDate) {
		this.endDate = endDate;
	}
	
}
