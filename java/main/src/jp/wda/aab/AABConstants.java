/**
 *
 */
package jp.wda.aab;

import java.util.Properties;

import org.apache.commons.io.FilenameUtils;

/**
 * @author u-kat
 *
 */
public class AABConstants {

	public static final String DEFAULT_PROPERTIES_FILE = "aab.properties";

	public static final String SEARCH_TYPE_ALL = "all";

	public static final String ATF_EXTENTION = ".atf";
	public static final String IMAGE_FILE_PLACEHOLDER = "${input}";
	public static final String ATF_FILE_PLACEHOLDER   = "${output}";


	public static final String PROPERTIES_REPOSITORY_LOCSTION = "git.repository";

	public static final String PROPERTIES_COMMIT_MESSAGE      = "git.commit.message";

	public static final String DEFAULT_COMMIT_MESSAGE      = "atf auto builder";

	public static final String PROPERTIES_ATF = "atf.";

	public static final String PROPERTIES_ATF_COMMAND = ".command";
	public static final String PROPERTIES_ATF_PARAMETERS = ".params";

	public static final String PROPERTIES_ATFSDK_LOCATION = PROPERTIES_ATF + "sdkpath";

	public static final String PROPERTIES_FILE_TYPES = PROPERTIES_ATF + "fileTypes";

	public static final String PROPERTIES_SEARCH_TYPE = PROPERTIES_ATF + "search";


	public static String command(Properties propeties, String type) {
		return FilenameUtils.concat(
				  propeties.getProperty(PROPERTIES_ATFSDK_LOCATION)
				, propeties.getProperty(PROPERTIES_ATF + type + PROPERTIES_ATF_COMMAND));
	}

	public static String[] params(Properties propeties, String type) {
		String params = propeties.getProperty(PROPERTIES_ATF + type + PROPERTIES_ATF_PARAMETERS);
		if(params == null || params.length() == 0) { return new String[0]; }
		return params.split(" ");
	}

}
