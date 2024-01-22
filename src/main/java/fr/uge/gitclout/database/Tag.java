package fr.uge.gitclout.database;

import jakarta.persistence.*;

import java.util.Date;
import java.util.List;
import java.util.Objects;


/**
 * This class is used to manage the database .
 * this table represents the tag of a repository
 * @author Tagnan Tremellat
 * @version 1.0
 */
@Entity
public class Tag {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  
  @Column(name = "name", nullable = false)
  private String name;
  
  @OneToMany(mappedBy = "tag", cascade = CascadeType.ALL)
  private List<Contribution> contributions;
  @ManyToOne
  @JoinColumn(name = "repository_id")
  private Repository repository;
  
  @Column(name = "sha1", nullable = false)
  private String sha1;


  public Tag(String name,String sha1) {
    this.name = name;
    this.sha1 = sha1;
  }
  
  public void setRepository(Repository repository) {
    this.repository = repository;
  }


  public Tag(String name) {
    this.name = name;
  }

  
  public Tag() {
  }
  
  public List<Contribution> getContributions() {
    return contributions;
  }


  public void setContributions(List<Contribution> contributions) {
    this.contributions = contributions;
  }

  
  public Long getId() {
    return id;
  }


  public String getName() {
    return name;
  }

  
  public void setSha1(String sha1) {
    this.sha1 = sha1;
  }


  public String getSha1() {
    return sha1;
  }

  
  public String toString() {
    return "Tag{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", sha1='" + sha1 + '\'' +
            ", repositoryId=" + (repository != null ? repository.getName(): "null") +
            '}';
  }

  
  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null || getClass() != obj.getClass()) return false;
    Tag tag = (Tag) obj;
    return Objects.equals(sha1, tag.sha1);
  }

  
  @Override
  public int hashCode() {
    return Objects.hash(sha1);
  }
}
