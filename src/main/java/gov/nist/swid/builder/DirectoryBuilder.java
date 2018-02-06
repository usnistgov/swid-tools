
package gov.nist.swid.builder;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class DirectoryBuilder extends AbstractFileSystemItemBuilder<DirectoryBuilder> {
    private Map<String, DirectoryBuilder> directoryMap = new LinkedHashMap<>();
    private List<ResourceBuilder> resources = new LinkedList<>();

    @Override
    public void reset() {
        super.reset();
    }

    public static DirectoryBuilder create() {
        return new DirectoryBuilder();
    }

    @Override
    public <T> void accept(ResourceCollectionEntryGenerator<T> creator, T parentContext) {
        creator.generate(this, parentContext);
    }

    /**
     * Retrieves the child resources.
     * 
     * @return the resources
     */
    public List<ResourceBuilder> getResources() {
        return resources;
    }

    /**
     * Retrieves or creates the named directory resource if it doesn't exist.
     * 
     * @param name
     *            the directory name
     * @return a directory resource
     */
    public DirectoryBuilder getDirectoryResource(String name) {
        DirectoryBuilder retval = directoryMap.get(name);
        if (retval == null) {
            retval = DirectoryBuilder.create();
            retval.name(name);
            directoryMap.put(name, retval);
            resources.add(retval);
        }
        return retval;
    }

    public FileBuilder newFileResource(String filename) {
        FileBuilder retval = FileBuilder.create();
        retval.name(filename);
        resources.add(retval);
        return retval;
    }

}
