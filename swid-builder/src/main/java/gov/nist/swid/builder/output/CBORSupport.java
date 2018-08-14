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

package gov.nist.swid.builder.output;

import com.fasterxml.jackson.dataformat.cbor.CBORGenerator;

import java.io.IOException;
import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class CBORSupport {

  private static final Pattern INTEGER_PATTERN = Pattern.compile("[-]?[1-9]\\d*");

  protected static void writeField(CBORGenerator generator, long fieldId, CBORWritable value) throws IOException {
    generator.writeFieldId(fieldId);
    value.write(generator);

  }

  protected static void writeTextField(CBORGenerator generator, long fieldId, String text) throws IOException {
    generator.writeFieldId(fieldId);
    generator.writeString(text);
  }

  protected static void writeBooleanField(CBORGenerator generator, long fieldId, boolean state) throws IOException {
    generator.writeFieldId(fieldId);
    generator.writeBoolean(state);

  }

  protected static void writeLongField(CBORGenerator generator, long fieldId, long value) throws IOException {
    generator.writeFieldId(fieldId);
    generator.writeNumber(value);
  }

  protected static void writeIntegerField(CBORGenerator generator, long fieldId, int value) throws IOException {
    generator.writeFieldId(fieldId);
    generator.writeNumber(value);
  }

  protected static void writeIntegerField(CBORGenerator generator, long fieldId, BigInteger value) throws IOException {
    generator.writeFieldId(fieldId);
    generator.writeNumber(value);
  }

  protected static void writeDateTimeField(CBORGenerator generator, long fieldId, ZonedDateTime dateTime)
      throws IOException {
    generator.writeFieldId(fieldId);
    generator.writeString(dateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
  }

  protected static void writeIntegerOrTextField(CBORGenerator generator, long fieldId, String value)
      throws IOException {
    if (INTEGER_PATTERN.matcher(value).matches()) {
      BigInteger intValue = new BigInteger(value);
      writeIntegerField(generator, fieldId, intValue);
    } else {
      writeTextField(generator, fieldId, value);
    }
  }

  protected static void writeBinaryField(CBORGenerator generator, long fieldId, byte[] value) throws IOException {
    generator.writeFieldId(fieldId);
    generator.writeBinary(value);
  }
}
