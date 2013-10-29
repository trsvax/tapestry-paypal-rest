package com.trsvax.paypal.services;

import java.net.URL;

import org.slf4j.Logger;

import com.paypal.api.payments.Link;
import com.paypal.api.payments.Payment;
import com.trsvax.paypal.PayPalPayment;
import com.trsvax.shop.Invoice;
import com.trsvax.shop.PaymentSource;

public class PayPalPaymentSource implements PaymentSource<PayPalPayment> {
	private final PayPalRestService payPalService;
	private final Logger logger;

	public PayPalPaymentSource(Logger logger, PayPalRestService payPalService) {
		this.logger = logger;
		this.payPalService = payPalService;
	}

	public Object pay(PayPalPayment payPalPayment, Invoice invoice) {
		logger.info("pay PayPal {}", payPalPayment.getState() );
		invoice.setPaymentMethod(payPalPayment);
		if ( "done".equals(payPalPayment.getState()) ) {
			return "thanks";
		}
		if ( "authorize".equals(payPalPayment.getState()) ) {
			try {
				Payment payment = payPalService.payPayPal(payPalPayment, invoice);
				logger.info("state: {}",payment.getState());
				for ( Link link : payment.getLinks() ) {
					if ( "approval_url".equals(link.getRel())) {
						return new URL(link.getHref());
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return "cart/PayPalCheckout";
	}

}
