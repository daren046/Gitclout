package fr.uge.gitclout.gitanalyse;

import fr.uge.gitclout.database.Contributor;
import fr.uge.gitclout.database.Repository;
import fr.uge.gitclout.database.Tag;
import fr.uge.gitclout.gitcloutexeption.AnalyzeException;
import fr.uge.gitclout.gitcloutexeption.CloneRepositoryException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * GitAnalysisService class for handling Git repository analyses.
 * @author Tagnan Tremellat
 */
public class GitAnalysisService {
  
  private final GitRepository gitRepository;
  private final GitCloneManager gitCloneManager;
  private Git git;
  
  public GitAnalysisService(GitRepository gitRepository, Git git, GitCloneManager gitCloneManager) {
    this.gitRepository = gitRepository;
    this.git = git;
    this.gitCloneManager = gitCloneManager;
  }
  /**
   * Clones the repository into a temporary directory and performs analysis.
   * This includes saving the repository to the database and deleting the temporary clone.
   *
   * @return The path to the cloned repository.
   * @throws CloneRepositoryException if an error occurs during cloning.
   */


  public Path cloneRepository() throws CloneRepositoryException {
    Path tempDir = gitCloneManager.createTempDirectory();
    try (Git git = gitCloneManager.cloneToDirectory(tempDir)) {
      this.git = git;
      return tempDir;
    }catch (IOException e) {
      throw new CloneRepositoryException("Failed to clone repository", e);
    }
  }


  /**
   * Retrieves the contributors of the repository.
   * Each contributor is identified by their name in the commit history.
   *
   * @return A Set of Contributors representing all unique contributors to the repository.
   * @throws AnalyzeException if an error occurs during the retrieval process.
   */
  public Set<Contributor> getContributors() throws IOException, GitAPIException {
      Set<Contributor> contributors = new HashSet<>();
      for (RevCommit commit : git.log().all().call()) {
        PersonIdent authorIdent = commit.getAuthorIdent();
        String authorEmail = authorIdent.getEmailAddress();
        String authorName = authorIdent.getName();
        if (authorName != null) {
          contributors.add(new Contributor(authorName , authorEmail));
        }
      }
      return contributors;
  }


  /**
   * Retrieves the tags of the repository.
   * Tags are used for categorizing and referencing specific points in history.
   *
   * @return A List of Tags associated with the repository.
   * @throws AnalyzeException if an error occurs during tag retrieval.
   */

  public List<Tag> ListTags() throws AnalyzeException {
    try {
      var tags = git.tagList().call();
      return tags.stream().map(tag -> new Tag(tag.getName().substring(tag.getName().lastIndexOf("/") + 1),tag.getObjectId().abbreviate(7).name())).collect(Collectors.toList());
    }catch (GitAPIException e) {
      throw new AnalyzeException("Failed to perform analysis", e);
    }
  }

  
  /**
   * Creates and saves a Repository object based on the current gitRepository.
   * This involves extracting the repository's URL and contributors, and then saving this data.
   * @return The created Repository object.
   */
  Repository createAndSaveRepository() {
    var url = gitRepository.remoteRepoUri();
    var name = url.substring(url.lastIndexOf('/') + 1);
    return new Repository(name, url);
  }


  /**
   * Resolves a Git tag to its corresponding commit object.
   * This method takes the name of a Git tag and returns the associated commit,
   * allowing for the examination of the details of the commit.
   *
   * @param tagName The name of the Git tag to be resolved to a commit.
   * @return RevCommit The commit object associated with the given tag.
   * @throws IOException If the tag cannot be found or there is an issue accessing the repository.
   */
  RevCommit resolveTagToCommit(String tagName) throws IOException {
    try (RevWalk walk = new RevWalk(git.getRepository())) {
      Ref tagRef = git.getRepository().findRef(tagName);
      if (tagRef != null) {
        var tagObjectId = tagRef.getObjectId();
        if (tagObjectId != null) {
          return walk.parseCommit(tagObjectId);
        }
      }
      throw new IOException("tag not found" + tagName);
    }
  }

  
  /**
   * Retrieves the differences between an empty tree and a commit.
   *
   * @param commit The current commit.
   * @return A List of DiffEntry objects representing changes between git tree and one commits.
   * @throws IOException if an error occurs during the diff operation.
   */
  List<DiffEntry> getDiffBetweenEmptyTreeAndCommit(RevCommit commit) throws IOException {
    try(ObjectReader reader = git.getRepository().newObjectReader()){
      CanonicalTreeParser emptyTreeParser = emptyTreeParser();
      CanonicalTreeParser commitTreeParser = new CanonicalTreeParser();
      commitTreeParser.reset(reader, commit.getTree());
      DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE);
      diffFormatter.setRepository(git.getRepository());
      return diffFormatter.scan(emptyTreeParser, commitTreeParser);
    }
  }
  
  
  /**
   * Retrieves the differences between two commits.
   * This is used for analyzing changes from one commit to its parent.
   *
   * @param parent The parent commit.
   * @param commit The current commit.
   * @return A List of DiffEntry objects representing changes between the two commits.
   */
  List<DiffEntry> getDiffBetweenCommits(RevCommit parent, RevCommit commit) throws IOException {
    try{
      DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE);
      CanonicalTreeParser parentTreeParser = new CanonicalTreeParser();
      CanonicalTreeParser commitTreeParser = new CanonicalTreeParser();
      parentTreeParser.reset(git.getRepository().newObjectReader(), parent.getTree());
      commitTreeParser.reset(git.getRepository().newObjectReader(), commit.getTree());
      diffFormatter.setRepository(git.getRepository());
      return diffFormatter.scan(parentTreeParser, commitTreeParser);
    }catch (IOException e) {
      throw new IOException("Failed to get diff between commits", e);
    }
  }
  
  
  /**
   * Creates and returns an empty tree parser.
   *
   * @return A CanonicalTreeParser initialized as an empty tree.
   */
  private CanonicalTreeParser emptyTreeParser() {
    CanonicalTreeParser treeParser = new CanonicalTreeParser();
    treeParser.reset();
    return treeParser;
  }
}
