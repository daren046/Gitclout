package fr.uge.gitclout.gitanalyse;

import org.eclipse.jgit.transport.CredentialsProvider;
import fr.uge.gitclout.gitcloutexeption.CloneRepositoryException;
import fr.uge.gitclout.gitcloutexeption.DeleteRepositoryException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.Objects;


/**
  * This class is used to clone a repository from a remote repository.
  * @author Tagnan Tremellat
  * @version 1.0
  */
public class GitCloneManager {
  
  private CredentialsProvider credentialsProvider;
  private final GitRepository gitRepository;
  
  public GitCloneManager(GitRepository gitRepository) {
    this.gitRepository = gitRepository;
  }
  
  /**
   * Clones the repository into the given directory.
   *
   * @param directory the path to the directory.
   * @return a Git object of the cloned repository.
   * @throws CloneRepositoryException if a Git API error occurs.
   */
  public Git cloneToDirectory(Path directory) throws CloneRepositoryException {
    Objects.requireNonNull(directory);
    try {
      return Git.cloneRepository()
              .setURI(gitRepository.remoteRepoUri())
              .setDirectory(directory.toFile())
              .setCredentialsProvider(credentialsProvider)
              .setBare(true)
              .call();
    } catch (GitAPIException e) {
      return null;
    }
  }
  
  
  /**
   * Creates a temporary directory to clone the repository into.
   *
   * @return the path to the temporary directory.
   * @throws CloneRepositoryException if an error occurs during creation.
   */
  public Path createTempDirectory() throws CloneRepositoryException {
    try {
      return Files.createTempDirectory("git_clone_");
    } catch (IOException e) {
      throw new CloneRepositoryException("Failed to clone repository", e);
    }
  }
  
}