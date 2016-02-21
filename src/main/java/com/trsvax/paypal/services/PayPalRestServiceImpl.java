package com.trsvax.paypal.services;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.ioc.annotations.Symbol;
import org.slf4j.Logger;

import com.paypal.api.payments.Address;
import com.paypal.api.payments.Amount;
import com.paypal.api.payments.AmountDetails;
import com.paypal.api.payments.CreditCard;
import com.paypal.api.payments.FundingInstrument;
import com.paypal.api.payments.Item;
import com.paypal.api.payments.ItemList;
import com.paypal.api.payments.Payer;
import com.paypal.api.payments.Payment;
import com.paypal.api.payments.PaymentExecution;
import com.paypal.api.payments.RedirectUrls;
import com.paypal.api.payments.SubTransaction;
import com.paypal.api.payments.Transaction;
import com.paypal.core.ConfigManager;
import com.paypal.core.rest.APIContext;
import com.paypal.core.rest.OAuthTokenCredential;
import com.paypal.core.rest.PayPalRESTException;
import com.paypal.core.rest.PayPalResource;
import com.trsvax.paypal.CreditCardPayment;
import com.trsvax.paypal.PayPalConstants;
import com.trsvax.paypal.PayPalPayment;
import com.trsvax.shop.Invoice;
import com.trsvax.shop.InvoiceItem;
import com.trsvax.shop.PaymentMethod;

public class PayPalRestServiceImpl implements PayPalRestService {
	private final Logger logger;
	private final String returnURL;
	private final String cancelURL;
	
	public PayPalRestServiceImpl(Logger logger, 
			@Symbol(PayPalConstants.PayPalConfig) String filename,
			@Symbol(PayPalConstants.RETURNURL) String returnURL,
			@Symbol(PayPalConstants.CANCELURL) String cancelURL
			) {
		this.logger = logger;
		this.cancelURL = cancelURL;
		this.returnURL = returnURL;
		try {
			InputStream is = new FileInputStream(filename);
			PayPalResource.initConfig(is);
		} catch (Exception e) {
			logger.error("config failed {}",e.getMessage());
		}
	}

	public OAuthTokenCredential getCredentials() {
		String clientID = ConfigManager.getInstance().getValue("clientID");
		String clientSecret = ConfigManager.getInstance().getValue("clientSecret");
		logger.info("PayPal id {}",clientID);
		return new OAuthTokenCredential(clientID, clientSecret);
	}

	public String getToken() throws PayPalRESTException {
		logger.info("get token");
		return getCredentials().getAccessToken();
	}

	public Payment payCreditCard(CreditCardPayment payment, Invoice invoice) throws PayPalRESTException {
		invoice.getPaymentMethod().setError(null);
		invoice.setPaymentMethod(payment);

        Address billingAddress = new Address();
        billingAddress.setLine1( payment.getBillingAddress().getStreet());
        billingAddress.setCity(payment.getBillingAddress().getCity());
        billingAddress.setCountryCode("US");
        billingAddress.setPostalCode(payment.getBillingAddress().getZIP());
        billingAddress.setState(payment.getBillingAddress().getState());

        CreditCard creditCard = new CreditCard();
        creditCard.setNumber(payment.getNumber());
        creditCard.setType(payment.getType());
        creditCard.setExpireMonth(payment.getExpireMonth());
        creditCard.setExpireYear(payment.getExpireYear());
        creditCard.setCvv2(payment.getCvv2());
        creditCard.setFirstName(payment.getFirstName());
        creditCard.setLastName(payment.getLastName());
        creditCard.setBillingAddress(billingAddress);

        AmountDetails amountDetails = new AmountDetails();
        amountDetails.setSubtotal(invoice.calculateSubtotal().toPlainString());
        amountDetails.setTax(invoice.calculateTax().toPlainString());
        amountDetails.setShipping(invoice.getShipping().toPlainString());

        Amount amount = new Amount();
        amount.setTotal(invoice.calculateTotal().toPlainString());
        amount.setCurrency(invoice.getCurrency());
        amount.setDetails(amountDetails);

        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setDescription(invoice.getDescription());

        List<Transaction> transactions = new ArrayList<Transaction>();
        transactions.add(transaction);

        FundingInstrument fundingInstrument = new FundingInstrument();
        fundingInstrument.setCreditCard(creditCard);

        List<FundingInstrument> fundingInstruments = new ArrayList<FundingInstrument>();
        fundingInstruments.add(fundingInstrument);

        Payer payer = new Payer();
        payer.setFundingInstruments(fundingInstruments);
        payer.setPaymentMethod("credit_card");

        Payment payPalPayment = new Payment();
        payPalPayment.setIntent("sale");
        payPalPayment.setPayer(payer);
        payPalPayment.setTransactions(transactions);

        logger.info("create payment");
        APIContext apiContext = new APIContext(getToken());
        logger.info("request id {}", apiContext.getRequestId());
         Payment createdPayment = payPalPayment.create(apiContext);
         logger.info("payment id {}",createdPayment.getId());
         payment.setTransactionID(createdPayment.getId());
         logger.info("id set");
         for ( SubTransaction subTransaction : createdPayment.getTransactions().get(0).getRelatedResources() ) {
        	 logger.info("get sub id");
        	 String id = subTransaction.getSale().getId();
        	 logger.info("sub id {}",id);
        	 payment.setTransactionID(id);
        	 break;
         }
 		invoice.getPaymentMethod().setState(PaymentMethod.PAID);

         return createdPayment;
		
	}

	public Payment payPayPal(PayPalPayment payment, Invoice invoice) throws PayPalRESTException {
		invoice.setPaymentMethod(payment);

	
		RedirectUrls redirectUrls = new RedirectUrls();
		//redirectUrls.setCancelUrl("http://localhost:8080/studio/paypal/cancel");
		//redirectUrls.setReturnUrl("http://localhost:8080/studio/paypal/return");

		redirectUrls.setCancelUrl(cancelURL);
		redirectUrls.setReturnUrl(returnURL);
	
		ItemList itemList = new ItemList();
		List<Item> items = new ArrayList<Item>();
		
		if ( invoice.getDiscount() == null ) {
			for ( InvoiceItem invoiceItem : invoice.getItems() ) {
				Item item = new Item();
				item.setCurrency("USD");
				item.setName(invoiceItem.getDescription());
				item.setPrice(invoiceItem.getPrice().toPlainString());
				item.setQuantity(invoiceItem.getQuantity().toString());
				items.add(item);
			}
			itemList.setItems(items);
		}

		
		AmountDetails amountDetails = new AmountDetails();
        amountDetails.setSubtotal(invoice.calculateSubtotal().toPlainString());
        amountDetails.setTax(invoice.calculateTax().toPlainString());
        amountDetails.setShipping(invoice.getShipping().toPlainString());

        Amount amount = new Amount();
        amount.setTotal(invoice.calculateTotal().toPlainString());
        amount.setCurrency(invoice.getCurrency());
        amount.setDetails(amountDetails);
        		
		Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setDescription(invoice.getDescription());
        transaction.setItemList(itemList);

        List<Transaction> transactions = new ArrayList<Transaction>();
        transactions.add(transaction);
		
		Payer payer = new Payer();
		payer.setPaymentMethod("paypal");
		
		Payment payPalPayment = new Payment();
        payPalPayment.setIntent("sale");
        payPalPayment.setPayer(payer);
        payPalPayment.setTransactions(transactions);       
		payPalPayment.setRedirectUrls(redirectUrls);
        

        Payment createdPayment = payPalPayment.create(getToken());
        payment.setTransactionID(createdPayment.getId());
        logger.info("id {}",createdPayment.getId());
        return createdPayment;

		
		
	}

	public Payment completePayPal(String payerID, Invoice invoice) throws PayPalRESTException {		
		PaymentExecution paymentExecution = new PaymentExecution();
		paymentExecution.setPayerId(payerID);
		
		String token = getToken();
		Payment payment = Payment.get(token, invoice.getPaymentMethod().getTransactionID());
		payment.execute(token, paymentExecution);
		invoice.getPaymentMethod().setState(PaymentMethod.PAID);

		return null;
	}


	
	
}
