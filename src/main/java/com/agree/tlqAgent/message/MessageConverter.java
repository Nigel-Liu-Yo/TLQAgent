package com.agree.tlqAgent.message;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MessageConverter {
    private static Logger logger = LoggerFactory.getLogger(MessageConverter.class);

    private String message;
    private boolean supportsXML;
    private Document document;
    private Element rootElement;
    private Element headerElement;
    public boolean supportsXML(){
        return  supportsXML;
    }

    public MessageConverter(String message) {
        this.message = message;
        if (message==null) return;
        parseMessage();
    }

    public String getMessageWithoutHeader() {
        // TODO seems that it is unnecessary to convert when sending message to TLQ
        return message;
    }

    private void parseMessage() {
        try {
            document = DocumentHelper.parseText(message);
            rootElement = document.getRootElement();
            headerElement = rootElement.element("HEAD");
            supportsXML = true;
        } catch (DocumentException e) {
            supportsXML = false;
        }

    }

    public String getMessageWithHeader() {
        try {
            headerElement = new MessageHeaderStrategy(headerElement).getHeader();
            return document.asXML();
        } catch (Exception e) {
            logger.error("parse XML error :", e);
        }

        return message;
    }

    public String getSuccessReturnMessage() {
        // TODO Auto-generated method stub
        return null;
    }

    public String getFailReturnMessage(String failReason) {
        Document doc = DocumentHelper.createDocument();
        Element rootElement = doc.addElement("FCC");
        if (supportsXML) {
            Element head = DocumentHelper.createElement("HEAD");
            head.setText(headerElement.getText());
        }
        Element msg = DocumentHelper.createElement("MSG");
        rootElement.add(msg);

        Element code = DocumentHelper.createElement("Code");
        code.setText("99999");
        msg.add(code);

        Element desc = DocumentHelper.createElement("Desc");
        desc.setText(failReason);
        msg.add(desc);
        return doc.asXML();
    }
}
