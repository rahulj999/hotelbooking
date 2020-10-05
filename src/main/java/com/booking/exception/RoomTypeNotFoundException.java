package com.booking.exception;

public class RoomTypeNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	public RoomTypeNotFoundException(String errorMessage) {
		super(errorMessage);
	}
}
