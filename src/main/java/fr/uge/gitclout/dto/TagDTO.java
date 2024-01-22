package fr.uge.gitclout.dto;

import fr.uge.gitclout.database.Tag;
import io.micronaut.core.annotation.Introspected;
import io.micronaut.data.annotation.MappedEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * This class represents a tag that will be sent to the front.
 * @author Tagnan Tremellat
 * @version 1.0
 */
@Introspected
@MappedEntity("tag")
public class TagDTO {
  private Long id;
  private String name;
  private String sha1;

  public TagDTO( String name , Long id ,String sha1) {
    if (name == null || sha1 == null) throw new IllegalArgumentException('"' + name + '"' + " is null");
    this.name = name;
    this.id = id;
    this.sha1 = sha1;
  }

  
  /**
   * This method converts a list of tags to a list of TagDTOs.
   * @param tags the list of tags to convert
   * @return the converted list of tags
   */
  public static List<TagDTO> convertToTagDTO(List<Tag> tags) {
    Objects.requireNonNull(tags);
    List<TagDTO> tagDTOs = new ArrayList<>();
    TagDTO tagDTO ;
    for (Tag tag : tags) {
      tagDTO = new TagDTO(tag.getName(),tag.getId() , tag.getSha1());
      tagDTOs.add(tagDTO);
    }
    return tagDTOs;
  }

    
  public Long getId() {
    return id;
  }


  public String getName() {
    return name;
  }


  public void setId(Long id) {
    this.id = id;
  }

  
  public String getSha1() {
    return sha1;
  }

  
  public void setSha1(String sha1) {
    this.sha1 = sha1;
  }


  public void setName(String name) {
    this.name = name;
  }

  

}