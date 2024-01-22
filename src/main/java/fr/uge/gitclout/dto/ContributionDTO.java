package fr.uge.gitclout.dto;

import fr.uge.gitclout.database.Contributor;
import fr.uge.gitclout.gitanalyse.LanguageName;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.MappedEntity;
import java.util.Map;
import static fr.uge.gitclout.dto.ContributorDTO.convertTocontributorDTO;

/**
  * This class represents a contribution that will be sent to the front.
  * @author Tagnan Tremellat
  * @version 1.0
 */
@Introspected
@MappedEntity("Contributions")
public class ContributionDTO {
  private final Long id;
  private final ContributorDTO contributor;
  private final Map<LanguageName, Integer> languageMap;


  public ContributionDTO(Long id, Contributor contributor,Map<LanguageName, Integer> languageMap) {
    this.id = id;
    this.contributor = convertTocontributorDTO(contributor);
    this.languageMap = languageMap;
  }

  
  public Long getId() {
    return id;
  }


  public Map<LanguageName, Integer> getLanguageMap() {
    return languageMap;
  }

  
  public ContributorDTO getContributor() {
    return contributor;
  }


  public String toString() {
    return "ContributionDTO{" +
        "id=" + id +
        ", contributor=" + contributor +
        ", languageMap=" + languageMap.toString() +
        '}';
    
  }
}


