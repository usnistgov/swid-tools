import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import gov.nist.swid.builder.EntityBuilder;
import gov.nist.swid.builder.SWIDBuilder;
import gov.nist.swid.builder.TagType;
import gov.nist.swid.builder.output.CBOROutputHandler;

public class Application {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		SWIDBuilder builder = SWIDBuilder.create();
		builder.addEntity(EntityBuilder.create().name("NIST").regid("nist.gov").addRole("tagCreator").addRole("softwareCreator"));
		builder.language("en-US").name("coswid app").tagId("tagId").tagType(TagType.PRIMARY).version("1.0.0").versionScheme("multipart-numeric");

		CBOROutputHandler handler = new CBOROutputHandler();
		BufferedOutputStream os = new BufferedOutputStream(new FileOutputStream(new File("swid.cbor")));
		handler.write(builder, os);
	}

}
