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

package gov.nist.secauto.swid.builder;

import java.util.Collection;
import java.util.regex.Pattern;

public abstract class AbstractBuilder implements Builder {
  protected void validateNonNull(String field, Object value) throws ValidationException {
    if (value == null) {
      throw new ValidationException("the field '" + field + "' must be provided");
    }
  }

  protected void validateNonEmpty(String field, String value) throws ValidationException {
    validateNonNull(field, value);

    if (value.isEmpty()) {
      throw new ValidationException("the field '" + field + "' must contain a non-empty value");
    }

  }

  protected void validateNonEmpty(String field, Object[] value) throws ValidationException {
    validateNonNull(field, value);

    if (value.length == 0) {
      throw new ValidationException("the field '" + field + "' must be a non-empty array");
    }

  }

  protected void validateNonEmpty(String field, byte[] value) throws ValidationException {
    validateNonNull(field, value);

    if (value.length == 0) {
      throw new ValidationException("the field '" + field + "' must be a non-empty array");
    }

  }

  protected <T> void validateNonEmpty(String field, Collection<T> value) throws ValidationException {
    validateNonNull(field, value);

    if (value.isEmpty()) {
      throw new ValidationException("the field '" + field + "' must contain a non-empty value");
    }

  }

  protected void validatePatternMatch(String field, Pattern pattern, String value) throws ValidationException {
    validateNonNull(field, value);

    if (!pattern.matcher(value).matches()) {
      StringBuilder builder = new StringBuilder();
      builder.append("the value for field '");
      builder.append(field);
      builder.append("' must match the pattern '");
      builder.append(pattern.pattern());
      builder.append('\'');
      throw new ValidationException(builder.toString());
    }
  }

  @Override
  public final boolean isValid() {
    try {
      validate();
    } catch (RuntimeException | ValidationException e) {
      return false;
    }
    return true;
  }
}
