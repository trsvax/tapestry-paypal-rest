package com.trsvax.paypal;

import org.apache.tapestry5.json.JSONObject;

public class PayPalErrorDetail {
	private String field;
	private String issue;
	
	public PayPalErrorDetail(JSONObject detail) {
		field = detail.getString("field").replace("payer.funding_instruments[0].", "");
		issue = detail.getString("issue");
	}
	public String getField() {
		return field;
	}
	public void setField(String field) {
		this.field = field;
	}
	public String getIssue() {
		return issue;
	}
	public void setIssue(String issue) {
		this.issue = issue;
	}
	
	

}
