

import org.junit.runner.RunWith;

import gov.nist.decima.testing.PathRunner;

@RunWith(PathRunner.class)
@PathRunner.Paths("src/test/resources/unit-tests")
@PathRunner.Requirements(value="classpath:requirements.xml",extensions="classpath:swid-requirements-ext.xsd")
public class UnitIntegrationTest {
//	public static List<File> paths() {
//		return Collections.singletonList(new File("src/test/resources/unit-tests/baseline-minimal.xml"));
//		return Collections.singletonList(new File("src/test/resources/unit-tests"));
//	}
}
