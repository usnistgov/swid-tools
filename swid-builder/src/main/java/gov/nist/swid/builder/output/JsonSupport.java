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
package gov.nist.swid.builder.output;

import com.fasterxml.jackson.core.JsonGenerator;

import java.io.IOException;
import java.math.BigInteger;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class JsonSupport {

  private static final Pattern INTEGER_PATTERN = Pattern.compile("[-]?[1-9]\\d*");

  protected void writeField(JsonGenerator generator, long fieldId) throws IOException {
    generator.writeFieldId(fieldId);
  }

  protected void writeField(JsonGenerator generator, long fieldId, JsonWritable value) throws IOException {
    writeField(generator, fieldId);
    value.write(generator);

  }

  protected void writeTextField(JsonGenerator generator, long fieldId, String text) throws IOException {
    writeField(generator, fieldId);
    generator.writeString(text);
  }

  protected void writeBooleanField(JsonGenerator generator, long fieldId, boolean state) throws IOException {
    writeField(generator, fieldId);
    generator.writeBoolean(state);

  }

  protected void writeLongField(JsonGenerator generator, long fieldId, long value) throws IOException {
    writeField(generator, fieldId);
    generator.writeNumber(value);
  }

  protected void writeIntegerField(JsonGenerator generator, long fieldId, int value) throws IOException {
    writeField(generator, fieldId);
    generator.writeNumber(value);
  }

  protected void writeIntegerField(JsonGenerator generator, long fieldId, BigInteger value) throws IOException {
    writeField(generator, fieldId);
    generator.writeNumber(value);
  }

  protected void writeDateTimeField(JsonGenerator generator, long fieldId, ZonedDateTime dateTime)
      throws IOException {
    writeField(generator, fieldId);
    generator.writeString(dateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME));
  }

  protected void writeIntegerOrTextField(JsonGenerator generator, long fieldId, String value)
      throws IOException {
    if (INTEGER_PATTERN.matcher(value).matches()) {
      BigInteger intValue = new BigInteger(value);
      writeIntegerField(generator, fieldId, intValue);
    } else {
      writeTextField(generator, fieldId, value);
    }
  }

  protected void writeBinaryField(JsonGenerator generator, long fieldId, byte[] value) throws IOException {
    writeField(generator, fieldId);
    generator.writeBinary(value);
  }
}
