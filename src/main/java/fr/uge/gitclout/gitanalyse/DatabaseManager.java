package fr.uge.gitclout.gitanalyse;

import fr.uge.gitclout.database.Contribution;
import fr.uge.gitclout.database.Contributor;
import fr.uge.gitclout.database.Repository;
import fr.uge.gitclout.database.Tag;
import fr.uge.gitclout.database.DataBaseService;
import jakarta.inject.Singleton;
import jakarta.transaction.Transactional;
import java.util.*;

/**
 * DatabaseManager class for managing all elements into the database.
 *  @author Tagnan Tremellat.
 * @version 1.0
 */
@Singleton
public class DatabaseManager {
  private final DataBaseService dataBaseService;

  public DatabaseManager(DataBaseService dataBaseService) {
    this.dataBaseService = dataBaseService;
  }


  /**
   * Saves contributors into the database.
   *
   * @param contributors a Set of contributors.
   * @return Set of contributors.
   */
  @Transactional
  public Set<Contributor> saveContributors(Set<Contributor> contributors) {
    Objects.requireNonNull(contributors);
    Set<Contributor> newContrib = new HashSet<>();
    for (Contributor contributor : contributors) {
      if (dataBaseService.existsByUsername(contributor.getUsername())) {
            newContrib.add(dataBaseService.findContributorByUsername(contributor.getUsername()));
        continue;
      }
      newContrib.add(dataBaseService.save(contributor));
    }
    return newContrib;
  }


  /**
   * Updates contributors into the database.
   * @param contributors a Set of contributors.
   */
  @Transactional
  public void updateContributors(Set<Contributor> contributors) {
    Objects.requireNonNull(contributors);
    for (Contributor contributor : contributors) {
      if (dataBaseService.existsByUsername(contributor.getUsername())) {
        continue;
      }
      dataBaseService.save(contributor);
    }
  }


  /**
   * Deletes contributors from the database.
   * @param contributors a Set of contributors.
   */
  @Transactional
  public void deleteContributor(Set<Contributor> contributors) {
    Objects.requireNonNull(contributors);
    for (Contributor contributor : contributors) {
      dataBaseService.delete(contributor);
    }
  }


  /**
  * Saves a contributor into the database.
  * @param contributor a contributor.
  * @return a contributor.
   */
  @Transactional
  public Contributor saveContributor(Contributor contributor) {
    Objects.requireNonNull(contributor);
    return dataBaseService.save(contributor);
  }


  /**
   * Saves tags into the database.
   * @param tags a List of tags.
   * @param repository a repository.
   */
  @Transactional
  public void saveTags(List<Tag> tags, Repository repository) {
    Objects.requireNonNull(tags);
    Objects.requireNonNull(repository);
    for (Tag tag : tags) {
      tag.setRepository(repository);
      dataBaseService.save(tag);
    }
  }

  
  /**
   * Updates tags into the database.
   * @param tags a List of tags.
   * @param repository the associated repository.
   */
  @Transactional
  public void updateTags(List<Tag> tags, Repository repository) {
    Objects.requireNonNull(tags);
    Objects.requireNonNull(repository);
    for (Tag tag : tags) {
      if(!dataBaseService.existsByNameTag(tag.getName())){
        tag.setRepository(repository);
        dataBaseService.save(tag);
      }
    }
  }
  

  /**
   * Saves a repository into the database.
   *
   * @param repository a repository.
   */
  @Transactional
  public void saveRepository(Repository repository) {
    Objects.requireNonNull(repository);
    dataBaseService.save(repository);
  }
  
  
  /**
   * Saves a contribution into the database.
   * @param contribution a contribution.
   */
  @Transactional
  public void saveContribution(Contribution contribution) {
    Objects.requireNonNull(contribution);
    dataBaseService.save(contribution);
  }


  /**
   * Saves a GitAnalysisResult into the database.
   * @param gitAnalysisResult a GitAnalysisResult.
   */
  @Transactional
  public void saveRepo(Optional<GitAnalysisResult> gitAnalysisResult) {
    Objects.requireNonNull(gitAnalysisResult);
    if (gitAnalysisResult.isEmpty()) {
      return;
    }
    var result = gitAnalysisResult.get();
    result.setContributors(saveContributors(result.getContributors()));
    result.getRepository().setContributors(result.getContributors());
    saveRepository(result.getRepository());
    result.getRepository().setTags(result.getTags());
    saveTags(result.getTags(), result.getRepository());
    saveContributions(result.getContributions());
  }


  /**
   * Updates a GitAnalysisResult into the database.
   * @param gitAnalysisUpdate a GitAnalysisResult.
   */
  @Transactional
  public void updateRepo(Optional<GitAnalysisResult> gitAnalysisUpdate){
    Objects.requireNonNull(gitAnalysisUpdate);
    if (gitAnalysisUpdate.isEmpty()){
      return;
    }
    var update = gitAnalysisUpdate.get();

    deleteContributor(update.getDeletedContributors());
    updateContributors(update.getContributors());
    update.getRepository().setContributors(update.getContributors());
    
    deleteTag(update.getDeletedTags());
    updateTags(update.getRepository().getTags(), update.getRepository());
    saveContributions(update.getContributions());
  }
  
  
  /**
   * Saves contributions into the database.
   * @param contributions a List of contributions.
   */
  @Transactional
  public void saveContributions(List<Contribution> contributions) {
    Objects.requireNonNull(contributions);
    for (Contribution contribution : contributions) {
      contribution.setContributor(saveContributor(contribution.getContributor()));
      contribution.setTag(dataBaseService.findTagByName(contribution.getTag().getName()));
      saveContribution(contribution);
    }

  }
  

  /**
   * Delete tags from the databaseOUI
   * @param tags a List of tags.
   */
  public void deleteTag(List<Tag> tags){
    Objects.requireNonNull(tags);
    for (Tag tag : tags) {
      dataBaseService.delete(tag);
    }
  }


  /**
   * Finds a repository by its url.
   *
   * @param url a url.
   * @return an optional of repository.
   */
  public Optional<Repository> findRepoByUrl(String url) {
    return dataBaseService.getRepositoryByUrl(url);
  }
  
  /**
   * Delete a repository from the database.
   *
   * @param repository repository
   */
  public void deleteRepository(Repository repository) {
    Objects.requireNonNull(repository);
    dataBaseService.delete(repository);
  }
}
  