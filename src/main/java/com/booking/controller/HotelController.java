package com.booking.controller;

import java.util.List;

import javax.websocket.server.PathParam;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.booking.dao.HotelDAO;
import com.booking.model.Hotel;

@RestController
public class HotelController {
	
	@Autowired
	private HotelDAO hotelDAO;
	
	@PostMapping("/hotel")
	public void saveHotel(@RequestBody Hotel hotel) {
		hotelDAO.save(hotel);
	}
	
	@GetMapping("/hotels")
	public List<Hotel> getHotels() {
		return hotelDAO.findAll();
	}
	
	@GetMapping("/hotel/{id}")
	public Hotel getHotelById(@PathParam(value = "id") int id) {
		return hotelDAO.findById(id).get();
	}
}
