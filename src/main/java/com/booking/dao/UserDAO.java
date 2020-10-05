package com.booking.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.booking.model.User;

public interface UserDAO extends JpaRepository<User, Integer> {
	public User findByName(String name);
}
