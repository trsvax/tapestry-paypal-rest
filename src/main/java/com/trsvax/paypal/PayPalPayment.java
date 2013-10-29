package com.trsvax.paypal;

import com.trsvax.shop.PaymentMethod;

public interface PayPalPayment extends PaymentMethod {
	public String getState();
	public void setState(String state);
	

	
	

}
