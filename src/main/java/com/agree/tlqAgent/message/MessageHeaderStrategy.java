package com.agree.tlqAgent.message;

import org.dom4j.Element;

public class MessageHeaderStrategy {
	private Element headerElement = null;

	public MessageHeaderStrategy(Element headerElement) {
		this.headerElement = headerElement;
	}

	// TODO
	public Element getHeader() {
		String txId = headerElement.elementText("TXID");
		headerElement = decorateHeaderByTxId(headerElement, txId);
		return headerElement;
	}

	private Element decorateHeaderByTxId(Element headerElement, String txId) {
		// TODO Auto-generated method stub
		return headerElement;
	}
}
