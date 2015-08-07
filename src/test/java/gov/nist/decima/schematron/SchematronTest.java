package gov.nist.decima.schematron;

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPathExpressionException;

import org.apache.xmlbeans.XmlException;
import org.junit.Test;
import org.oclc.purl.dsdl.svrl.ActivePatternDocument.ActivePattern;
import org.oclc.purl.dsdl.svrl.FailedAssertDocument.FailedAssert;
import org.oclc.purl.dsdl.svrl.FiredRuleDocument.FiredRule;
import org.oclc.purl.dsdl.svrl.NsPrefixInAttributeValuesDocument.NsPrefixInAttributeValues;
import org.oclc.purl.dsdl.svrl.SchematronOutputDocument;
import org.oclc.purl.dsdl.svrl.SuccessfulReportDocument.SuccessfulReport;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

public class SchematronTest {

	@Test
	public void testProcess() throws TransformerException, ParserConfigurationException, SAXException, IOException, XPathExpressionException, XmlException {
		Schematron schematron = new Schematron(new StreamSource(getClass().getResourceAsStream("/swid-nistir-8060.sch")));

		StringWriter writer = new StringWriter();
		StreamResult result = new StreamResult(writer);

		DocumentBuilderFactory bf = DocumentBuilderFactory.newInstance();
		DocumentBuilder b = bf.newDocumentBuilder();
		Document doc = b.parse(getClass().getResourceAsStream("/test-swid.xml"));
		DOMSource xml = new DOMSource(doc);

		DOMResult domResult = new DOMResult();
		schematron.process(xml, domResult);
//		schematron.process(xml, result);
//		System.out.println(writer.toString());
//
//		XPathFactory xPathFactory = XPathFactory.newInstance();
//		XPath xpath = xPathFactory.newXPath();
//		XPathExpression xPathExpr = xpath.compile("/*[local-name()='SoftwareIdentity']");
//		NodeList nl = (NodeList)xPathExpr.evaluate(doc, XPathConstants.NODESET);
//		for (int i=0;i<nl.getLength();i++) {
//			Node node = nl.item(i);
//			System.out.println("line: "+node.)
//		}
//		
		SchematronOutputDocument output = SVRLParser.parse(new SVRLHandler() {

			public void handleNSPrefix(NsPrefixInAttributeValues prefix) {
				// TODO Auto-generated method stub
				
			}

			public void handleActivePattern(ActivePattern activePattern) {
				// TODO Auto-generated method stub
				
			}

			public void handleFiredRule(FiredRule xmlObject) {
				// TODO Auto-generated method stub
				
			}

			public void handleSuccessfulReport(SuccessfulReport successfulReport) {
				// TODO Auto-generated method stub
				
			}

			public void handleFailedAssert(FailedAssert failedAssert) {
				// TODO Auto-generated method stub
				
			}
			
		}, domResult.getNode());
	}

	@Test
	public void testStuff() {
		
	}
}
