package com.trsvax.paypal.services;

import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.services.LibraryMapping;

import com.trsvax.paypal.CreditCardPayment;
import com.trsvax.paypal.PayPalPayment;
import com.trsvax.shop.PaymentSource;

public class PayPalModule {
	
   public static void bind(ServiceBinder binder) {
	   binder.bind(PayPalRestService.class,PayPalRestServiceImpl.class);
   }

	public static void contributePaymentSource(MappedConfiguration<Class, PaymentSource> configuration) {
		configuration.addInstance(PayPalPayment.class,PayPalPaymentSource.class);
		configuration.addInstance(CreditCardPayment.class,CreditCardPaymentSource.class);
	}
	
	
	public static void contributeComponentClassResolver(Configuration<LibraryMapping> configuration) {
		configuration.add(new LibraryMapping("paypal", "com.trsvax.paypal"));
	}
}
