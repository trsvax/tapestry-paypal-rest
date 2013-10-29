package com.trsvax.paypal.services;

import com.paypal.api.payments.Payment;
import com.paypal.core.rest.OAuthTokenCredential;
import com.paypal.core.rest.PayPalRESTException;
import com.trsvax.paypal.CreditCardPayment;
import com.trsvax.paypal.PayPalPayment;
import com.trsvax.shop.Invoice;

public interface PayPalRestService {
	public OAuthTokenCredential getCredentials();
	public String getToken() throws PayPalRESTException;
	
	public Payment payCreditCard(CreditCardPayment payment, Invoice invoice) throws PayPalRESTException;
	public Payment payPayPal(PayPalPayment payment, Invoice invoice) throws PayPalRESTException;
	
	public Payment completePayPal(String payerID, Invoice invoice) throws PayPalRESTException;
	
}
