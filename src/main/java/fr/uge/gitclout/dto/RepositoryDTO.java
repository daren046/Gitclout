package fr.uge.gitclout.dto;

import fr.uge.gitclout.database.Contributor;
import fr.uge.gitclout.database.Tag;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.MappedEntity;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import static fr.uge.gitclout.dto.ContributorDTO.*;
import static fr.uge.gitclout.dto.TagDTO.*;

/**
 * This class represents a repository that will be sent to the front.
 * @author Tagnan Tremellat
 * @version 1.0
 */
@Introspected
@MappedEntity("repository")
public class RepositoryDTO {
  private final Long id;
  private final String name;
  private final String url;
  private final List<TagDTO> tags;
  private final Set<ContributorDTO> contributors;


  public RepositoryDTO(Long id, String name, String url, List<Tag> tags, Set<Contributor> contributors) {
    Objects.requireNonNull(tags);
    Objects.requireNonNull(contributors);
    if(name == null){
      throw new IllegalArgumentException("name is null");
    }
    this.id = id;
    this.name = name;
    this.url = url;
    this.tags = convertToTagDTO(tags);
    this.contributors = convertToContributorDTO(contributors);
  }
  

  public Long getId() {
    return id;
  }


  public String getName() {
    return name;
  }


  public String getUrl() {
    return url;
  }


  public List<TagDTO> getTags() {
    return tags;
  }


  public Set<ContributorDTO> getContributors() {
    return contributors;
  }

  
  @Override
  public String toString() {
    return "RepositoryDTO{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", url='" + url + '\'' +
            ", tags=" + tags +
            ", contributors=" + contributors +
            '}';
  }
}