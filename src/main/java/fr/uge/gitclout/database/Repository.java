package fr.uge.gitclout.database;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;

import java.util.List;
import java.util.Set;


/**
 * This class is used to manage the database .
 * this table represents the repository
 * @author Tagnan Tremellat
 * @version 1.0
 */
@Entity
@Table(name = "repositories")
public class Repository {
  @JsonProperty("id")
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Column(name = "name", nullable = false)
  private String name;
  
  @Column(name = "url", nullable = false)
  private String url;

  @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
  @JoinColumn(name = "repository_id")
  private List<Tag> tags;

  @ManyToMany(fetch = FetchType.EAGER, cascade = CascadeType.MERGE)
  @JoinTable(
          name = "repository_contributors",
          joinColumns = @JoinColumn(name = "repository_id"),
          inverseJoinColumns = @JoinColumn(name = "contributor_id")
  )
  private Set<Contributor> contributors;
  

  public Repository(String name , String url,Set<Contributor> contributors){
    this.name = name;
    this.url = url;
    this.contributors = contributors;
  }


  public Repository() {
  }


  public Repository(String name, String url) {
    this.name = name;
    this.url = url;
  }
  
  
  public Set<Contributor> getContributors() {
    return contributors;
  }


  public Long getId() {
    return id;
  }


  public String getName() {
    return name;
  }


  public void setContributors(Set<Contributor> contributors) {
    this.contributors = contributors;
  }


  public String getUrl() {
    return url;
  }

  
  public List<Tag> getTags() {
    return tags;
  }

  
  public void setTags(List<Tag> tags) {
    this.tags = tags;
  }

  
  public String toString(){
    return "Repository{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", url='" + url + '\'' +
            ", tagCount=" + (tags != null ? tags.size() : "null") +
            ", contributorCount=" + (contributors != null ? contributors.size() : "null") +
            '}';
  }
}