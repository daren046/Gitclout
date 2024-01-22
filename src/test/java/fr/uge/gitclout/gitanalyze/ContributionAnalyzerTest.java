package fr.uge.gitclout.gitanalyze;

import fr.uge.gitclout.database.Contribution;
import fr.uge.gitclout.database.Tag;
import fr.uge.gitclout.gitanalyse.*;
import fr.uge.gitclout.gitcloutexeption.AnalyzeException;
import fr.uge.gitclout.gitcloutexeption.CloneRepositoryException;
import jakarta.inject.Inject;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.diff.DiffEntry;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

/**
 * This class is used to test the ContributionAnalyzer class.
 * @author Tagnan Tremellat
 * @version 1.0
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ContributionAnalyzerTest {
  
  @Inject
  private ContributionAnalyzer contributionAnalyzer;
  private List<Tag> tags;
  private List<DiffEntry> diffs;
   @BeforeAll
   void setUp() throws CloneRepositoryException, AnalyzeException {
     var repo = new GitRepository("https://github.com/SamueleGiraudo/Calimba" );
     var gitCloneManager = new GitCloneManager(repo);
     var path = gitCloneManager.createTempDirectory();
     Git git = gitCloneManager.cloneToDirectory(path);
     var gitAnalysisService = new GitAnalysisService(repo, git, gitCloneManager);
     contributionAnalyzer = new ContributionAnalyzer(null, git, gitAnalysisService);
     tags = gitAnalysisService.ListTags();
   }
   
  @Test
  public void MultiLineCommentInJava() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Method method = ContributionAnalyzer.class.getDeclaredMethod("isCodeBetweenMultiLineComment", String.class, LanguageName.class);
    method.setAccessible(true);
    var line = "/* texte entre commentaires */";
    var Language = LanguageName.JAVA;
    var result1 = (boolean) method.invoke(contributionAnalyzer,line , Language);
    assertTrue(result1);
    var line2 =   "/* aa */ function(); /* aa */ /* aa */ function(); /* ";
    var result2 = (boolean) method.invoke(contributionAnalyzer,line2 , Language);
    assertFalse(result2);
    String line3 = "/* a a a a a */";
    var result3 = (boolean) method.invoke(contributionAnalyzer,line3 , Language);
    assertTrue(result3);
    line = " texte entre commentaires \"\"\"";
    var result4 = (boolean) method.invoke(contributionAnalyzer,line , Language);
    assertTrue(result4);
    line = "\"\"\" texte entre commentaires \"\"\" func() \"\"\" texte entre commentaires \"\"\"";
    var language = LanguageName.PYTHON;
    var result5 = (boolean) method.invoke(contributionAnalyzer, line , Language);
    assertTrue(result5);
  }
  
  @Test
  public void CheckCommentInCode() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Method method = ContributionAnalyzer.class.getDeclaredMethod("isComment", String.class, LanguageName.class, LineStatement.class);
    method.setAccessible(true);
    String[]  code = {"int main() {",
            "// truc", "/* comm1 */", "printf(%d, 3); /* comm2 */", "int a, b, c; /*",
            "comm3*/ printf(aa);", "/*", "a", "a", " */", "return 0; // truc", "/* aa */ function(); /* aa", "*/",
            " /* aa */ function(); /* aa */ /* aa */ function(); /* ", "aa */ truc();", " /*",
            "*/ int a;", "return 0;", "}"
    };
    var language = LanguageName.JAVA;
    var codeCount = 0;
    
    var lineStatement = LineStatement.Code;
    for (String line : code) {
      LineStatement isComment = (LineStatement) method.invoke(contributionAnalyzer, line, language, lineStatement);
      if (isComment == LineStatement.Code) {
        codeCount++;
      }
      lineStatement = isComment;
      
    }
    assertEquals(11, codeCount);
    
  }
  
  @Test
  void testRecognizeLanguage() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
     Method method = ContributionAnalyzer.class.getDeclaredMethod("recognizeLanguage", String.class);
      method.setAccessible(true);
      var filename = "test.java";
      var result = (LanguageName) method.invoke(contributionAnalyzer, filename);
      assertEquals(LanguageName.JAVA, result);
      filename = "test.py";
      result = (LanguageName) method.invoke(contributionAnalyzer, filename);
      assertEquals(LanguageName.PYTHON, result);
      filename = "test.java.c";
      result = (LanguageName) method.invoke(contributionAnalyzer, filename);
      assertEquals(LanguageName.C, result);
  }
  @SuppressWarnings("unchecked")
  @Test
  void testAnalyzeContributionForTags() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Method method = ContributionAnalyzer.class.getDeclaredMethod("analyzeContributionForTags", Tag.class , Tag.class);
    method.setAccessible(true);
    var tag1 = tags.get(0);
    var tag2 = tags.get(1);
    var result = (List<Contribution>) method.invoke(contributionAnalyzer, tag1, tag2);
    assertNotNull(result);
    assertEquals(1, result.size());
    result = (List<Contribution>) method.invoke(contributionAnalyzer, tag1, tag1);
    assertNotNull(result);
    assertEquals(0, result.size());
  }
  @SuppressWarnings("unchecked")
  @Test
  void testAnalyzeContributionForTag() throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
    Method method = ContributionAnalyzer.class.getDeclaredMethod("analyzeContributionForTag", Tag.class);
    method.setAccessible(true);
    var tag1 = tags.get(0);

    var result = (List<Contribution>) method.invoke(contributionAnalyzer, tag1);
    assertNotNull(result);
    assertEquals(1, result.size());
  }
}
