package com.trsvax.paypal;

import com.trsvax.shop.Address;
import com.trsvax.shop.PaymentMethod;

public interface CreditCardPayment extends PaymentMethod {
	
	public String getLast4Digits();
	
	public String getState();
	public void setState(String state);
	
	public String getNumber();
	public void setNumber(String number);
	
	public String getType();
	public void setType(String type);
	
	public String getExpireMonth();
	public void setExpireMonth(String month);
	
	public String getExpireYear();
	public void setExpireYear(String year);
	
	public String getCvv2();
	public void setCvv2(String cvvs);
	
	public String getFirstName();
	public void setFirstName(String firstName);
	
	public String getLastName();
	public void setLastName(String lastName);
	
	public Address getBillingAddress();
}
