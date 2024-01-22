package fr.uge.gitclout.api;

import fr.uge.gitclout.database.Repository;
import io.micronaut.context.ApplicationContext;
import io.micronaut.http.HttpResponse;
import io.micronaut.runtime.Micronaut;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
//@MicronautTest
public class ControllerTest {
  
  private Controllers controllers;
  private final String link = "{\"url\":\"https://github.com/Vlt09/gitCloutTest/\"}";
  @BeforeAll
  void setUp() {
    ApplicationContext app = Micronaut.run(ControllerTest.class);
    this.controllers= app.getBean(Controllers.class);
  }
  
  @Test
  void  processRepositoryTest() throws GitAPIException, IOException {
    var repository = new Repository("gitCloutTest" , link);
    var result = controllers.processRepository(link);
    assert(result.getStatus().equals(HttpResponse.ok().getStatus()));
    assert(result.getBody().isPresent());
    assertEquals(result.getBody().get().getUrl(),"https://github.com/Vlt09/gitCloutTest/");
    assertEquals(5 , result.getBody().get().getTags().size());
    assertEquals(1, result.getBody().get().getContributors().size());
  }

  
  @Test
  void  getAllRepositoryTest() throws GitAPIException, IOException {
    var result = controllers.processRepository(link);
    var test = controllers.findAll();
    assert(!test.isEmpty());
  }
  @Test
  void  deleteRepositoryTest() throws GitAPIException, IOException {
    var result = controllers.deleteRepository(link);
    assertNotEquals(result.getStatus(), HttpResponse.ok().getStatus());
  }
  
  @Test
  void updateRepositoryTest() throws GitAPIException, IOException {
    var result = controllers.processRepository(link);
    var result2 = controllers.processRepository(link);
    assertEquals(result2.getStatus(), HttpResponse.ok().getStatus());
    assertTrue(result2.getBody().isPresent());
    assertTrue(result.getBody().isPresent());
    assertEquals(result2.getBody().get().getTags().size(), 5);
    assertEquals(result2.getBody().get().getContributors().size(), result.getBody().get().getContributors().size());
    assertEquals(result.getBody().get().getTags().size() , result2.getBody().get().getTags().size());
  }
  
}
