package syssim;

import repo.Resource;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: arosha
 * Date: 4/8/14
 * Time: 11:35 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Coordinator {

    void addResource(Resource resource);

    void removeResource(Resource resource);

    boolean grantPermission(String resourceName, String serverId);

    void releasePermission(String resourceName, String serverId);

    List<String> getResourceIds();

    void updateResource(String resourceName, String serverId, String key, String value);

}