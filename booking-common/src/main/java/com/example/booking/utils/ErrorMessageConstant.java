package com.example.booking.utils;

import lombok.Getter;

@Getter
public class ErrorMessageConstant {

	public static final String CONCURRENT_ERR_003 = "You can't make multiple action at the same time!";
	public static final String SYSTEM_001 = "System Exception Occours!";
	public static final String NOTFOUND_001 = "Data Not Found!";

	
	public static final String BOOK_FOR_ERR_CLASS_001 = "You can't book class that already start or expired!";
	
	public static final String PACKAGE_ERROR_001 = "Package expired!";
	public static final String CLASS_CANCLE_ERROR ="You can't cancle class that start soon!";
	
	public static final String PASSWORD_ERROR_001 ="Wrong Password!";
	
	public static final String AUTH_ERROR_001 ="Authentication Failure";


 
}
