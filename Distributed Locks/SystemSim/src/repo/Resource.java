package repo;

/**
 * Created with IntelliJ IDEA.
 * User: arosha
 * Date: 4/8/14
 * Time: 8:25 PM
 * To change this template use File | Settings | File Templates.
 */
public interface Resource {

    MapResource cloneResource();

    boolean update(String key, String value);

}
