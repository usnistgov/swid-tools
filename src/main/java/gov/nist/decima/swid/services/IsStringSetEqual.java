package gov.nist.decima.swid.services;

import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import net.sf.saxon.expr.XPathContext;
import net.sf.saxon.lib.ExtensionFunctionCall;
import net.sf.saxon.lib.ExtensionFunctionDefinition;
import net.sf.saxon.om.Sequence;
import net.sf.saxon.om.StructuredQName;
import net.sf.saxon.trans.XPathException;
import net.sf.saxon.value.BooleanValue;
import net.sf.saxon.value.SequenceType;

public class IsStringSetEqual extends ExtensionFunctionDefinition {
	private static final StructuredQName FUNCTION_QNAME = new StructuredQName("java-swid",
			"java:gov.nist.decima.swid.schematron", "isStringSetEqual");

	private static final SequenceType[] FUNCTION_ARGUMENTS = new SequenceType[] {
		 SequenceType.SINGLE_STRING,
		 SequenceType.SINGLE_STRING,
		 SequenceType.OPTIONAL_STRING
		 };

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

	private static class FunctionCall extends ExtensionFunctionCall {
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
