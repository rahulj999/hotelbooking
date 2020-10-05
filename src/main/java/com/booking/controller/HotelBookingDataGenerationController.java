package com.booking.controller;

import java.sql.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.booking.dao.HotelDAO;
import com.booking.dao.HotelRoomTypeDAO;
import com.booking.dao.UserDAO;
import com.booking.model.Hotel;
import com.booking.model.HotelBookingRequest;
import com.booking.model.HotelRoom;
import com.booking.model.RoomType;
import com.booking.model.User;

@RestController
public class HotelBookingDataGenerationController {
	
//	@Autowired
//	private HotelBookingDAO hotelBookingDAO;
	
	@Autowired
	private HotelDAO hotelDAO;
	
	@Autowired
	private HotelRoomTypeDAO hotelRoomTypeDAO;
	
//	@Autowired
//	private HotelRoomDAO hotelRoomDAO;
	
	@Autowired
	private UserDAO userDAO;
	
	RestTemplate restTemplate = new RestTemplate();
	
	@PostMapping("/hoteldata")
	public void hotelDataGenerator() {
		String baseUrl = "http://localhost:8080";
		String url = baseUrl + "/hotel";
		Hotel hotel = new Hotel();
		hotel.setName("Taj");
		hotel.setLocation("Mumbai");
		restTemplate.postForEntity(url, hotel, String.class);
		
		hotel.setName("Hyatt");
		hotel.setLocation("Mumbai");
		restTemplate.postForEntity(url, hotel, String.class);
		
		url = baseUrl + "/user";
		User user = new User();
		user.setName("ABC");
		user.setBonusPoints(100);
		restTemplate.postForEntity(url, user, String.class);
		
		user.setName("PQR");
		user.setBonusPoints(200);
		restTemplate.postForEntity(url, user, String.class);
		
		url = baseUrl + "/hotelRoomType";
		RoomType roomType = new RoomType();
		roomType.setCost(new Double(12));
		roomType.setRoomType("DELUXE");
		restTemplate.postForEntity(url, roomType, String.class);
		
		roomType.setCost(new Double(15));
		roomType.setRoomType("BUSINESS");
		restTemplate.postForEntity(url, roomType, String.class);
		
		roomType.setCost(new Double(130));
		roomType.setRoomType("CORPORATE");
		restTemplate.postForEntity(url, roomType, String.class);
		
		url = baseUrl + "/hotelRoom";
		HotelRoom hotelRoom = new HotelRoom();
		hotelRoom.setNumber(1);
		
		roomType = hotelRoomTypeDAO.findByRoomType("DELUXE");
		hotelRoom.setRoomTypeId(roomType.getId());
		
		hotel = hotelDAO.findByName("BBQ");
		hotelRoom.setHotelId(hotel.getId());
		restTemplate.postForEntity(url, hotelRoom, String.class);
		
		hotelRoom.setNumber(2);
		roomType = hotelRoomTypeDAO.findByRoomType("BUSINESS");
		hotelRoom.setRoomTypeId(roomType.getId());
		
		hotelRoom.setHotelId(hotel.getId());
		restTemplate.postForEntity(url, hotelRoom, String.class);
		
		hotelRoom.setNumber(3);
		roomType = hotelRoomTypeDAO.findByRoomType("CORPORATE");
		hotelRoom.setRoomTypeId(roomType.getId());
		
		hotelRoom.setHotelId(hotel.getId());
		restTemplate.postForEntity(url, hotelRoom, String.class);
		
		url = baseUrl + "/hotel/booking";
		HotelBookingRequest hotelBookingRequest = new HotelBookingRequest();
		hotelBookingRequest.setHotelName("Taj");
		hotelBookingRequest.setRoomType("DELUXE");
		hotelBookingRequest.setBookingDate(new Date(System.currentTimeMillis()));
		user = userDAO.findByName("ABC");
		hotelBookingRequest.setUserId(user.getId());
		
		restTemplate.postForEntity(url, hotelBookingRequest, String.class);
		
		hotelBookingRequest.setRoomType("CORPORATE");		
		restTemplate.postForEntity(url, hotelBookingRequest, String.class);
	}
}
