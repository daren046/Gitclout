package fr.uge.gitclout.gitanalyze;

import fr.uge.gitclout.database.Contributor;
import fr.uge.gitclout.database.Repository;
import fr.uge.gitclout.database.Tag;
import fr.uge.gitclout.gitanalyse.GitAnalysisService;
import fr.uge.gitclout.gitanalyse.GitCloneManager;
import fr.uge.gitclout.gitanalyse.GitRepository;
import fr.uge.gitclout.gitcloutexeption.AnalyzeException;
import fr.uge.gitclout.gitcloutexeption.CloneRepositoryException;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import jakarta.inject.Inject;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class GitAnalysisServiceTest {
  @Inject
  private GitAnalysisService gitAnalysisService;
  
  
  @BeforeAll
   void setUp() throws CloneRepositoryException {
    var repo = new GitRepository("https://github.com/SamueleGiraudo/Calimba");
    var gitCloneManager = new GitCloneManager(repo);
    var path = gitCloneManager.createTempDirectory();
    Git git = gitCloneManager.cloneToDirectory(path);
    gitAnalysisService = new GitAnalysisService(repo, git, gitCloneManager);
  
  }
  
  @Test
  void testGetContributors() throws  GitAPIException, IOException {
    Set<Contributor> contributors = gitAnalysisService.getContributors();
    assertNotNull(contributors);
    assertFalse(contributors.isEmpty());
    assertEquals(2, contributors.size());
  }
  
  @Test
  void testListTags() throws AnalyzeException {
    List<Tag> tags = gitAnalysisService.ListTags();
    assertNotNull(tags);
    assertFalse(tags.isEmpty());
    assertEquals(3, tags.size());
  }
  
  @Test
  void testRepositoryName() throws  NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Method method = GitAnalysisService.class.getDeclaredMethod("createAndSaveRepository");
    method.setAccessible(true);
    var result = (Repository) method.invoke(gitAnalysisService);
    var repo = new Repository("Calimba", "https://github.com/SamueleGiraudo/Calimba");
    assertEquals(repo.getName(), result.getName());
    assertEquals(repo.getUrl(), result.getUrl());
  }
  @Test
  void resolveTags() throws AnalyzeException, NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    var list = new ArrayList<RevCommit>();
    List<Tag> tags = gitAnalysisService.ListTags();
    Method method = GitAnalysisService.class.getDeclaredMethod("resolveTagToCommit" , String.class);
    method.setAccessible(true);
    for (Tag tag : tags) {;
      list.add((RevCommit) method.invoke(gitAnalysisService, tag.getName()));
    }
    assertNotNull(list);
    assertEquals(3, list.size());
  }
  @Test
  void testEmptyTreeParser() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Method method = GitAnalysisService.class.getDeclaredMethod("emptyTreeParser" );
    method.setAccessible(true);
    var result = (CanonicalTreeParser) method.invoke(gitAnalysisService);
    assertNotNull(result);
  }
}
  
  
