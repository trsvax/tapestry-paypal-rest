package com.trsvax.paypal.components;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.internal.NullAnnotationProvider;
import org.apache.tapestry5.ioc.services.MasterObjectProvider;

import com.trsvax.paypal.CreditCardPayment;
import com.trsvax.shop.PaymentMethod;

public class CreditCardButton {
	
	@Parameter
	private PaymentMethod payment;
	
	@Inject
	private MasterObjectProvider provider;
	
	void onSelectedFromCreditCard() { 
		//payment = (PaymentMethod) objectSource.build(CreditCardPayment.class); 
		
		payment = provider.provide(CreditCardPayment.class, new NullAnnotationProvider(), null, true);
	}

}
