

import java.io.File;
import java.util.Collections;
import java.util.List;

import org.junit.runner.RunWith;

import gov.nist.decima.testing.PathRunner;

@RunWith(PathRunner.class)
public class UnitIntegrationTest {
	public static List<File> paths() {
//		return Collections.singletonList(new File("src/test/resources/unit-tests/baseline-minimal.xml"));
		return Collections.singletonList(new File("src/test/resources/unit-tests"));
	}
}
