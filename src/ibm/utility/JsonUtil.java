package com.ibm.utility;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.xml.XMLConstants;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.json.JSONException;
import org.apache.xml.serialize.OutputFormat;
import org.apache.xml.serialize.XMLSerializer;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.ibm.sterling.afc.jsonutil.PLTJSONUtils;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfs.japi.YFSEnvironment;

public class JsonUtil {

	public String getJsonString(Document inDoc) throws JSONException{
		
		org.apache.commons.json.JSONObject jsonObj = PLTJSONUtils.getJSONObjectFromXML(inDoc.getDocumentElement(), null, null);
		
		return jsonObj.toString();
	}
	
	public boolean validateXMLSchema(YFSEnvironment env, Document inDoc) throws Exception{
    	String strApiname = "createStore";
    	if(YFCObject.isVoid(strApiname))
    		throw new Exception("API name in the argument is not configured");
        try {
            SchemaFactory factory = 
                    SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new StreamSource(new File("C:\\IBMOMS\\xapidocs\\extn\\output\\xsd\\createStore.xsd")));
            Validator validator = schema.newValidator();
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            OutputFormat outputFormat = new OutputFormat(inDoc);
            XMLSerializer serializer = new XMLSerializer(outputStream, outputFormat);
            serializer.serialize(inDoc);
            validator.validate(new StreamSource(new ByteArrayInputStream(outputStream.toByteArray())));
        } catch (IOException | SAXException e) {
            System.out.println("validateXMLSchema Exception: "+e.getMessage());
            return false;
        }
        System.out.println("validateXMLSchema No Error");
        return true;
    }
    
    public String getXsdFilePath(YFSEnvironment env, String strApiName){
    	String strFilePath = "";
    	//strFilePath = props.getProperty("XSD_FILEPATH");
    	System.out.println("validateXMLSchema xsd file path from argument is : "+ strFilePath);
    	if(YFCObject.isVoid(strFilePath) && !YFCObject.isVoid(strApiName))
    		strFilePath = "/global/template/xsd/"+strApiName+".xsd";
    	System.out.println("validateXMLSchemab xsd file path from jar is : "+ strFilePath);
    	return "C:\\IBMOMS\\xapidocs\\extn\\output\\xsd\\createStore.xsd";	
    }
    
    public boolean validateXMLSchema(String xsdPath, String xmlPath){
        
        try {
            SchemaFactory factory = 
                    SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = factory.newSchema(new File(xsdPath));
            Validator validator = schema.newValidator();
            validator.validate(new StreamSource(new File(xmlPath)));
        } catch (IOException | SAXException e) {
            System.out.println("Exception: "+e.getMessage());
            return false;
        }
        System.out.println("No Error");
        return true;
    }
}
