package fr.uge.gitclout.gitanalyse;

import fr.uge.gitclout.database.Contribution;
import fr.uge.gitclout.database.Contributor;
import fr.uge.gitclout.database.Repository;
import fr.uge.gitclout.database.Tag;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * this class represents the result of the analysis of a git repository.
 * It contains the list of contributors, tags and contributions.
 * @author Tagnan Tremellat
 * @version 1.0
 */
public class GitAnalysisResult {
  
  private final Repository repository;
  private Set<Contributor> contributors;
  private List<Tag> tags;
  private List<Contribution> contributions;
  private List<Tag> deletedTags;
  private Set<Contributor> deletedContributors;
  
  
  public GitAnalysisResult(Repository repository, Set<Contributor> contributors, List<Tag> tags, List<Contribution> contributions) {
    this.contributors = contributors;
    this.tags = tags;
    this.contributions = contributions;
    this.repository = repository;
  }
  
  
  public GitAnalysisResult(Repository repository, Set<Contributor> contributors, List<Tag> tags,
                           List<Contribution> contributions, List<Tag> deletedTags, Set<Contributor> deletedContributors) {
    this.contributors = contributors;
    this.tags = tags;
    this.contributions = contributions;
    this.repository = repository;
    this.deletedTags = deletedTags;
    this.deletedContributors = deletedContributors;
  }
  
  
  public Set<Contributor> getContributors() {
    Objects.requireNonNull(contributors);
    return contributors;
  }
  
  
  public List<Tag> getTags() {
    Objects.requireNonNull(tags);
    return tags;
  }
  
  
  public List<Contribution> getContributions() {
    Objects.requireNonNull(contributions);
    return contributions;
  }
  
  
  public void setContributors(Set<Contributor> contributors) {
    Objects.requireNonNull(contributors);
    this.contributors = contributors;
  }
  
  
  public void setTags(List<Tag> tags) {
    Objects.requireNonNull(tags);
    this.tags = tags;
  }
  
  
  public void setContributions(List<Contribution> contributions) {
    Objects.requireNonNull(contributions);
    this.contributions = contributions;
  }
  
  
  public Repository getRepository() {
    
    return repository;
  }
  
  
  public List<Tag> getDeletedTags() {
    return deletedTags;
  }
  
  
  public Set<Contributor> getDeletedContributors() {
    return deletedContributors;
  }
  
}
