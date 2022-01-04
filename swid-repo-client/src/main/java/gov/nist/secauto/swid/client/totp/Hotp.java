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

import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * A direct implementation of https://tools.ietf.org/html/rfc4226 and
 * https://tools.ietf.org/html/rfc6238 C = counter K = key Digits = length of OTP Note that a strict
 * implementation of RFC 4226 will only support HMAC-SHA-1.
 */
public class Hotp {

  public static enum HashAlgorithm {
    SHA1("HMACSHA1"),
    SHA256("HMACSHA256"),
    SHA512("HMACSHA512");

    private String name;

    private HashAlgorithm(String name) {
      this.name = name;
    }

    public String getAlgorithmName() {
      return name;
    }
  }

  private static class Digits {
    private long value;
    private int length;

    /**
     * @param length
     *          the number of digits in the OTP
     */
    private Digits(int length) {
      if (length < 6 || length > 9) {
        // 6 is the minimum in the RFC and 19 is the maximum that a long
        // can represent
        // 9 is the maximum number of digits supported in the RFC
        throw new IllegalArgumentException("the length must be between 6 and 9 digits");
      }
      this.length = length;
      StringBuilder builder = new StringBuilder("1");
      for (int i = 0; i < length; i++) {
        builder.append('0');
      }
      value = Long.valueOf(builder.toString());
    }

    public int length() {
      return length;
    }

    public long mod(long input) {
      return input % value;
    }
  }

  /** the hash algorithm */
  private HashAlgorithm algorithm;
  /** the number of digits */
  private Digits digits;

  /**
   * Creates a new instance
   * 
   * @param algorithm
   *          the algorithm to use
   * @param length
   *          the number of digits
   */
  public Hotp(HashAlgorithm algorithm, int length) {
    this.algorithm = algorithm;
    this.digits = new Digits(length);
  }

  /**
   * Generates an HOTP value according to RFC 4226
   * 
   * @param key
   *          the shared secret ('K')
   * @param counter
   *          the counter ('C') value
   * @return the HOTP value
   */
  public String generate(byte[] key, long counter) {
    try {
      byte[] hs = hashString(key, counter);
      long hotp = truncate(hs);
      return pad(Long.toString(hotp), this.digits.length());
    } catch (InvalidKeyException e) {
      throw new RuntimeException("configuration error, invalid key", e);
    } catch (NoSuchAlgorithmException e) {
      throw new RuntimeException("configuration error, missing algorithm support", e);
    }
  }

  /**
   * A helper function for padding a String up to a minimum number of characters
   * 
   * @param s
   *          the string to pad
   * @param minLength
   *          the minimum
   * @return the string or a string padded up to the minimum length
   */
  private String pad(String s, int minLength) {
    int padding = minLength - s.length();
    if (padding <= 0) {
      // nothing to do
      return s;
    }
    StringBuilder builder = new StringBuilder();
    for (int i = 0; i < padding; i++) {
      builder.append("0");
    }
    builder.append(s);
    return builder.toString();
  }

  /**
   * Performs dynamic truncation as described in section 5.3
   * 
   * @param hash
   *          the input hash
   * @return the truncated value
   */
  private long truncate(byte[] hash) {
    // put selected bytes into result int
    int offset = hash[hash.length - 1] & 0xf;
    int binary = ((hash[offset] & 0x7f) << 24) | ((hash[offset + 1] & 0xff) << 16) | ((hash[offset + 2] & 0xff) << 8)
        | (hash[offset + 3] & 0xff);
    return digits.mod(binary);
  }

  /**
   * Generates HS as described in step 1. of section 5.3
   * 
   * @param key
   *          the secret Key or 'K' value
   * @param counter
   *          the counter or 'C' value
   * @return hash string
   * @throws NoSuchAlgorithmException
   * @throws InvalidKeyException
   */
  private byte[] hashString(byte[] key, long counter) throws NoSuchAlgorithmException, InvalidKeyException {
    Mac mac = Mac.getInstance(algorithm.getAlgorithmName());
    SecretKeySpec macKey = new SecretKeySpec(key, algorithm.getAlgorithmName());
    ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
    buffer.putLong(counter);
    mac.init(macKey);
    return mac.doFinal(buffer.array());
  }

}
