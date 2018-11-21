/**
 * Portions of this software was developed by employees of the National Institute
 * of Standards and Technology (NIST), an agency of the Federal Government.
 * Pursuant to title 17 United States Code Section 105, works of NIST employees are
 * not subject to copyright protection in the United States and are considered to
 * be in the public domain. Permission to freely use, copy, modify, and distribute
 * this software and its documentation without fee is hereby granted, provided that
 * this notice and disclaimer of warranty appears in all copies.
 *
 * THE SOFTWARE IS PROVIDED 'AS IS' WITHOUT ANY WARRANTY OF ANY KIND, EITHER
 * EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT LIMITED TO, ANY WARRANTY
 * THAT THE SOFTWARE WILL CONFORM TO SPECIFICATIONS, ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, AND FREEDOM FROM
 * INFRINGEMENT, AND ANY WARRANTY THAT THE DOCUMENTATION WILL CONFORM TO THE
 * SOFTWARE, OR ANY WARRANTY THAT THE SOFTWARE WILL BE ERROR FREE. IN NO EVENT
 * SHALL NIST BE LIABLE FOR ANY DAMAGES, INCLUDING, BUT NOT LIMITED TO, DIRECT,
 * INDIRECT, SPECIAL OR CONSEQUENTIAL DAMAGES, ARISING OUT OF, RESULTING FROM, OR
 * IN ANY WAY CONNECTED WITH THIS SOFTWARE, WHETHER OR NOT BASED UPON WARRANTY,
 * CONTRACT, TORT, OR OTHERWISE, WHETHER OR NOT INJURY WAS SUSTAINED BY PERSONS OR
 * PROPERTY OR OTHERWISE, AND WHETHER OR NOT LOSS WAS SUSTAINED FROM, OR AROSE OUT
 * OF THE RESULTS OF, OR USE OF, THE SOFTWARE OR SERVICES PROVIDED HEREUNDER.
 */

package gov.nist.swid.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.Consts;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import gov.nist.swid.totp.Totp;

public class LoginServiceImpl implements LoginService {

  private static final Logger LOG = LogManager.getLogger(LoginServiceImpl.class);

  public static final String LOGIN_ENDPOINT = "/service/rest/external/client/login";

  /**
   * 
   * @param client
   *          the Http Client
   * @param passwordSeed
   *          the password seed for the client
   * @return the reponse
   * @throws KeyStoreException
   *           if there are errors with loading client certificate
   * @throws NoSuchAlgorithmException
   *           if there are errors
   * @throws CertificateException
   *           if there are errors with loading client certificate
   * @throws IOException
   *           if there are errors with read or write
   * @throws KeyManagementException
   *           if there are errors with certificate
   * @throws UnrecoverableKeyException
   *           if there are errors with loading client certificate
   */
  public String login(CloseableHttpClient client, String passwordSeed) throws KeyStoreException,
      NoSuchAlgorithmException, CertificateException, IOException, KeyManagementException, UnrecoverableKeyException {

    LOG.info("Submitting authentication request ");
    String oneTimePassword = generateOneTimePassword(passwordSeed);
    String response = this.callEndPoint(client, oneTimePassword);
    JSONParser parser = new JSONParser();
    JSONObject jsonObject = null;
    try {
      jsonObject = (JSONObject) parser.parse(response);
      LOG.info("User Authenticated:" + response);
    } catch (ParseException e) {
      LOG.error("Failed Authentication Response :" + response);

    }
    String token = null;
    if (jsonObject != null) {
      token = (String) jsonObject.get("jwt");
    }

    return token;

  }

  /**
   * Generate one time password based on password seed
   * 
   * @param passwordSeed
   * @return
   */
  private String generateOneTimePassword(String passwordSeed) {
    Totp totp = new Totp(gov.nist.swid.totp.Hotp.HashAlgorithm.SHA256, 8);
    Base64 decoder = new Base64();
    return totp.totp(decoder.decode(passwordSeed));

  }

  /**
   * Set parameters and execute the Client request for login
   * 
   * @param aHTTPClient
   *          the http client
   * @param aEndPointURL
   *          the login URL
   * @param password
   *          the password
   * @return the response
   */
  private String callEndPoint(CloseableHttpClient aHTTPClient, String password) throws IOException {
    String response = "";
    try {
      String loginEndpoint = getLoginEndpoint();
      LOG.info("Attempting to authenticate using rest service: " + loginEndpoint);
      HttpPost post = new HttpPost(loginEndpoint);
      post.setHeader("Accept", "application/json");
      post.setHeader("Content-type", "application/x-www-form-urlencoded");

      List<NameValuePair> nvList = new ArrayList<NameValuePair>();
      nvList.add(new BasicNameValuePair("password", password));
      post.setEntity(new UrlEncodedFormEntity(nvList, Consts.UTF_8));
      HttpResponse httpResponse = aHTTPClient.execute(post);
      LOG.info("Response Status: " + httpResponse.getStatusLine());
      BufferedReader rd = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
      response = rd.readLine();

    } catch (Exception ex) {
      LOG.error("Unable to authenticate using login rest service,  " + ex.getMessage());
    }
    return response;
  }

  /**
   * Return the endpoint for login
   * 
   * @return the login endpoint
   */
  private String getLoginEndpoint() {

    return "https://" + Action.getHostName() + LOGIN_ENDPOINT;
  }

}
