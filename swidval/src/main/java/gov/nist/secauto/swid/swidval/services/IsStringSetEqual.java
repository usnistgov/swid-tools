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

package gov.nist.secauto.swid.swidval.services;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.BooleanValue;
import net.sf.saxon.value.SequenceType;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

public class IsStringSetEqual
    extends ExtensionFunctionDefinition {
  private static final StructuredQName FUNCTION_QNAME
      = new StructuredQName("java-swid", "java:gov.nist.secauto.swid.swidval.schematron", "isStringSetEqual");

  private static final SequenceType[] FUNCTION_ARGUMENTS
      = new SequenceType[] { SequenceType.STRING_SEQUENCE, SequenceType.STRING_SEQUENCE, SequenceType.OPTIONAL_STRING };

  @Override
  public StructuredQName getFunctionQName() {
    return FUNCTION_QNAME;
  }

  @Override
  public SequenceType[] getArgumentTypes() {
    return FUNCTION_ARGUMENTS;
  }

  @Override
  public SequenceType getResultType(SequenceType[] suppliedArgumentTypes) {
    return SequenceType.SINGLE_BOOLEAN;
  }

  @Override
  public ExtensionFunctionCall makeCallExpression() {
    return new FunctionCall();
  }

  private static class FunctionCall
      extends ExtensionFunctionCall {
    @Override
    public Sequence call(XPathContext context, Sequence[] arguments) throws XPathException {
      String left = arguments[0].head().getStringValue();
      String right = arguments[1].head().getStringValue();

      String pattern;
      if (arguments.length == 3) {
        pattern = arguments[2].head().getStringValue();
      } else {
        pattern = "\\s+";
      }

      Set<String> leftSet = parseSet(left, pattern);
      Set<String> rightSet = parseSet(right, pattern);
      boolean retval = leftSet.equals(rightSet);
      return BooleanValue.get(retval);
    }

    private Set<String> parseSet(String str, String pattern) {
      Set<String> retval = new LinkedHashSet<>();
      for (String s : str.split(pattern)) {
        retval.add(s);
      }
      return Collections.unmodifiableSet(retval);
    }
  }
}
