package com.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.booking.model.Hotel;

public interface HotelDAO extends JpaRepository<Hotel, Integer> {
	public Hotel findByName(String name);
}
