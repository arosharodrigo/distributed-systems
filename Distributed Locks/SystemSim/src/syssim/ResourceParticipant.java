package syssim;

import repo.Resource;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created with IntelliJ IDEA.
 * User: arosha
 * Date: 4/8/14
 * Time: 8:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class ResourceParticipant extends Participant implements Coordinator {

    //Participant owned resources
    private Map<String, Resource> resourceMap;

    //Participant issued permissions
    private Map<String, String> resourcePermissions;

    //Other resource locations
    private Map<String, List<String>> resourceLocations;

    //When need to access a resource after sending 'GIVE_PERMISSION' msg to all,
    //this map will fill by grant permission result of coordinators.
    //Result should have 'GRANTED' OR 'NOT-GRANTED'
    private Map<String, List<String>> resultedPermisssions;


    public ResourceParticipant(EventListener listener, SimSystem simSystem, int port) {
        super(listener, simSystem, port);
        resourceMap = new HashMap<String, Resource>();
        resourceLocations = new HashMap<String, List<String>>();
        resourcePermissions = new HashMap<String, String>();
        resultedPermisssions = new ConcurrentHashMap<String, List<String>>();
    }

    public void addResource(Resource resource) {
        resourceMap.put(resource.getName(), resource);
    }

    public void removeResource(Resource resource) {
        resourceMap.remove(resource.getName());
    }

    //After some node/participant need to update all replicas this method should be called by each coordinator
    public void updateResource(String resourceName, String serverId, String key, String value) {
        if(resourcePermissions.containsKey(resourceName)
                && resourcePermissions.get(resourceName) != null
                && !resourcePermissions.get(resourceName).equals(serverId)) {
            //no updates
        } else {
            resourceMap.get(resourceName).update(key, value);
        }
    }

    public boolean grantPermission(String resourceName, String serverId) {
        if (resourcePermissions.containsKey(resourceName) && resourcePermissions.get(resourceName) != null) {
            return false;
        } else {
            resourcePermissions.put(resourceName, serverId);
            return true;
        }
    }

    public void releasePermission(String resourceName, String serverId) {
        if (resourcePermissions.containsKey(resourceName)
                && resourcePermissions.get(resourceName) != null
                && resourcePermissions.get(resourceName).equals(serverId)) {
            resourcePermissions.remove(resourceName);
        }
    }

    public void addToResultedPermissions(String resourceName, String coordinatorId, String result) {
        if(resultedPermisssions.containsKey(resourceName)) {
            List<String> list = resultedPermisssions.get(resourceName);
            if (list == null) {
                list = new ArrayList<String>();
            }
            list.add(result);
            resultedPermisssions.put(resourceName, list);
        } else {
            List<String> list = new ArrayList<String>();
            list.add(result);
            resultedPermisssions.put(resourceName, list);
        }
    }

    //When initializing/updating resourceLocations each node needs each other resource names.
    public List<String> getResourceIds() {
        return new ArrayList<String>(resourceMap.keySet());
    }

    public Map<String, List<String>> getResourceLocations() {
        return resourceLocations;
    }

    public void setResourceLocations(Map<String, List<String>> resourceLocations) {
        this.resourceLocations = resourceLocations;
    }

    public Map<String, List<String>> getResultedPermisssions() {
        return resultedPermisssions;
    }

    public void setResultedPermisssions(Map<String, List<String>> resultedPermisssions) {
        this.resultedPermisssions = resultedPermisssions;
    }
}
