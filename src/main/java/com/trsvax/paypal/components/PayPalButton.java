package com.trsvax.paypal.components;

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.ioc.Resource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.internal.NullAnnotationProvider;
import org.apache.tapestry5.ioc.services.MasterObjectProvider;

import com.trsvax.paypal.PayPalPayment;
import com.trsvax.shop.PaymentMethod;

public class PayPalButton {
	
	@Parameter
	private PaymentMethod payment;
	
	@Inject
	private MasterObjectProvider provider;
	
	void onSelectedFromPayPal() { 
		//payment = objectFactory.build(PayPalPayment.class); 
		payment = provider.provide(PayPalPayment.class, new NullAnnotationProvider(), null, true);
	}

	public Asset getImage() {
		return new Asset() {
			
			public String toClientURL() {
				return "https://www.paypal.com/en_US/i/logo/PayPal_mark_37x23.gif";
			}
			
			public Resource getResource() {
				return null;
			}
		};
	}
}
