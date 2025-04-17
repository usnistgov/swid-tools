/**
 * Portions of this software was developed by employees of the National Institute
 * of Standards and Technology (NIST), an agency of the Federal Government and is
 * being made available as a public service. Pursuant to title 17 United States
 * Code Section 105, works of NIST employees are not subject to copyright
 * protection in the United States. This software may be subject to foreign
 * copyright. Permission in the United States and in foreign countries, to the
 * extent that NIST may hold copyright, to use, copy, modify, create derivative
 * works, and distribute this software and its documentation without fee is hereby
 * granted on a non-exclusive basis, provided that this notice and disclaimer
 * of warranty appears in all copies.
 *
 * THE SOFTWARE IS PROVIDED 'AS IS' WITHOUT ANY WARRANTY OF ANY KIND, EITHER
 * EXPRESSED, IMPLIED, OR STATUTORY, INCLUDING, BUT NOT LIMITED TO, ANY WARRANTY
 * THAT THE SOFTWARE WILL CONFORM TO SPECIFICATIONS, ANY IMPLIED WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, AND FREEDOM FROM
 * INFRINGEMENT, AND ANY WARRANTY THAT THE DOCUMENTATION WILL CONFORM TO THE
 * SOFTWARE, OR ANY WARRANTY THAT THE SOFTWARE WILL BE ERROR FREE.  IN NO EVENT
 * SHALL NIST BE LIABLE FOR ANY DAMAGES, INCLUDING, BUT NOT LIMITED TO, DIRECT,
 * INDIRECT, SPECIAL OR CONSEQUENTIAL DAMAGES, ARISING OUT OF, RESULTING FROM,
 * OR IN ANY WAY CONNECTED WITH THIS SOFTWARE, WHETHER OR NOT BASED UPON WARRANTY,
 * CONTRACT, TORT, OR OTHERWISE, WHETHER OR NOT INJURY WAS SUSTAINED BY PERSONS OR
 * PROPERTY OR OTHERWISE, AND WHETHER OR NOT LOSS WAS SUSTAINED FROM, OR AROSE OUT
 * OF THE RESULTS OF, OR USE OF, THE SOFTWARE OR SERVICES PROVIDED HEREUNDER.
 */

package gov.nist.secauto.swid.client.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Header;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.file.Files;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.Principal;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

public class HTTPServiceImpl implements HTTPService {

  private static final Logger LOG = LogManager.getLogger(HTTPServiceImpl.class);

  public static final String TOKEN_CACHE_FILENAME = "./.access";

  /*
   * (non-Javadoc)
   * 
   * @see gov.nist.secauto.swid.client.client.PostService#postSWID(java.lang.String, java.lang.String,
   * java.lang.String, java.lang.String, java.lang.String, java.nio.file.Path, java.lang.String)
   */
  @Override
  public String postSwid(String clientCertificatePath, String clientCertificatePassword, String passwordSeed,
      List<String> swidFiles, Action action, TagType type) throws KeyStoreException, NoSuchAlgorithmException,
      CertificateException, IOException, KeyManagementException, UnrecoverableKeyException, TransformerException {

    HttpClientFactory clientFactory = new HttpClientFactory();
    KeyStore identityKeyStore = clientFactory.loadKeyStore(clientCertificatePath, clientCertificatePassword);
    CloseableHttpClient client = clientFactory.build(identityKeyStore, clientCertificatePassword);
    String subjectDN = getKeyStoreSubjectDN(identityKeyStore);
    try {

      String token = getCachedToken(subjectDN);
      if (token == null) {
        LoginService loginService = new LoginServiceImpl();
        token = loginService.login(client, passwordSeed);
        setTokenCache(token, subjectDN);
      } else {
        LOG.info("Proceeding with cached token");
      }

      if (token != null && !token.isEmpty()) {
        String response = this.sendSWIDData(client, token, swidFiles, action, type);
        if (response != null && !response.isEmpty()) {
          LOG.info("***********************Post Application response***********************\n" + response);
        }
        return response;
      } else {
        LOG.info("Authentication Failed, unable to post SWID to NVD repository");
      }
    } finally {
      client.close();
    }
    return null;

  }

  /*
   * (non-Javadoc)
   * 
   * @see gov.nist.secauto.swid.client.client.PostService#postSWID(java.lang.String, java.lang.String,
   * java.lang.String, java.lang.String, java.lang.String, java.nio.file.Path, java.lang.String)
   */
  @Override
  public String sendSWIDData(CloseableHttpClient client, String token, List<String> swidFiles, Action action,
      TagType type) throws KeyStoreException, NoSuchAlgorithmException, CertificateException, IOException,
      KeyManagementException, UnrecoverableKeyException, TransformerException {

    String responseString = callEndPoint(client, swidFiles, action, token, type);

    return StringEscapeUtils.unescapeXml(prettyFormat(responseString, 3));

  }

  /**
   * Execute insert or update http request
   * 
   * @param httpClient
   * @param swidDataFilePath
   * @param action
   * @param token
   * @return
   * @throws IOException
   * @throws TransformerException
   */
  private String callEndPoint(CloseableHttpClient httpClient, List<String> swidFiles, Action actionType, String token,
      TagType type) throws IOException, TransformerException {
    PayloadBuilderService payloadBuilder = new PayloadBuilderServiceImpl();
    String responseString = "";
    String endPointURL = "";

    endPointURL = actionType.fetchEndpoint();
    LOG.info("Calling SWID " + actionType.action + ": " + endPointURL);
    String payload = payloadBuilder.buildPayload(swidFiles, type);
    StringEntity entity = new StringEntity(payload);

    // HttpPost
    HttpUriRequest httpRequest = new HttpPost(endPointURL);
    if (actionType.equals(Action.insert)) {
      httpRequest = new HttpPost(endPointURL);
      ((HttpPost) httpRequest).setEntity(entity);
    } else if (actionType.equals(Action.update)) {
      httpRequest = new HttpPut(endPointURL);
      ((HttpPut) httpRequest).setEntity(entity);
    }

    httpRequest.setHeader("Accept", "application/xml");
    httpRequest.setHeader("Content-type", "application/xml");
    httpRequest.setHeader("Authorization", "Bearer " + token);

    LOG.info("SWID request payload *****************************************\n" + prettyFormat(payload, 2));

    HttpResponse httpResponse = httpClient.execute(httpRequest);
    LOG.info("Response Status :" + httpResponse.getStatusLine());
    BufferedReader rd = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent()));
    String line = "";
    while ((line = rd.readLine()) != null) {
      responseString += line;
    }

    return responseString;
  }

  /**
   * Format XML response for human readability
   * 
   * @param input
   *          input string to format
   * @param indent
   *          the number of space to indext
   * @return the formatted string
   * @throws TransformerException
   *           if transformation error occurs
   */
  public static String prettyFormat(String input, int indent) throws TransformerException {

    if (input == null || input.isEmpty()) {
      return null;
    }
    Source xmlInput = new StreamSource(new StringReader(input));
    StringWriter stringWriter = new StringWriter();
    StreamResult xmlOutput = new StreamResult(stringWriter);
    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    // transformerFactory.setAttribute("indent-number", indent);
    Transformer transformer = transformerFactory.newTransformer();
    transformer.setOutputProperty(OutputKeys.INDENT, "yes");
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    transformer.transform(xmlInput, xmlOutput);
    return xmlOutput.getWriter().toString();

  }

  /**
   * Format XML input String
   * 
   * @param input
   *          the string to format
   * @return the formatted string
   * @throws TransformerException
   *           if error with formatting occurs
   */
  public static String prettyFormat(String input) throws TransformerException {
    return prettyFormat(input, 2);
  }

  /**
   * Fetch token if cached
   * 
   * @param subjectDN
   *          the DN of the certificate
   * @return the cached token string
   * @throws UnsupportedEncodingException
   *           if encoding error occurs
   * @throws IOException
   *           if error with read or write
   */
  public String getCachedToken(String subjectDN) throws UnsupportedEncodingException, IOException {
    String token = null;
    String dn = null;
    String data = null;
    File file = new File(TOKEN_CACHE_FILENAME);
    if (file.exists() && file.isFile()) {
      try {
        data = new String(Files.readAllBytes(file.toPath()));
        String[] items = data.split("\n");
        if (items != null && items.length == 2) {
          dn = items[0];
          token = items[1];
        }
        if (dn != null && !dn.isEmpty() && !dn.equals(subjectDN)) {
          throw new JWTException("Cached DN does not match DN of cient certificate provided");
        }
        if (token != null) {
          int index = token.lastIndexOf('.');
          String withoutSignature = token.substring(0, index + 1);
          @SuppressWarnings({ "unused", "rawtypes" })
          Jwt<Header, Claims> untrusted = Jwts.parser().unsecured().build().parseUnsecuredClaims(withoutSignature);

        } else {
          throw new JWTException("Cached token is NULL");
        }
      } catch (io.jsonwebtoken.ExpiredJwtException exp) {
        LOG.info("Cached token expired, proceed to attempt re-authentication");
        return null;
      } catch (JWTException e) {
        LOG.info(e.getMessage() + ", proceed to attempt re-authentication");
        return null;
      } catch (Exception e) {
        LOG.info("Cached token is invalid, proceed to attempt re-authentication");
        return null;
      }

    }

    return token;
  }

  /**
   * Set token cache
   * 
   * @param token
   *          the token to cache
   * @param subjectDN
   *          DN of the certificate
   * @throws IOException
   *           if read or write error occurs
   */
  public void setTokenCache(String token, String subjectDN) throws IOException {
    FileWriter writer = null;

    if (token == null) {
      LOG.info("Unable to cache token, token is null");
      return;
    }
    try {

      writer = new FileWriter(new File(TOKEN_CACHE_FILENAME));
      writer.write(subjectDN + "\n" + token);
      LOG.info("Token cached");
    } finally {
      if (writer != null) {
        writer.close();
      }
    }
  }

  /**
   * Get Subject DN from Keystore instance
   * 
   * @param ks
   *          the keystore
   * @return the string which is the DN of the certificate
   * @throws KeyStoreException
   *           if error occurs with keystore loading
   */
  public String getKeyStoreSubjectDN(KeyStore ks) throws KeyStoreException {
    Enumeration<String> enumeration = ks.aliases();
    String subjectDN = null;
    while (enumeration.hasMoreElements()) {
      String alias = (String) enumeration.nextElement();

      Certificate cert = ks.getCertificate(alias);
      if (cert instanceof X509Certificate) {
        X509Certificate x509cert = (X509Certificate) cert;

        // Get subject
        Principal principal = x509cert.getSubjectDN();
        subjectDN = principal.getName();
        LOG.info("Subject DN from certificate: " + subjectDN);
      }
    }

    return subjectDN;

  }

}
