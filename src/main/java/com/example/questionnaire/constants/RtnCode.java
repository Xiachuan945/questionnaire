package com.example.questionnaire.constants;

public enum RtnCode {

	SUCCESSFUL (200, "Successful !") , // 
	QUESTION_PARAM_ERROR (400, "Question Param error !") , //	
	QUESTIONNAIRE_PARAM_ERROR (400, "Questionnaire Param error !") , //	
	QUESTIONNAIRE_ID_PARAM_ERROR (400, "Questionnaire id Param error !") , //	
	QUESTIONNAIRE_ID_NOT_FOUND (404, "Questionnaire id not found !") , //	
	UPDATE_ERROR (400, "Update error !") , //	
	;
	
	private int code ;
	
	private String message ;

	private RtnCode(int code, String message) {
		this.code = code;
		this.message = message;		
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}	
	
}
