package com.scalar.identityProvider.payload.response;

import lombok.Getter;
import lombok.Setter;

/*
 * Response payload for messages
 */
public class MessageResponse {

	/*
	 * Status of the response
	 */
	@Getter
  	@Setter
	private String status;

	/*
	 * Message content
	 */
	@Getter
  	@Setter
	private String message;


	/**
	 * Constructor to initialize the message
	 * 
	 * @param status  Status of the response
	 * @param message Message content
	 */
	public MessageResponse(String status, String message) {
		this.status = status;
	    this.message = message;
	  }
}
