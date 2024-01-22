package fr.uge.gitclout.database;

import fr.uge.gitclout.gitanalyse.LanguageName;
import jakarta.persistence.*;
import java.util.Map;

/**
 * This class is used to manage the database.
 * this table represents the contribution of a contributor to a tag.
 * @author Tagnan Tremellat
 * @version 1.0
 */
@Entity
public class Contribution {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @ManyToOne
  @JoinColumn(name = "contributor_id")
  private Contributor contributor;
  
  @ManyToOne
  @JoinColumn(name = "tag_id")
  private Tag tag;
  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "contributor_language_map", joinColumns = @JoinColumn(name = "contributor_id"))
  @MapKeyColumn(name = "language_name")
  @Column(name = "count")
  private Map<LanguageName, Integer> languageMap;

/**
 * Default constructor.
 */
  public Contribution() {
  }
  
  /**
   * Constructor.
   * @return id of the contribution.
   */
  public Long getId() {
    return id;
  }
  /**
   *
   */
  public Contribution(Contributor contributor, Tag tag, Map<LanguageName, Integer> languageMap) {
    this.tag = tag;
    this.contributor = contributor;
    this.languageMap = languageMap;
  }
  
  /**
   *  Returns the map of languages.
   * @return the map of languages.
   */
  public Map<LanguageName, Integer> getLanguageMap() {
    return languageMap;
  }
  
  /**
   * Sets the map of languages.
   * @param languageMap the map of languages.
   */
  public void setLanguageMap(Map<LanguageName, Integer> languageMap) {
    this.languageMap = languageMap;
  }
  
  /**
   * Sets the tag.
   * @param tag  the tag.
   */
  public void setTag(Tag tag) {
    this.tag = tag;
  }
  
  /**
   * Returns the tag.
   * @return the tag.
   */
  public Tag getTag() {
    return tag;
  }

  /**
   * Sets the contributor.
   * @param contributor the contributor.
   */
  public void setContributor(Contributor contributor) {
    this.contributor = contributor;
  }
  
  /**
   * Returns the contributor.
   * @return the contributor.
   */
  public Contributor getContributor() {
    return contributor;
  }
}