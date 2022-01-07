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

package gov.nist.secauto.swid.builder.resource;

import static gov.nist.secauto.swid.builder.util.Util.requireNonEmpty;

import gov.nist.secauto.swid.builder.ValidationException;

import java.time.ZonedDateTime;
import java.util.Objects;

public class EvidenceBuilder
    extends AbstractResourceCollectionBuilder<EvidenceBuilder> {
  private ZonedDateTime date;
  private String deviceId;

  public static EvidenceBuilder create() {
    return new EvidenceBuilder();
  }

  protected EvidenceBuilder() {
    super();
  }

  @Override
  public void reset() {
    super.reset();
    this.date = null;
    this.deviceId = null;
  }

  public ZonedDateTime getDate() {
    return date;
  }

  public String getDeviceId() {
    return deviceId;
  }

  /**
   * Sets the date/time for when the evidence was collected.
   * 
   * @param date
   *          a non-null date
   * @return the current instance
   */
  public EvidenceBuilder date(ZonedDateTime date) {
    Objects.requireNonNull(date);
    this.date = date;
    return this;
  }

  /**
   * Identifies the device on which the evidence was collected.
   * 
   * @param deviceId
   *          the identifier for the device
   * @return the current instance
   */
  public EvidenceBuilder deviceId(String deviceId) {
    requireNonEmpty(deviceId, "deviceId");
    this.deviceId = deviceId;
    return this;
  }

  @Override
  public void validate() throws ValidationException {
    super.validate();
  }

}
