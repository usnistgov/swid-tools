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

package gov.nist.swid.builder.resource;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HashUtils {
    private HashUtils() {
        // disable
    }

    /**
     * Converts an array of bytes into a list of bytes.
     * 
     * @param bytes
     *            the array of bytes to convert
     * @return a list of bytes
     */
    public static List<Byte> toList(byte[] bytes) {
        List<Byte> retval = new ArrayList<>(bytes.length);
        for (byte b : bytes) {
            retval.add(b);
        }
        return retval;
    }

    /**
     * Converts a list of bytes into an array of bytes.
     * 
     * @param bytes
     *            a list of bytes
     * @return the array of bytes to convert
     */
    public static byte[] toArray(List<Byte> bytes) {
        byte[] retval = new byte[bytes.size()];
        for (int pos = 0; pos < bytes.size(); pos++) {
            retval[pos] = bytes.get(pos);
        }
        return retval;
    }

    private static final char[] hexArray = "0123456789abcdef".toCharArray();

    /**
     * Converts an array of bytes into a hexadecimal string.
     * 
     * @param bytes
     *            the array of bytes to convert
     * @return a hexadecimal string
     */
    public static String toHexString(byte[] bytes) {
        // Based on example from:
        // http://stackoverflow.com/questions/9655181/how-to-convert-a-byte-array-to-a-hex-string-in-java
        char[] hexChars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int value = bytes[i] & 0xFF;
            hexChars[i * 2] = hexArray[value >>> 4];
            hexChars[i * 2 + 1] = hexArray[value & 0x0F];
        }
        return new String(hexChars);
    }

    /**
     * Converts an list of bytes into a hexadecimal string.
     * 
     * @param bytes
     *            the list of bytes to convert
     * @return a hexadecimal string
     */
    public static String toHexString(List<Byte> bytes) {
        // Based on example from:
        // http://stackoverflow.com/questions/9655181/how-to-convert-a-byte-array-to-a-hex-string-in-java
        char[] hexChars = new char[bytes.size() * 2];
        int pos = 0;
        for (byte b : bytes) {
            int value = b & 0xFF;
            hexChars[pos * 2] = hexArray[value >>> 4];
            hexChars[pos * 2 + 1] = hexArray[value & 0x0F];
            ++pos;
        }
        return new String(hexChars);
    }

    /**
     * Generates a hash value, in the form of an array of bytes, by digesting a provided input
     * stream based on the provided hash algorithm.
     * 
     * @param algorithm
     *            the hash function to use
     * @param file
     *            the file to read bytes from
     * @return an array of bytes representing a hash value
     * @throws NoSuchAlgorithmException
     *             if the selected hash function is not supported
     * @throws IOException
     *             if an error occured while reading the input stream
     */
    public static byte[] hash(HashAlgorithm algorithm, File file) throws NoSuchAlgorithmException, IOException {
        return hash(algorithm, new BufferedInputStream(new FileInputStream(file)));
    }

    /**
     * Generates a hash value, in the form of an array of bytes, by digesting a provided input
     * stream based on the provided hash algorithm.
     * 
     * @param algorithm
     *            the hash function to use
     * @param is
     *            the input stream to read bytes from
     * @return an array of bytes representing a hash value
     * @throws NoSuchAlgorithmException
     *             if the selected hash function is not supported
     * @throws IOException
     *             if an error occured while reading the input stream
     */
    public static byte[] hash(HashAlgorithm algorithm, InputStream is) throws NoSuchAlgorithmException, IOException {
        MessageDigest digest = MessageDigest.getInstance(algorithm.getName());
        byte[] dataBytes = new byte[1024];
        int nread = 0;

        while ((nread = is.read(dataBytes)) != -1) {
            digest.update(dataBytes, 0, nread);
        }

        byte[] mdbytes = digest.digest();
        int valueLength = algorithm.getValueLength() / 8;
        if (valueLength < mdbytes.length) {
            mdbytes = Arrays.copyOfRange(mdbytes, 0, valueLength);
        }
        return mdbytes;

        // //convert the bytes to hex format
        // StringBuffer hexString = new StringBuffer();
        // for (int i=0;i<mdbytes.length;i++) {
        // hexString.append(Integer.toHexString(0xFF & mdbytes[i]));
        // }
        //
        // return hexString.toString();
    }
}
