package fr.uge.gitclout.api;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import fr.uge.gitclout.database.Contribution;
import fr.uge.gitclout.database.Contributor;
import fr.uge.gitclout.database.Repository;
import fr.uge.gitclout.database.Tag;
import fr.uge.gitclout.dto.ContributionDTO;
import fr.uge.gitclout.dto.RepositoryDTO;
import fr.uge.gitclout.gitanalyse.*;
import fr.uge.gitclout.gitcloutexeption.AnalyzeException;
import fr.uge.gitclout.database.DataBaseService;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.annotation.Body;
import io.micronaut.transaction.annotation.Transactional;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * GitService class for handling Git repository operations.
 * Provides functionality to fetch data from external sources, process repositories,
 * and retrieve repository information from a database.
 * @author Tagnan Tremellat
 * @version 1.0
 */
@Singleton
public class GitService  {
  
  @Inject
  DataBaseService dataBaseService;
  @Inject
  ProgressWebSocket progressWebSocket;
  private final DatabaseManager databaseManager;
  public GitService(DatabaseManager databaseManager) {
    this.databaseManager = databaseManager;
  }
  

  /**
   * Auxiliary method to process a repository based on its URL.
   * @param repository The repository to be processed.
   * @return HttpResponse containing the RepositoryDTO.
   */
  public HttpResponse<RepositoryDTO> processRepositoryAux(Repository repository) {
    Objects.requireNonNull(repository);
    RepositoryDTO repositoryDTO = new RepositoryDTO(repository.getId(), repository.getName(), repository.getUrl(), repository.getTags(), repository.getContributors());
    return HttpResponse.ok(repositoryDTO);
  }


  /**
   * Processes a repository based on a request body.
   * @param requestBody The request body containing repository information.
   * @return HttpResponse containing the RepositoryDTO if successful, server error or not found otherwise.
   */
  public HttpResponse<RepositoryDTO> processRepository(@Body String requestBody) throws IOException, GitAPIException {
    String url = extractUrlFromRequestBody(requestBody);
    var repo = new GitRepository(url);
    var gitCloneManager = new GitCloneManager(repo);
    var path = gitCloneManager.createTempDirectory();
    var git = gitCloneManager.cloneToDirectory(path);
    if (git == null) {
      return HttpResponse.serverError();
    }
    return processRepositoryAux(repo, git, gitCloneManager, url);
  }


  /**
   * Auxiliary method to process a repository based on its URL.
   * @param repo The repository to be processed.
   * @param git The Git object.
   * @param gitCloneManager The GitCloneManager object.
   * @param url The URL of the repository.
   * @return HttpResponse containing the RepositoryDTO if successful, server error or not found otherwise.
   * @throws GitAPIException api exception
   * @throws AnalyzeException analyze exception
   * @throws IOException io exception
   */
  private HttpResponse<RepositoryDTO> processRepositoryAux(GitRepository repo, Git git, GitCloneManager gitCloneManager, String url) throws GitAPIException, AnalyzeException, IOException {
    var gitAnalysisService = new GitAnalysisService(repo, git, gitCloneManager);
    var ContributionAnalyzer = new ContributionAnalyzer(progressWebSocket,git,gitAnalysisService);
    GitAnalyze gitAnalyze = new GitAnalyze(ContributionAnalyzer, gitAnalysisService , new TagManagementService(), new ContributorManagementService());
    Optional<Repository> existingRepo = databaseManager.findRepoByUrl(url);
    if (existingRepo.isPresent()) {
      return handleExistingRepository(existingRepo.get(), gitAnalyze,gitAnalysisService);
    } else {
      return analyzeAndSaveNewRepository(gitAnalyze);
    }
  }


  /**
   * Extracts the URL from a request body.
   * @param requestBody The request body containing repository information.
   * @return The URL.
   * @throws JsonProcessingException json processing exception
   */
  private String extractUrlFromRequestBody(String requestBody) throws JsonProcessingException {
    ObjectMapper localMapper = new ObjectMapper();
    return localMapper.readTree(requestBody).get("url").asText();
  }
  
  /**
   * Handles an existing repository for the analysis.
   * @param existingRepo The repository to be used.
   * @param gitAnalyze The GitAnalyze object.
   * @param gitAnalysisService The GitAnalysisService object.
   * @return HttpResponse containing the RepositoryDTO if successful, server error or not found otherwise.
   * @throws AnalyzeException analyze exception
   * @throws GitAPIException api exception
   * @throws IOException io exception
   */
  private HttpResponse<RepositoryDTO> handleExistingRepository(Repository existingRepo, GitAnalyze gitAnalyze , GitAnalysisService gitAnalysisService) throws AnalyzeException, GitAPIException, IOException {

    var path = gitAnalysisService.cloneRepository();
    if (path == null) {return HttpResponse.serverError();}
    var updater = gitAnalyze.updateRepository(existingRepo);
    if (updater.isPresent()) {
      databaseManager.updateRepo(updater);
      return processRepositoryAux(updater.get().getRepository());
    }
    return processRepositoryAux(existingRepo);
  }


  /**
   * Analyses and saves the new repository.
   * @param gitAnalyze The GitAnalyze object.
   * @return HttpResponse containing the RepositoryDTO if successful, server error or not found otherwise.
   * @throws AnalyzeException analyze exception
   */
  private HttpResponse<RepositoryDTO> analyzeAndSaveNewRepository(GitAnalyze gitAnalyze) throws AnalyzeException {
    Objects.requireNonNull(gitAnalyze);
    var gitAnalysisResult = gitAnalyze.analyzeRepository();
    if (gitAnalysisResult.isPresent()) {
      databaseManager.saveRepo(gitAnalysisResult);
      return processRepositoryAux(gitAnalysisResult.get().getRepository());
    }
    return HttpResponse.notFound();
  }


  /**
   * Retrieves all repositories.
   * @return A list of RepositoryDTO representing all repositories.
   */
  @Transactional
  public List<RepositoryDTO> getAllRepository() {
    var repositoryList = dataBaseService.findAll();
    List<RepositoryDTO> repositoryDTOList = new ArrayList<>();
    for (Repository repository : repositoryList) {
      repositoryDTOList.add(new RepositoryDTO(repository.getId(), repository.getName(), repository.getUrl(), repository.getTags(), repository.getContributors()));
    }
    return repositoryDTOList;
  }


  /**
   * Retrieves all contributions for a given tag.
   * @param tag The tag to retrieve contributions for.
   * @return A list of ContributionDTO representing all contributions for the given tag.
   */
  @Transactional
  public List<ContributionDTO> getContributionsByTagId(Tag tag) {
    Objects.requireNonNull(tag);
    var tag2 = dataBaseService.findBySha1(tag.getSha1());
    Optional<List<Contribution>> contributions = dataBaseService.getContributionPerTag(tag2);
    List<ContributionDTO> contributionDTOList = new ArrayList<>();
    if (contributions.isEmpty()) {return contributionDTOList;}
    for (Contribution contribution : contributions.get()) {
      var contributor = new Contributor(contribution.getContributor().getId(), contribution.getContributor().getUsername(), contribution.getContributor().getEmail());
      contributionDTOList.add(new ContributionDTO(contribution.getId(), contributor, contribution.getLanguageMap()));
    }
    return contributionDTOList;
  }


  /**
   * Retrieves a repository by its ID.
   * @param id The unique identifier of the repository.
   * @return An Optional containing the RepositoryDTO if found, or an empty Optional otherwise.
   */
  @Transactional
  public Optional<RepositoryDTO> getRepositoryById(long id) {
    Objects.requireNonNull(id);
    var repository = dataBaseService.getRepositoryById(id);
    if (repository.isPresent()) {
      RepositoryDTO repositoryDTO = new RepositoryDTO(repository.get().getId(), repository.get().getName(),
              repository.get().getUrl(), repository.get().getTags(), repository.get().getContributors());
      return Optional.of(repositoryDTO);
    }
    return Optional.empty();
  }


  /**
   * Deletes a repository.
   * @param linkRepository it's 
   * @return HttpResponse containing a message if successful, not found otherwise.
   */
  public HttpResponse<String> deleteRepository(String linkRepository) {
    var repository = databaseManager.findRepoByUrl(linkRepository);
    if (repository.isPresent()) {
      databaseManager.deleteRepository(repository.get());
      return HttpResponse.ok("Repository deleted");
    }
    return HttpResponse.notFound();
  }
}
