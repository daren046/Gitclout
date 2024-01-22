package fr.uge.gitclout.gitanalyze;

import fr.uge.gitclout.database.Contributor;
import fr.uge.gitclout.database.Repository;
import fr.uge.gitclout.database.Tag;
import fr.uge.gitclout.gitanalyse.*;
import fr.uge.gitclout.gitcloutexeption.CloneRepositoryException;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GitAnalyzeTest {
  
  private GitAnalyze gitAnalyze;
  @BeforeAll
  void setUp() throws CloneRepositoryException {
    var repo = new GitRepository("https://github.com/SamueleGiraudo/Calimba");
    var gitCloneManager = new GitCloneManager(repo);
    var path = gitCloneManager.createTempDirectory();
    Git git = gitCloneManager.cloneToDirectory(path);
    var gitAnalysisService = new GitAnalysisService(repo, git, gitCloneManager);
    var contributionAnalyzer = new ContributionAnalyzer(null, git, gitAnalysisService);
    gitAnalyze = new GitAnalyze(contributionAnalyzer, gitAnalysisService , new TagManagementService() , new ContributorManagementService());
    
    
  }
  
  @Test
  void updateTest() throws GitAPIException, IOException {
    Repository repository = new Repository("Calimba" , "https://github.com/SamueleGiraudo/Calimba");
    var emptyList = new ArrayList<Tag>();
    var emptyList2 = new HashSet<Contributor>();
    repository.setTags(emptyList);
    repository.setContributors(emptyList2);
    var result = gitAnalyze.updateRepository(repository);
    assert(result.isPresent());
    assert(result.get().getContributors().size() == 2);
    assert(result.get().getTags().size() == 3);
    assert(result.get().getContributors().stream().anyMatch(contributor -> contributor.getName().equals("SamueleGiraudo")));
    assert(result.get().getContributors().stream().anyMatch(contributor -> contributor.getName().equals("Samuele Giraudo")));
    assert(result.get().getTags().stream().anyMatch(tag -> tag.getName().equals("v0.1011")));
    assert(result.get().getTags().stream().anyMatch(tag -> tag.getName().equals("v0.1010")));
    assert(result.get().getTags().stream().anyMatch(tag -> tag.getName().equals("v0.0011")));
    
  }
  @Test
  void updateTest2() throws GitAPIException, IOException {
    Repository repository = new Repository("Calimba" , "https://github.com/SamueleGiraudo/Calimba");
    var emptyList = new ArrayList<Tag>();
    emptyList.add(new Tag("v0.1011425", "ejkdfndkj"));
    emptyList.add(new Tag("v0.101033", "sdjsdnkd"));
    emptyList.add(new Tag("v0.001122" , "ejzkdjskd"));
    var emptyList2 = new HashSet<Contributor>();
    repository.setTags(emptyList);
    repository.setContributors(emptyList2);
    var result = gitAnalyze.updateRepository(repository);
    assert(result.isPresent());
    assert(result.get().getContributors().size() == 2);
    assert(result.get().getTags().size() == 3);
    assert(result.get().getContributors().stream().anyMatch(contributor -> contributor.getName().equals("SamueleGiraudo")));
    assert(result.get().getContributors().stream().anyMatch(contributor -> contributor.getName().equals("Samuele Giraudo")));
    assert(result.get().getTags().stream().anyMatch(tag -> tag.getName().equals("v0.1011")));
    assert(result.get().getTags().stream().anyMatch(tag -> tag.getName().equals("v0.1010")));
    assert(result.get().getTags().stream().anyMatch(tag -> tag.getName().equals("v0.0011")));
    
  }
  

}

