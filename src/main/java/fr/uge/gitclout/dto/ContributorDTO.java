package fr.uge.gitclout.dto;

import fr.uge.gitclout.database.Contributor;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.MappedEntity;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * This class represents a contributor that will be sent to the front.
 * @author Tagnan Tremellat
 * @version 1.0
 */
@Introspected
@MappedEntity("contributor")
public class ContributorDTO {
  private final Long id;
  private final String username;

  public ContributorDTO(String username, Long id) {
    this.id = id;
    this.username = username;
  }
  
  
  /**
   * This method converts a set of contributors to a set of contributorDTOs.
   * @param contributors list of contributors
   * @return a set of contributorDTOs
   */
  public static Set<ContributorDTO> convertToContributorDTO(Set<Contributor> contributors) {
    Objects.requireNonNull(contributors);
    Set<ContributorDTO> contributorDTOs = new HashSet<>();
    ContributorDTO contributorDTO ;
    for (Contributor contributor : contributors) {
      contributorDTO = new ContributorDTO(contributor.getUsername(),contributor.getId());
      contributorDTOs.add(contributorDTO);
    }
    return contributorDTOs;
  }


  /**
   * This method converts a contributor to a contributorDTO.
   * @param contributor a contributor
   * @return a contributorDTO
   */
  public static ContributorDTO convertTocontributorDTO(Contributor contributor){
    Objects.requireNonNull(contributor);
    return new ContributorDTO(contributor.getUsername(), contributor.getId());
  }


  public Long getId() {
   return id;
  }


  public String getUsername() {
    return username;
  }


  @Override
  public String toString() {
    System.out.println("ContributorDTO");
    return "id=" + id +
           ", username='" + username + '\'' +
           ", languageMap=" +
           '}';
  }
}