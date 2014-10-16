/**
 *
 */
package jp.wda.aab;

import java.io.File;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;

/**
 * @author u-kat
 *
 */
public class CommandThread extends Thread {

	/**
	 *
	 */
	public CommandThread(Properties systemProperties, File baseDir, String target) {
		super();
		this.systemProperties = systemProperties;

		this.baseDir   = baseDir;
		this.imageFile = new File(baseDir, target);
		this.atfFile   = new File(baseDir, FilenameUtils.removeExtension(target) + AABConstants.ATF_EXTENTION);
		this.type      = FilenameUtils.getExtension(target);
		this.atfExists = this.atfFile.exists();
		this.isTarget  = !this.atfExists ? true : this.atfFile.lastModified() < this.imageFile.lastModified();
	}

	private Properties systemProperties;
	private File baseDir;
	private File imageFile;
	private File atfFile;
	private String type;


	public boolean atfExists;
	public boolean isTarget;
	public boolean completed = false;
	public int result;



	public void run() {
		completed = false;
		String   command = AABConstants.command(systemProperties, type);
		String[] params  = AABConstants.params(systemProperties, type);

		ProcessBuilder pb = new ProcessBuilder(command);
		pb.redirectErrorStream(true);
		List<String> clist = pb.command();

		for(int i = 0; i < params.length; i++) {
			if(AABConstants.IMAGE_FILE_PLACEHOLDER.equals(params[i])) {
				clist.add(FilenameUtils.normalize(imageFile.getAbsolutePath()));
			} else if(AABConstants.ATF_FILE_PLACEHOLDER.equals(params[i])) {
				clist.add(FilenameUtils.normalize(atfFile.getAbsolutePath()));
			} else {
				clist.add(params[i]);
			}
		}

		try {
			Process process = pb.start();
			try (InputStream is = process.getInputStream()) { while(is.read() >= 0); }

			completed = true;
		} catch(Throwable ex) {
			ex.printStackTrace();
		}
	}

	/**
	 *
	 * @return
	 */
	public String getGeneratedFile() {
		if(!this.atfFile.exists()) { return null; }
		if(this.atfFile.lastModified() < this.imageFile.lastModified()) { return null; }

		String base = FilenameUtils.normalize(baseDir.getAbsolutePath(), true);
		String atf  = FilenameUtils.normalize(atfFile.getAbsolutePath(), true);

		return atf.substring(base.length() + 1);
	}

}
