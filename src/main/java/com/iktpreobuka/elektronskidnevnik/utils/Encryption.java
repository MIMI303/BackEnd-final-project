package com.iktpreobuka.elektronskidnevnik.utils;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class Encryption {
	
	public static String getPassEncoded(String pass) {
		BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
		return bCryptPasswordEncoder.encode(pass);
	}

	public static void main(String[] args) {
		System.out.println(getPassEncoded("ljubinko"));
		
	}
}
