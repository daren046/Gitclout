package fr.uge.gitclout.gitanalyse;

import fr.uge.gitclout.database.Contributor;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * This class is used to manage the contributors of a repository.
 * @author Tagnan Tremellat
 * @version 1.0
 */
public class ContributorManagementService {
  
  public ContributorManagementService() {
  }
  
  /**
   * Deletes contributors that are no longer present.
   *
   * @param oldContributors The set of old contributors.
   * @param contributors    The set of current contributors.
   * @return A set of deleted contributors.
   */
  public Set<Contributor> deleteContributors(Set<Contributor> oldContributors, Set<Contributor> contributors) {
    Objects.requireNonNull(oldContributors);
    Objects.requireNonNull(contributors);
  Set<Contributor> deletedContributors = new HashSet<>(oldContributors);
  deletedContributors.removeAll(contributors);
  return deletedContributors;
  }
}
