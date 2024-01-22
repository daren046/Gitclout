package fr.uge.gitclout.api;

import fr.uge.gitclout.database.Tag;
import fr.uge.gitclout.dto.ContributionDTO;
import fr.uge.gitclout.dto.RepositoryDTO;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * Controller class for handling Git related requests.
 * @author Tagnan Tremellat
 * @version 1.0
 */
@Controller()
public class Controllers {
  private final GitService gitService;
  
  public Controllers(GitService gitService) {
    this.gitService = gitService;
  }
  
  /**
   * Verifies a git link and returns a response.
   * @param requestBody The git link to be verified.
   * @return HttpResponse with the processed repository.
   */
  @Operation(summary = "Verify a git link",
          description = "Verifies a git link and returns a response.")
  @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(type = "string")))
  @Post(uri = "/postLink", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
  public HttpResponse<RepositoryDTO> processRepository(@Body String requestBody) throws GitAPIException, IOException {
    return gitService.processRepository(requestBody);
  }
  
  
  /**
   * Finds all repositories.
   *
   * @return List of all repositories.
   */
  @Operation(summary = "Find all repositories",
          description = "Finds all repositories and returns a list of them.")
  @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(type = "array", implementation = RepositoryDTO.class)))
  @Get("/allRepository")
  public List<RepositoryDTO> findAll() {
    return gitService.getAllRepository();
  }


  /**
   * Retrieves a repository by its ID.
   *
   * @param id The ID of the repository.
   * @return Optional containing the repository if found.
   */
  @Operation(summary = "Retrieve a repository by ID",
          description = "Retrieves a repository by its ID.")
  @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(type = "object", implementation = RepositoryDTO.class)))
  @Post(uri = "/postId", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
  public Optional<RepositoryDTO> getRepositoryById(@Body long id) {
    return gitService.getRepositoryById(id);
  }


  /**
   * Processes contributor data by tag.
   *
   * @param tag The tag to process contributions for.
   * @return List of contribution data.
   */
  @Operation(summary = "Process contributor data by tag",
          description = "Processes contributor data for a specific tag and returns a list of contributions .")
  @ApiResponse(content = @Content(mediaType = MediaType.TEXT_PLAIN, schema = @Schema(type = "array", implementation = ContributionDTO.class)))
  @Post(uri = "/postContrib", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
  public List<ContributionDTO> processContributor(@Body Tag tag) {
    Objects.requireNonNull(tag);
    return gitService.getContributionsByTagId(tag);
  }


  /**
   * Deletes a repository.
   *
   * @param repository The repository to delete.
   */
  @Operation(summary = "Delete a repository",
          description = "Deletes a repository based on the provided information only need a link.")
  @ApiResponse(content = @Content(mediaType = MediaType.APPLICATION_JSON, schema = @Schema(type = "repository")))
  @Post(uri = "/deleteRepo", consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
  public HttpResponse<String> deleteRepository(@Body String repository) {
    Objects.requireNonNull(repository);
    return gitService.deleteRepository(repository);
  }
}

