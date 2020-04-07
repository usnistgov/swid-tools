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

package gov.nist.secauto.swid.client.totp;

import gov.nist.secauto.swid.client.totp.Hotp.HashAlgorithm;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

/**
 * A direct implementation of https://tools.ietf.org/html/rfc6238.
 * 
 */
public class Totp {
  // the size of the time step in seconds
  private int timeStepSeconds;
  // the time adjustment from UNIX time in seconds (based on UTC and the
  // epoch)
  private long T0;
  // the internal HOTP implementation
  private Hotp hotp;

  /**
   * Creates a default Totp instance with a 30 second time step, T0 of 0 using HMAC-SHA-1 producing an
   * 8 digit output.
   */
  public Totp() {
    this(30, 0L, HashAlgorithm.SHA1, 8);
  }

  /**
   * Creates a Totp instance with a 30 second time step, T0 of 0 using the provided parameters.
   * 
   * @param algorithm
   *          the algorithm to use
   * @param digits
   *          the number of digits
   */
  public Totp(HashAlgorithm algorithm, int digits) {
    this(30, 0L, algorithm, digits);
  }

  /**
   * Creates a Totp instance with a 30 second time step, T0 of 0 using the provided parameters.
   * 
   * @param timeStepSeconds
   *          the size of the time step in seconds (this is X in the RFC)
   * @param algorithm
   *          the algorithm to use
   * @param T0
   *          the time adjustment from UNIX time in seconds (based on UTC and the epoch)
   * @param digits
   *          the number of digits
   * @see Hotp
   */
  public Totp(int timeStepSeconds, long T0, HashAlgorithm algorithm, int digits) {
    if (timeStepSeconds <= 0) {
      throw new IllegalArgumentException("timeStepSeconds must be greater than zero");
    }
    this.timeStepSeconds = timeStepSeconds;
    this.T0 = T0;
    this.hotp = new Hotp(algorithm, digits);
  }

  /**
   * Calculates a TOTP result using the provided key and the current time
   * 
   * @param key
   *          the key/seed to use for calculating the value
   * @return the TOTP value for the current time point
   */
  public String totp(byte[] key) {
    return totp(key, currentTime());
  }

  /**
   * Calculates a TOTP result using the provided key and provided time
   * 
   * @param key
   *          the key/seed to use for calculating the value
   * @param time
   *          the time value to use
   * @return the TOTP value for the provided time point
   */
  public String totp(byte[] key, long time) {
    long T = (time - T0) / timeStepSeconds;
    return hotp.generate(key, T);
  }

  /**
   * @return the current UNIX time (based on UTC and epoch) in seconds
   */
  private long currentTime() {
    Calendar calendar = GregorianCalendar.getInstance(TimeZone.getTimeZone("UTC"));
    return calendar.getTimeInMillis() / 1000;
  }
}
