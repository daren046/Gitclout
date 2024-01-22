package fr.uge.gitclout.gitanalyze;

import fr.uge.gitclout.database.Contributor;
import fr.uge.gitclout.gitanalyse.ContributorManagementService;
import fr.uge.gitclout.gitanalyse.TagManagementService;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
public class ContributionManagementServiceTest {
  
  @Test
  void testDeleteContributors() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    var contributorManagementService = new ContributorManagementService();
    var oldContributors = Set.of(new Contributor("1", "1"), new Contributor("2", "2"), new Contributor("3", "3"));
    var updateContributors = Set.of(new Contributor("1", "1"), new Contributor("2", "2"));
    var expected = Set.of(new Contributor("3", "3"));
    var result = contributorManagementService.deleteContributors(oldContributors, updateContributors);
    assertEquals(expected.size(), result.size());
    oldContributors = Set.of(new Contributor("1", "1"), new Contributor("2", "2"), new Contributor("3", "3"));
    updateContributors = Set.of(new Contributor("1", "1"), new Contributor("2", "2"), new Contributor("3", "3"));
    result = contributorManagementService.deleteContributors(oldContributors, updateContributors);
    assertEquals(0, result.size());
    oldContributors = Set.of(new Contributor("1", "1"), new Contributor("2", "2"), new Contributor("3", "3"));
    updateContributors = Set.of(new Contributor("1", "1"), new Contributor("2", "2"), new Contributor("3", "3"), new Contributor("4", "4"));
    result = contributorManagementService.deleteContributors(oldContributors, updateContributors);
    assertEquals(0, result.size());
  }
  
  
}
