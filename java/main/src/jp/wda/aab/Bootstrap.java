package jp.wda.aab;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FilenameUtils;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.TreeWalk;

import com.gitblit.models.PathModel.PathChangeModel;
import com.gitblit.utils.JGitUtils;

/**
 *
 *
 *
 * @author		amnz
 */
public class Bootstrap {

	// アプリケーションエントリーポイント ///////////////////////////////////////////////
	//                                                         Application Entry Point //
	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		if(args == null) { args = new String[0]; }

		String properties;
		if(args.length < 1) {
			properties = FilenameUtils.normalize(new File(AABConstants.DEFAULT_PROPERTIES_FILE).getAbsolutePath());
			System.out.println("use default properties." + properties);
		} else {
			properties = FilenameUtils.normalize(new File(args[0]).getAbsolutePath());
			System.out.println("properties." + properties);
		}

		new Bootstrap(properties);
	}

	// コンストラクタ ///////////////////////////////////////////////////////////////////
	//                                                                    Constructors //
	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 */
	public Bootstrap(String properties) {
		super();

		systemProperties = new Properties();
		File propertiesFile = new File(properties);
		try(FileReader reader = new FileReader(propertiesFile)) {
			systemProperties.load(reader);
		} catch(IOException ex) {
			System.out.println("Properties file is not found. " + propertiesFile);
			System.exit(-1);
			return;
		}

		String repositoryDir = systemProperties.getProperty(AABConstants.PROPERTIES_REPOSITORY_LOCSTION);
		if(repositoryDir == null || repositoryDir.length() == 0) {
			System.out.println("use current directory repository.");
			repositoryDir = FilenameUtils.normalize(new File("").getAbsolutePath());
		} else {
			repositoryDir = FilenameUtils.normalize(new File(repositoryDir).getAbsolutePath());
		}
		targetDir = new File(FilenameUtils.concat(repositoryDir, Constants.DOT_GIT)).getAbsoluteFile();

		initFileTypes();
		if(fileTypes.length == 0) { return; }

		try {
			initRepository();
			searchFiles();
		} catch(IOException ex) {
			System.out.println("directory:" + targetDir);
			System.out.println("           is not a valid repository");
			System.exit(-1);
			return;
		}

		if(threads.size() == 0) { return; }

		execute();
	}

	// 内部フィールド定義 ///////////////////////////////////////////////////////////////
	//                                                                          Fields //
	/////////////////////////////////////////////////////////////////////////////////////

	private Properties systemProperties;

	private String[] fileTypes;

	private File targetDir;

	private Repository repository;

	private ObjectId head;

	private List<CommandThread> threads = new ArrayList<CommandThread>();

	// 内部メソッド /////////////////////////////////////////////////////////////////////
	//                                                                 Private Methods //
	/////////////////////////////////////////////////////////////////////////////////////

	/**
	 *
	 */
	private void initFileTypes() {
		String fileTypesDef = systemProperties.getProperty(AABConstants.PROPERTIES_FILE_TYPES);
		if(fileTypesDef == null) { fileTypes = new String[0]; return; }

		fileTypes = fileTypesDef.split(",");
		for(int i = 0; i < fileTypes.length; i++) { fileTypes[i] = fileTypes[i].trim(); }
	}

	/**
	 *
	 */
	private void initRepository() throws IOException {
		FileRepositoryBuilder builder = new FileRepositoryBuilder();
		this.repository = builder.setGitDir(targetDir)
				.readEnvironment()
				.findGitDir()
				.build();

		if(!repository.getRepositoryState().canCommit()){ throw new IOException(); }

		head = repository.resolve(Constants.HEAD);
		if(head == null) { throw new IOException(); }
	}

	/**
	 *
	 * @throws IOException
	 */
	private void searchFiles() throws IOException {
		String flag;
		try {
			flag = systemProperties.getProperty(AABConstants.PROPERTIES_SEARCH_TYPE);
		} catch(Throwable ex) {
			flag = AABConstants.SEARCH_TYPE_ALL;
		}

		if(AABConstants.SEARCH_TYPE_ALL.equalsIgnoreCase(flag)) {
			searchFromAllFiles();
		} else {
			searchFromCommitedFiles();
		}

	}

	/**
	 *
	 * @throws IOException
	 */
	private  void searchFromCommitedFiles() throws IOException {
		RevWalk walk = new RevWalk(repository);
		walk.setRetainBody(false);

		List<PathChangeModel> files = JGitUtils.getFilesInCommit(repository, walk.parseCommit(head));
		for(PathChangeModel f : files) { checkFile(f.path); }
	}

	/**
	 *
	 * @throws IOException
	 */
	private  void searchFromAllFiles() throws IOException {
		RevWalk walk = new RevWalk(repository);
		walk.setRetainBody(false);

		TreeWalk treeWalk = new TreeWalk(repository);
		treeWalk.addTree(walk.parseTree(head));
		treeWalk.setRecursive(true);

		while (treeWalk.next()) { checkFile(treeWalk.getPathString()); }
	}

	/**
	 *
	 * @param path
	 */
	private void checkFile(String path) {
		if(!FilenameUtils.isExtension(path, fileTypes)) { return; }

		CommandThread t = new CommandThread(systemProperties, repository.getDirectory().getParentFile(), path);
		if(!t.isTarget) { return; }

		threads.add(t);
	}


	/**
	 *
	 */
	private void execute() {
		for(Thread t : threads) { t.start(); }
		for(Thread t : threads) {
			try {
				t.join();
			} catch(InterruptedException ex) {
				ex.printStackTrace();
			}
		}

		Git git = Git.wrap(repository);
		AddCommand add = git.add();
		CommitCommand commit = git.commit();

		List<String> generatedFiles = new ArrayList<String>();
		for(CommandThread t : threads) {
			String file = t.getGeneratedFile();
			if(file == null) { continue; }

			generatedFiles.add(file);
			commit.setOnly(file);
			if(!t.atfExists) { add.addFilepattern(file); }
			System.out.println("generated : " + file);
		}


		try {
			add.call();

			commit.setMessage(systemProperties.getProperty(AABConstants.PROPERTIES_COMMIT_MESSAGE, AABConstants.DEFAULT_COMMIT_MESSAGE));
			commit.call();

			PushCommand push = git.push();
			push.call();
		} catch(Throwable ex) {
			ex.printStackTrace();
			System.exit(-1);
			return;
		}
	}

}
