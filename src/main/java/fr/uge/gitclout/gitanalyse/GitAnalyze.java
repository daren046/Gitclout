package fr.uge.gitclout.gitanalyse;

import fr.uge.gitclout.database.*;
import fr.uge.gitclout.gitcloutexeption.AnalyzeException;
import jakarta.inject.Inject;
import org.eclipse.jgit.api.errors.GitAPIException;
import fr.uge.gitclout.database.Repository;
import java.io.IOException;
import java.util.*;

/**
 *  GitAnalyze class for handling Git repository analyses.
 *  It provides functionalities for cloning, analyzing, and deleting repositories,
 *  as well as extracting and saving relevant information into a database.
 * @author Tagnan Tremellat
 * @version 1.0
 * GitAnalyze class for handling the repository's analyzes.
 */

public class GitAnalyze {
  
  private final ContributionAnalyzer contributionAnalyzer;
  private final GitAnalysisService gitAnalysisService;
  private final TagManagementService tagManagementService;
  private final ContributorManagementService contributorManagementService;
  
  /**
   * Constructor for GitAnalyze.
   * Initializes a new GitAnalyze instance with the specified Git repository and database manager.
   *
   */
  @Inject
  public GitAnalyze(ContributionAnalyzer contributionAnalyzer, GitAnalysisService gitAnalysisService , TagManagementService tagManagementService , ContributorManagementService contributorManagementService) {
    this.contributionAnalyzer = contributionAnalyzer;
    this.gitAnalysisService = gitAnalysisService;
    this.tagManagementService = tagManagementService;
    this.contributorManagementService = contributorManagementService;
  }


  /**
   * Analyzes a Git repository and saves its information to the database.
   * Clones the repository, retrieves necessary information, then deletes the cloned repository.
   *
   * @return An optional GitAnalysisResult if the analysis was successful, empty otherwise.
   * @throws AnalyzeException if an error occurs during the analysis.
   */
  public Optional<GitAnalysisResult> analyzeRepository() throws AnalyzeException {
    try {
      var gitAnalysisResult = repositoryToDataBase();
      return Optional.ofNullable(gitAnalysisResult);
    } catch (Exception e) {
      throw new AnalyzeException("Failed to analyze repository", e);
    }
  }

  
  /**
   * Saves the repository's information to the database and analyzes contributions for tags.
   * It retrieves tags and commits from the repository and analyzes contributions for each tag.
   *
   * @return A GitAnalysisResult containing repository information and contributions.
   * @throws AnalyzeException if an error occurs during the process.
   */
  public GitAnalysisResult repositoryToDataBase() throws AnalyzeException {
    try {
      List<Tag> tags = gitAnalysisService.ListTags();
      var repository = gitAnalysisService.createAndSaveRepository();
      var contributors = gitAnalysisService.getContributors();
      var contribution = contributionAnalyzer.analyzeContributionsForTags(tags);
      return new GitAnalysisResult(repository, contributors, tags, contribution);
    } catch (Exception e) {
      throw new AnalyzeException("Failed to save repository to the database", e);
    }
  }
  
 
  /**
   * Updates an existing repository's information and contributions.
   * It checks for changes in tags, contributors, and contributions and updates the database accordingly.
   *
   * @param repository The existing repository to be updated.
   * @return An optional GitAnalysisResult if the update was successful, empty otherwise.
   * @throws GitAPIException if an error occurs during Git operations.
   * @throws IOException if an error occurs during file operations.
   * @throws AnalyzeException if an error occurs during the analysis.
   */
  public Optional<GitAnalysisResult> updateRepository(Repository repository) throws GitAPIException, IOException, AnalyzeException {
    Objects.requireNonNull(repository);
      Set<Contributor> newContributors = gitAnalysisService.getContributors();
      List<Contribution> contributions = new ArrayList<>();
      List<Tag> updateTags = gitAnalysisService.ListTags();
      var newTags = tagManagementService.updateTags(repository.getTags(), updateTags);
      if (newTags == null) { return Optional.empty(); }
      var deletedTag = tagManagementService.deleteTag(repository.getTags(), updateTags);
        repository.getTags().removeAll(deletedTag);
      var deletedContributor = contributorManagementService.deleteContributors(repository.getContributors() ,newContributors);
      contributions = updateRepositoryAux(newTags, repository.getTags() , contributions);
    return Optional.of(new GitAnalysisResult(repository, gitAnalysisService.getContributors(),
            updateTags, contributions , deletedTag, deletedContributor));
  }
  
  /**
   * Updates the repository's information and contributions.
   * @param newTags different tags between the old and new repository.
   * @param oldTags old tags of the repository.
   * @param contributions contributions of the repository.
   * @return a list of contributions.
   * @throws IOException if an error occurs during file operations.
   */
  private List<Contribution> updateRepositoryAux(List<Tag> newTags, List<Tag> oldTags , List<Contribution> contributions) throws IOException {
    if(!newTags.isEmpty()){
      var trulyNewTags = tagManagementService.getTrulyNewTagsWithLast(newTags, oldTags);
      contributions = contributionAnalyzer.updateContribution(trulyNewTags);
      tagManagementService.addNewTagsWithoutDuplicates(oldTags, newTags);
    }
    return contributions;
  }
}


