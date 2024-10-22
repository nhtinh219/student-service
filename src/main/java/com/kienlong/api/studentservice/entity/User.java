package com.kienlong.api.studentservice.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer Id;
	
	@Column(unique = true, nullable = false, length = 20)
	private String username;
	
	@Column(nullable = false, length = 80)
	private String password;
	
	@Column(nullable = false, length = 10)
	private String role;
}
