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

public enum Action {

  insert("insert", "POST", "/service/rest/external/swid/list"),
  update("update", "PUT", "/service/rest/external/swid/list");

  String action;
  String method;
  String endpoint;
  public static final String WEBSERVICES_DEFAULT_HOSTNAME = "auth.nvd.nist.gov";

  private Action(String action, String method, String endpoint) {
    this.action = action;
    this.method = method;
    this.endpoint = endpoint;

  }

  /**
   * Get the action name for the enum
   * 
   * @return the action name
   */
  public String getAction() {
    return action;
  }

  /**
   * Set the action name
   * 
   * @param action
   *          the action name
   */
  public void setAction(String action) {
    this.action = action;
  }

  /**
   * Return Action name that matches the name.
   * 
   * @param name
   *          the action name
   * 
   * @return the Action enum that matches with the name
   * 
   */
  public static Action findByName(String name) {

    if (name != null) {
      for (Action actionItem : values()) {
        if (name.equals(actionItem.getAction())) {
          return actionItem;
        }
      }
    }
    return null;
  }

  public String getMethod() {
    return method;
  }

  /**
   * Set method
   * 
   * @param method
   *          the http method allowed for the action
   */
  public void setMethod(String method) {
    this.method = method;
  }

  /**
   * Get endpoint URL for the action
   * 
   * @return the endpoint URL for the action
   */
  public String getEndpoint() {
    return endpoint;
  }

  /**
   * Set endpoint URL for the action
   * 
   * @param endpoint
   *          the endpoint URL for the action
   */
  public void setEndpoint(String endpoint) {
    this.endpoint = endpoint;
  }

  /**
   * Return endpoint URL
   * 
   * @return the endpoint URL for the action
   */
  public String fetchEndpoint() {

    return "https://" + getHostName() + endpoint;
  }

  /**
   * Use swidHost environment variable value, if provided
   * 
   * @return the service provider host
   */
  public static String getHostName() {
    String hostname = WEBSERVICES_DEFAULT_HOSTNAME;
    String swidHost = System.getenv("swidhost");
    if (swidHost != null) {
      hostname = swidHost;
    }

    return hostname;
  }

}
