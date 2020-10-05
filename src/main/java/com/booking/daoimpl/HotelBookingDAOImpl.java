package com.booking.daoimpl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.booking.model.HotelRoomBooking;

@Repository
public class HotelBookingDAOImpl {
	
	
	public List<HotelRoomBooking> getHotelRoomBookings(int hotelId) {
		List<HotelRoomBooking> hotelRoomBookings = new ArrayList<>();
		return hotelRoomBookings;
	}
	

}
