package com.booking.controller;

import static com.booking.enums.BookingStatusEnum.BOOKED;
import static com.booking.enums.BookingStatusEnum.CANCELLED;
import static com.booking.enums.BookingStatusEnum.PENDING_APPROVAL;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.booking.dao.HotelBookingDAO;
import com.booking.dao.HotelDAO;
import com.booking.dao.HotelRoomDAO;
import com.booking.dao.HotelRoomTypeDAO;
import com.booking.dao.UserDAO;
import com.booking.exception.HotelNotFoundException;
import com.booking.exception.RoomTypeNotFoundException;
import com.booking.exception.UserNotFoundException;
import com.booking.model.Hotel;
import com.booking.model.HotelBookingRequest;
import com.booking.model.HotelRoom;
import com.booking.model.HotelRoomBooking;
import com.booking.model.RoomType;
import com.booking.model.User;

@RestController
public class HotelBookingController {
	
	@Autowired
	private HotelBookingDAO hotelBookingDAO;
	
	@Autowired
	private HotelDAO hotelDAO;
	
	@Autowired
	private HotelRoomTypeDAO hotelRoomTypeDAO;
	
	@Autowired
	private HotelRoomDAO hotelRoomDAO;
	
	@Autowired
	private UserDAO userDAO;
	
	@GetMapping("/hotel")
	public String base() {
		return "Welcome to hotel";
	}
	
	@GetMapping("/hotel/{hotelId}/bookings")
	public List<HotelRoomBooking> getHotelRoomBookingsByHotelId(@RequestParam int hotelId) {
		List<HotelRoomBooking> hotelRoomBookings = new ArrayList<>();
		hotelBookingDAO.findByHotelId(hotelId).forEach(hotelRoomBookings::add);
		return hotelRoomBookings;
	}
	
	@GetMapping("/hotel/bookings")
	public List<HotelRoomBooking> getHotelRoomBookings() {
		List<HotelRoomBooking> hotelRoomBookings = new ArrayList<>();
		hotelBookingDAO.findAll().forEach(hotelRoomBookings::add);
		return hotelRoomBookings;
	}
	
	@PostMapping("/hotel/booking")
	public void bookHotelRoom(@Valid @RequestBody HotelBookingRequest hotelBookingRequest) 
			throws HotelNotFoundException, RoomTypeNotFoundException, UserNotFoundException, Exception {
		// check hotel with name exists
		String hotelName = hotelBookingRequest.getHotelName();
		Hotel hotel = hotelDAO.findByName(hotelName);
		if(hotel == null) {
			throw new HotelNotFoundException("Hotel "+hotelName+" not found.");
		}
		// check hotel room type exists
		String hotelRoomType = hotelBookingRequest.getRoomType();
		RoomType roomType = hotelRoomTypeDAO.findByRoomType(hotelRoomType);
		if(roomType == null) {
			throw new RoomTypeNotFoundException("Room type " + hotelRoomType + " not found.");
		}
		// check user exists in the db
		int userId = hotelBookingRequest.getUserId();
		Optional<User> userObj = userDAO.findById(userId);
		if(!userObj.isPresent()) {
			throw new UserNotFoundException("User with id "+userId+" not found.");
		}
		User user = userObj.get();
		// check booking date is not older date than today
		Date bookingDate = hotelBookingRequest.getBookingDate();
		long millis = System.currentTimeMillis();
		Date currentDate = new Date(millis);
		if(bookingDate.toLocalDate().isBefore(currentDate.toLocalDate())) {
			throw new Exception("Date should be greater than now or current");
		}
		
		// check hotel room of the specified type is vacant
		HotelRoomBooking existingHotelRoomBooking = hotelBookingDAO.findByHotelIdAndBookingDateAndUserIdAndHotelRoomType(hotel.getId(), bookingDate, userId, hotelRoomType);
		if(existingHotelRoomBooking != null && BOOKED.name().equals(existingHotelRoomBooking.getBookingStatus())) {
			throw new Exception("Hotel room is already booked");
		}
		HotelRoomBooking hotelRoomBooking = new HotelRoomBooking();
		hotelRoomBooking.setBookingDate(bookingDate);
		String bookingStatus = (roomType.getCost().intValue() <= user.getBonusPoints()) ?
				BOOKED.name() : PENDING_APPROVAL.name();
		hotelRoomBooking.setBookingStatus(bookingStatus);
		hotelRoomBooking.setHotelId(hotel.getId());
		hotelRoomBooking.setHotelRoomType(hotelRoomType);
		hotelRoomBooking.setUserId(userId);
		
		HotelRoom hotelRoom = null;
		// check vacant hotel rooms
		List<HotelRoom> hotelRoomsByHotelId = hotelRoomDAO.findByHotelIdAndRoomTypeId(hotel.getId(), roomType.getId());
		List<HotelRoomBooking> hotelRoomBookings = hotelBookingDAO.findByHotelIdAndBookingDateAndHotelRoomType(hotel.getId(), bookingDate, hotelRoomType);
		List<HotelRoom> vacantHotelRooms = new ArrayList<>();
		vacantHotelRooms.addAll(hotelRoomsByHotelId);
		for(HotelRoomBooking booking:hotelRoomBookings) {
			if(BOOKED.name().equals(booking.getBookingStatus()) || PENDING_APPROVAL.name().equals(booking.getBookingStatus())) {
				vacantHotelRooms.removeAll(hotelRoomsByHotelId.parallelStream().filter((room) -> booking.getHotelRoomId() == room.getId()).collect(Collectors.toList()));
			}
		}
		
		// check if hotel rooms are empty and probable booking status is booked.
		if((vacantHotelRooms == null || vacantHotelRooms.isEmpty()) && BOOKED.name().equals(bookingStatus)) {
			// check pending approval hotel rooms and cancel the current booking and assign it to this booking.
			List<HotelRoomBooking> pendingApprovalHotelRoomBookings = hotelRoomBookings.parallelStream().filter((booking) -> PENDING_APPROVAL.name().equals(booking.getBookingStatus())).collect(Collectors.toList()); //hotelBookingDAO.findByHotelIdAndBookingDateAndBookingStatusAndHotelRoomType(hotel.getId(), bookingDate, PENDING_APPROVAL.name(), hotelRoomType);
			if(pendingApprovalHotelRoomBookings != null && pendingApprovalHotelRoomBookings.size() > 0) {
				HotelRoomBooking pendingApprovalHotelRoomBooking = pendingApprovalHotelRoomBookings.get(0);
				hotelRoom = hotelRoomsByHotelId.parallelStream().filter((room) -> pendingApprovalHotelRoomBooking.getHotelRoomId() == room.getId()).collect(Collectors.toList()).get(0);
				pendingApprovalHotelRoomBooking.setBookingStatus(CANCELLED.name());
				hotelBookingDAO.save(pendingApprovalHotelRoomBooking);
			} else {
				throw new Exception("Hotel rooms in " + hotelName +" of type " + roomType.getRoomType() +" are not available.");
			}
		} else {
			hotelRoom = vacantHotelRooms.get(0);
		}
		hotelRoomBooking.setHotelRoomId(hotelRoom.getId());
		hotelRoomBooking = hotelBookingDAO.save(hotelRoomBooking);
		
		// deduct the bonus points from user for this booking if status is BOOKED.
		if(BOOKED.name().equals(bookingStatus)) {
			int remainingUserBonusPoints = user.getBonusPoints() - roomType.getCost().intValue();
			user.setBonusPoints(remainingUserBonusPoints);
			userDAO.save(user);
		}
	}
}
