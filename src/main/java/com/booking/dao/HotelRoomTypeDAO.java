package com.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.booking.model.RoomType;

public interface HotelRoomTypeDAO extends JpaRepository<RoomType, Integer> {
	public RoomType findByRoomType(String roomType);
}
