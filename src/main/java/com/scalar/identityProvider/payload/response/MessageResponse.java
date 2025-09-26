package com.scalar.identityProvider.payload.response;

import lombok.Getter;
import lombok.Setter;

/*
 * Response payload for messages
 */
public class MessageResponse {

	/*
	 * Message content
	 */
	@Getter
  	@Setter
	private String message;


	/*
	 * Constructor to initialize the message
	 */
	public MessageResponse(String message) {
	    this.message = message;
	  }
}
