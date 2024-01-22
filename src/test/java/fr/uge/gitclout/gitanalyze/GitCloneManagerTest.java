package fr.uge.gitclout.gitanalyze;


import fr.uge.gitclout.gitanalyse.GitCloneManager;
import fr.uge.gitclout.gitanalyse.GitRepository;
import fr.uge.gitclout.gitcloutexeption.CloneRepositoryException;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * This class is used to test the GitCloneManager class.
 * @author Tagnan Tremellat
 * @version 1.0
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GitCloneManagerTest {
  
  private GitCloneManager gitCloneManager;
  @BeforeAll
  void setUp() {
    GitRepository gitRepository = new GitRepository("https://github.com/SamueleGiraudo/Calimba" );
    gitCloneManager = new GitCloneManager(gitRepository);
  }
  
  @Test
  public void cloneToDirectoryTest() throws CloneRepositoryException {
     var path =  gitCloneManager.createTempDirectory();
    var git = gitCloneManager.cloneToDirectory(path);
    assertNotEquals(null, git);
    assert(path.toFile().exists());
    
  }
  
}
