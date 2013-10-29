package com.trsvax.paypal;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.json.JSONObject;

public class PayPalError {

		private String name = "Unknown Error";
		private String message;
		private String link;
		
		private List<PayPalErrorDetail> details = new ArrayList<PayPalErrorDetail>();
		
		public PayPalError(String error) {
			int index = error.indexOf("{");
			message = error;
			if ( index < 0 ) {
				return;
			} 
			error = error.substring(index);
			JSONObject object = new JSONObject(error);
			if ( object.has("name")) {
				name = object.getString("name");
			} 
			if ( object.has("message") ) {
				message = object.getString("message");
			}
			if ( object.has("link")) {
				link = object.getString("link");
			}
			
			if ( object.has("details") ) {
				for ( Object detail : object.getJSONArray("details").toList() ) {					
					details.add( new PayPalErrorDetail((JSONObject) detail));
				}
			}
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getMessage() {
			return message;
		}

		public void setMessage(String message) {
			this.message = message;
		}

		public String getLink() {
			return link;
		}

		public void setLink(String link) {
			this.link = link;
		}

		public List<PayPalErrorDetail> getDetails() {
			return details;
		}

		public void setDetails(List<PayPalErrorDetail> details) {
			this.details = details;
		}
		
		
}
