package fr.uge.gitclout.database;

import jakarta.persistence.*;
import java.util.Objects;

/**
 * This class is used to manage the database .
 * this table represents the contributor of a repository
 * @author Tagnan Tremellat
 * @version 1.0
 */
@Entity
@Table(name = "contributor")
public class Contributor {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @Column(name = "username", nullable = false )
  private String username;
  
  @Column(name = "email", nullable = false )
  private String email;

  public Contributor(String username) {
    this.username = username;
  }


  public Contributor() {
  }


  public Contributor(String username , String email) {
    this.username = username;
    this.email = email;
  }


  public Contributor(Long id, String username, String email) {
    this.id = id;
    this.username = username;
    this.email = email;
  }


  public Long getId () {
    return this.id;
  }


  public String getUsername(){
    return this.username;
  }


  public String toString(){
    return id + " Name " + username  ;
  }
    

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Contributor contributor = (Contributor) o;
    return Objects.equals(username, contributor.username);
  }


  @Override
  public int hashCode() {
    return Objects.hash(username);
  }


  public String getName() {
    return username;
  }


  public void setUsername(String username) {
    this.username = username;
  }
  
  
  public String getEmail() {
    return email;
  }


  public void setEmail(String email) {
    this.email = email;
  }
}
