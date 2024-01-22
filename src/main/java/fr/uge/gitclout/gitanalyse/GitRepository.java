package fr.uge.gitclout.gitanalyse;
import java.util.Objects;

/**
 * This class represents a git repository.
 * @param remoteRepoUri the uri of the remote repository.
 */
public record GitRepository(String remoteRepoUri) {
  public GitRepository{
    Objects.requireNonNull(remoteRepoUri);
  }
}