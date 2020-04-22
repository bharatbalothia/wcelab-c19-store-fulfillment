package com.ibm.utility;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Properties;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.json.JSONException;
import org.elasticsearch.common.jackson.dataformat.yaml.snakeyaml.reader.ReaderException;
import org.w3c.dom.Document;

import com.ibm.sterling.afc.jsonutil.PLTJSONUtils;
import com.yantra.interop.japi.YIFApi;
import com.yantra.interop.japi.YIFCustomApi;
import com.yantra.yfc.core.YFCObject;
import com.yantra.yfc.dom.YFCDocument;
import com.yantra.yfc.log.YFCLogCategory;
import com.yantra.yfs.japi.YFSEnvironment;

public class RestUtility implements YIFCustomApi{
	
	private static YFCLogCategory log;

		String strAuthUrl="";
		String strUser = "";
		String strPassword = "";
		public Properties props;
		YIFApi api  = null;
		static{
			log = YFCLogCategory.instance(RestUtility.class);
		}
	
		private String getBase64EncodedAuthString(){
			String strCredential = props.getProperty("UserId")+":"+props.getProperty("Password");
			return "Basic "+Base64.encodeBase64String(strCredential.getBytes());
		}
		
		public void setProperties(Properties prop) throws Exception {
			this.props = prop;
		}
		
		public Document invokeWebservice(YFSEnvironment env, Document inDoc) {
			Document docResponse = null;
			String strResponse = null;
			String strRequestmethod = props.getProperty("req_method");
			String strgetUrl = props.getProperty("get_url");
			String strpostUrl = props.getProperty("post_url");
			String strUrlParams = inDoc.getDocumentElement().getAttribute("url_params");
			try {
				org.apache.commons.json.JSONObject jsonObj = PLTJSONUtils.getJSONObjectFromXML(inDoc.getDocumentElement(), null, null);
				if(strRequestmethod.equals("GET")){
					strResponse = invokeGet(strgetUrl, strUrlParams);
					
				}else if (strRequestmethod.equals("POST")){
					strResponse = sendPOST(strpostUrl, jsonObj.toString());
				}
				Document xmlResponse = PLTJSONUtils.getXmlFromJSON(strResponse, "Response");
				System.out.println("invokePOST method call end : " +YFCDocument.getDocumentFor(xmlResponse).toString());
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			return docResponse;
		}

		
	private String invokeGet(String url, String message) {
		String strAuth = getBase64EncodedAuthString();
		String strUrl = url+message;
		HttpURLConnection con = null;
		StringBuffer response = null;
		try{
			log.debug("invokeGet method call start : ");
			URL obj = new URL(strUrl);
			con = (HttpURLConnection) obj.openConnection();
			con.setRequestMethod("GET");
			con.setRequestProperty("getContentType", "application/x-www-form-urlencoded");
			con.setRequestProperty("Authorization", strAuth);

			int responseCode = con.getResponseCode();
			log.debug("invokeGet response code is : " +responseCode);
			if (responseCode == HttpURLConnection.HTTP_OK) {
				BufferedReader in = new BufferedReader(new InputStreamReader(
						con.getInputStream()));
				String inputLine;
				response = new StringBuffer();

				while ((inputLine = in.readLine()) != null) {
					response.append(inputLine);
				}
				in.close();
				log.debug("invokeGet method call end : ");
				return (response.toString());
			} else {
				throw new RuntimeException("Request Failed : HTTP error code : "
		                 + con.getResponseCode());
			}
			
		}catch(ReaderException i){
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(!YFCObject.isNull(con))
			con.disconnect();
		}

		return null;

	}
	
	public String sendPOST(String url, String restmessage) throws IOException, JSONException {
		String strAuth = getBase64EncodedAuthString();
		HttpURLConnection con = null;
		StringBuffer response = null;
		String strUrl = url;
		try{
			log.debug("invokePOST method call start : ");
			URL obj = new URL(strUrl);
		con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("POST");
		con.setRequestProperty("Authorization", strAuth);
		con.setRequestProperty("Accept", "application/json");
		con.setRequestProperty("Content-type", "application/json");

		con.setDoOutput(true);
		OutputStream os = con.getOutputStream();
		os.write(restmessage.getBytes());
		os.flush();
		os.close();
		
		int responseCode = con.getResponseCode();
		log.debug("invokePOST response code is : " +responseCode);
		if (responseCode == HttpURLConnection.HTTP_OK) { 
			BufferedReader in = new BufferedReader(new InputStreamReader(
					con.getInputStream()));
			String inputLine;
			response = new StringBuffer();

			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();
			con.disconnect();
			String strResponse = response.toString();
			log.debug("invokePOST method call end : ");
			return strResponse;
		} else {

			throw new RuntimeException("Request Failed : HTTP error code : "
	                 + con.getResponseCode());
		}
		}catch(ReaderException i){
			
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			if(!YFCObject.isNull(con))
			con.disconnect();
		}
		return null;
	}
	
}
