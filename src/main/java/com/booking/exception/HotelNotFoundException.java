package com.booking.exception;

public class HotelNotFoundException extends Exception {

	private static final long serialVersionUID = 1L;

	public HotelNotFoundException(String errorMessage) {
		super(errorMessage);
	}
}
