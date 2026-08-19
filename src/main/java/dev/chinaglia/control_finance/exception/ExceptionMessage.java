package dev.chinaglia.control_finance.exception;

import java.util.ArrayList;
import java.util.List;

public class ExceptionMessage {
	
	public List<ErrorMessageResponse> errorMessageResponses;
	
	public ExceptionMessage() {
		this.errorMessageResponses = new ArrayList<>();
	}

	public List<ErrorMessageResponse> getErrorMessageResponses ()
	{
		return errorMessageResponses;
	}
	
	public void addErrorMessageResponse(ErrorMessageResponse errorMessageResponse)
	{
		if(errorMessageResponse != null) 
		{
			errorMessageResponses.add(errorMessageResponse);
		}
	}
	
}
