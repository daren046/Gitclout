package fr.uge.gitclout.gitanalyse;

import fr.uge.gitclout.api.ProgressWebSocket;
import fr.uge.gitclout.database.Contribution;
import fr.uge.gitclout.database.Contributor;
import fr.uge.gitclout.database.Tag;
import org.eclipse.jgit.api.BlameCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.blame.BlameResult;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.diff.DiffFormatter;
import org.eclipse.jgit.diff.Edit;
import org.eclipse.jgit.diff.EditList;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.util.io.DisabledOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.*;
import java.util.regex.Pattern;
import static org.eclipse.jgit.diff.Edit.Type.*;

/**
 * ContributionAnalyzer class for analyzing contributions in a Git repository.
 * @author Tagnan Tremellat
 * @version 1.0
 */
public class ContributionAnalyzer {
  
  private final ConcurrentMap<String, BlameResult> blameResultCache = new ConcurrentHashMap<>();
  private final ConcurrentMap<Contributor, ConcurrentMap<LanguageName, Integer>> contributions = new ConcurrentHashMap<>();
  private final ProgressWebSocket progressWebSocket;
  private final LinkedHashSet<Integer> insertLines = new LinkedHashSet<>();
  private final LinkedHashSet<Integer> deleteLines = new LinkedHashSet<>();
  private final ExecutorService executorService;
  private final GitAnalysisService gitAnalysisService ;
  private final Git git;
  
  /**
   * Constructs a new ContributionAnalyzer with the specified Git repository and progressWebSocket.
   * @param progressWebSocket The progressWebSocket to update the progress of the analysis.
   * @param git The Git object representing the repository to analyze.
   * @param gitAnalysisService The GitAnalysisService object to use for Git operations.
   */
  public ContributionAnalyzer(ProgressWebSocket progressWebSocket,  Git git, GitAnalysisService gitAnalysisService) {
    this.git = git;
    this.progressWebSocket = progressWebSocket;
    this.gitAnalysisService = gitAnalysisService;
    this.executorService = Executors.newFixedThreadPool(5);
  }


  /**
   * Analyzes all contributions by tag.
   * This involves examining each commit for each tag and analyzing the contributions.
   * @param tags A List of tags for which contributions are to be analyzed.
   */
  List<Contribution> analyzeContributionsForTags(List<Tag> tags) throws IOException {
    int tagProgress = 0;
    List<Contribution> allContributions = new ArrayList<>();
    if (!tags.isEmpty()) {
      allContributions.addAll(analyzeContributionForTag(tags.get(0)));
      progressWebSocket.updateProgress(++tagProgress, tags.size());
    }
    for (var i = 0; i < tags.size() - 1; i++) {
      allContributions.addAll(analyzeContributionForTags(tags.get(i), tags.get(i + 1)));
      progressWebSocket.updateProgress(++tagProgress, tags.size());
    }
    executorService.shutdown();
    return allContributions;
  }
  
  
  /**
   * Processes the diffs for a given tag.
   *
   * @param tag  The first tag associated with the diffs.
   * @param tag2 The second tag associated with the diffs.
   * @param diff The list of DiffEntry objects representing changes between commits.
   * @return A list of Contribution objects.
   */
  private List<Contribution> processDiffs(Tag tag, Tag tag2, List<DiffEntry> diff) throws  IOException {
    try{
      var contributionList = new ArrayList<Contribution>();
      analyzeContributions(diff, tag, tag2);
      for (var entry : contributions.entrySet()) {
        Contribution contribution = new Contribution(entry.getKey(), tag2, entry.getValue());
        contributionList.add(contribution);
      }
      contributions.clear();
      return contributionList;
    }catch (GitAPIException | InterruptedException | ExecutionException e) {
      throw new IOException("Failed to process diffs", e);
    }
  }

  
  /**
   * Analyzes a single contribution for a given tag and commit.
   * This is used to analyze changes in each commit associated with a specific tag.
   * @param tag The tag to analyze the contribution for.
   * @param tag2 The commit to analyze the contribution in.
   */
  private List<Contribution> analyzeContributionForTags(Tag tag, Tag tag2) throws IOException {
    RevCommit commit = gitAnalysisService.resolveTagToCommit(tag.getName());
    RevCommit commit2 = gitAnalysisService.resolveTagToCommit(tag2.getName());
    List<DiffEntry> diffs = gitAnalysisService.getDiffBetweenCommits(commit, commit2);
    return processDiffs(tag, tag2, diffs);
  }
  
  
  private List<Contribution> analyzeContributionForTag(Tag tag) throws IOException {
    RevCommit commit = gitAnalysisService.resolveTagToCommit(tag.getName());
    var diffEntry = gitAnalysisService.getDiffBetweenEmptyTreeAndCommit(commit);
    return processDiffs(null, tag, diffEntry);
  }


  /**
   * Analyzes the contributions of each contributor.
   * This involves iterating over the repository's files and analyzing each contributor's contributions.
   *
   * @param diffs   The list of DiffEntry objects representing changes between commits.
   * @param oldTag  The old tag associated with the diffs.
   * @param newTag  The new tag associated with the diffs.
   * @throws GitAPIException if an error occurs during the analysis.
   */
  private void analyzeContributions(List<DiffEntry> diffs, Tag oldTag, Tag newTag) throws GitAPIException, InterruptedException, ExecutionException {
    var futureList = new ArrayList<Callable<Integer>>();
    for (DiffEntry diff : diffs) {
      futureList.add(() -> {
        var fileType = recognizeLanguage(diff.getNewPath());
        EditList editList = editDiffs(diff);
        processEdits(editList);
        switch (diff.getChangeType()) {
          case DELETE -> analyzeFileChange(diff, oldTag, null, fileType);
          case MODIFY -> analyzeFileChange(diff, oldTag, newTag, fileType);
          default -> analyzeFileChange(diff, null, newTag, fileType);
        }
        return 1;
      });
    }
    insertLines.clear();
    deleteLines.clear();
    var future = executorService.invokeAll(futureList);
    for (var f : future) {
      f.get();
    }
  }


  /**
   * Analyzes the file changes in a Git repository between two commits for a specific file type.
   *
   * @param diff         The DiffEntry representing the file changes.
   * @param oldTagOpt    An Optional containing the old tag associated with the file changes.
   * @param newTagOpt    An Optional containing the new tag associated with the file changes.
   * @param fileType     The type of the file being analyzed.
   * @throws GitAPIException if there is an error during Git operations.
   * @throws IOException     if there is an I/O error.
   */
  private void analyzeFileChange(DiffEntry diff, Tag oldTagOpt, Tag newTagOpt, LanguageName fileType) throws GitAPIException, IOException {
    if (oldTagOpt != null) {
      BlameResult blameResultOld = getBlameResult(diff.getOldPath(), oldTagOpt);
      analyzeContributionsForBlameResult(blameResultOld, fileType, true);
    }
    if (newTagOpt != null) {
      BlameResult blameResultNew = getBlameResult(diff.getNewPath(), newTagOpt);
      analyzeContributionsForBlameResult(blameResultNew, fileType, false);
    }
  }

  
  /**
   * Analyzes contributions based on the blame result of a file.
   *
   * @param blameResult  The BlameResult object containing information about file changes.
   * @param fileType     The type of the file being analyzed.
   * @param isDelete     A boolean indicating whether the file change is a deletion.
   */
  private void analyzeContributionsForBlameResult(BlameResult blameResult, LanguageName fileType, boolean isDelete) {
    if (blameResult == null) {
      return;
    }
    for (int i = 0; i < blameResult.getResultContents().size(); i++) {
      String line = blameResult.getResultContents().getString(i);
      var pastLine = LineStatement.Code;
      var actualStateLine = isComment(line, fileType, pastLine);
      if (actualStateLine == LineStatement.Code) {
        boolean isModifiedLine = isLineModified(i, isDelete);
        if (isModifiedLine) {
          PersonIdent author = blameResult.getSourceAuthor(i);
          addOrUpdateContribution(fileType, author.getName(),isDelete, author.getEmailAddress());
        }
      }
    }
  }


  /**
   * Adds or updates the contribution of an author for a specific file type.
   *
   * @param fileType    The type of the file being analyzed.
   * @param authorName  The name of the author.
   * @param isDelete    A boolean indicating whether the file change is a deletion.
   */
  private void addOrUpdateContribution(LanguageName fileType, String authorName, Boolean isDelete , String authorEmail) {
    // Implement logic to add or update the contribution of an author for the specified fileType
    var contributor = new Contributor(authorName , authorEmail);
    if (!contributions.containsKey(contributor)) {
      contributions.put(contributor, new ConcurrentHashMap<>());
    }
    var contribution = contributions.getOrDefault(contributor, new ConcurrentHashMap<>());
    int currentContribution = contribution.getOrDefault(fileType, 0);
    if (!isDelete && currentContribution >= 0) {
      contribution.put(fileType, currentContribution + 1);
    }
    contributions.put(contributor, contribution);
  }


  /**
   * Generates an EditList representing the edits in a DiffEntry.
   *
   * @param diff The DiffEntry representing the file changes.
   * @return An EditList containing the edits.
   * @throws IOException if there is an I/O error.
   */
  private EditList editDiffs(DiffEntry diff) throws IOException {
    DiffFormatter diffFormatter = new DiffFormatter(DisabledOutputStream.INSTANCE);
    diffFormatter.setRepository(git.getRepository());
    return diffFormatter.toFileHeader(diff).toEditList();
  }

  
  /**
   * Checks if a line in the file has been modified based on its line number.
   *
   * @param lineNumber The line number to check.
   * @param isDelete   A boolean indicating whether the file change is a deletion.
   * @return true if the line has been modified, false otherwise.
   */
  private boolean isLineModified(int lineNumber, boolean isDelete) {
    if(isDelete){
      return deleteLines.contains(lineNumber);
    }else {
      return insertLines.contains(lineNumber);
    }
  }


  /**
   * Processes edits in an EditList to update line numbers for insertions and deletions.
   *
   * @param edits The EditList containing the edits.
   */
  private void processEdits(EditList edits){
    for(Edit edit : edits){
      if(edit.getType() == INSERT  || edit.getType() == REPLACE){
        for(int i = edit.getBeginB(); i < edit.getEndB(); i++){
          insertLines.add(i);
        }
      }
      if(edit.getType() == DELETE){
        for(int i = edit.getBeginA(); i < edit.getEndA(); i++){
          deleteLines.add(i);
        }
      }
    }
  }

  
  /**
   * Retrieves the blame result for a specific file at a given tag.
   *
   * @param filePath The path to the file in the repository.
   * @param tag      The tag associated with the file version.
   * @return The BlameResult object containing information about the file's history.
   * @throws GitAPIException if there is an error during Git operations.
   * @throws IOException     if there is an I/O error.
   */
  private BlameResult getBlameResult(String filePath, Tag tag) throws GitAPIException, IOException {
    if (blameResultCache.containsKey(filePath)) {
      return blameResultCache.get(filePath);
    }
    var blameCommand = new BlameCommand(git.getRepository());
    blameCommand.setFilePath(filePath);
    blameCommand.setStartCommit(gitAnalysisService.resolveTagToCommit(tag.getName()));
    blameCommand.setFollowFileRenames(true);
    var result = blameCommand.call();
    blameResultCache.put(filePath, result);
    return result;
  }


  /**
   * Recognizes the language in which a given file is written.
   * @param fileName the name of the file being analyzed.
   * @return the corresponding LanguageName.
   */
  private LanguageName recognizeLanguage(String fileName) {
    String extension = fileName.substring(fileName.lastIndexOf(".") + 1);
    return switch (extension) {
      case "java" -> LanguageName.JAVA;
      case "py" -> LanguageName.PYTHON;
      case "js" -> LanguageName.JAVASCRIPT;
      case "html" -> LanguageName.HTML;
      case "ts" -> LanguageName.TYPESCRIPT;
      case "c", "h", "hpp", "hxx", "cpp", "hh", "cc", "cxx", "c++" -> LanguageName.C;
      case "hs" -> LanguageName.HASKELL;
      case "ml", "mli", "mll", "mly" -> LanguageName.OCAML;
      case "md" -> LanguageName.MARKDOWN;
      case "xml" -> LanguageName.XML;
      case "rb" -> LanguageName.RUBY;
      default -> LanguageName.OTHER;
    };
  }

  
  private  boolean isCodeBetweenMultiLineComment(String line, LanguageName language) {
    var commentStart = Pattern.quote(language.getMultiLineCommentStart());
    var commentEnd = Pattern.quote(language.getMultiLineCommentEnd());
    String regex = commentStart + ".*?" + commentEnd + ".*?" + commentStart + ".*?" + commentEnd;
    Pattern pattern = Pattern.compile(regex);
    var matcher = pattern.matcher(line);
    return !matcher.find();
  }

  
  /**
   * Checks if a line in a file is a comment based on the file type.
   *
   * @param line     The line of code to check.
   * @param language The type of the file being analyzed.
   * @param previousLineStatement The statement of the previous line.
   * @return LineStatement The statement of the line.
   */
  private  LineStatement isComment(String line, LanguageName language, LineStatement previousLineStatement) {
    String lineTrimmed = line.trim();
    if (!language.getSingleLineCommentRegex().isEmpty() && (lineTrimmed.startsWith(language.getSingleLineCommentRegex())) || lineTrimmed.isEmpty() || (lineTrimmed.startsWith(language.getMultiLineCommentStart()) && lineTrimmed.endsWith(language.getMultiLineCommentEnd())) && isCodeBetweenMultiLineComment(lineTrimmed, language)) {
      return LineStatement.SingleLineComment;
    }
    if (!lineTrimmed.contains(language.getMultiLineCommentStart()) && lineTrimmed.endsWith(language.getMultiLineCommentEnd())) {
      return LineStatement.EndComment;
    }
    if (lineTrimmed.startsWith(language.getMultiLineCommentStart()) && lineTrimmed.endsWith(language.getMultiLineCommentEnd()) && isCodeBetweenMultiLineComment(lineTrimmed, language)) {
      return LineStatement.StartComment;
    }
    if (!language.getSingleLineCommentRegex().isEmpty() && lineTrimmed.startsWith(language.getSingleLineCommentRegex())) {
      return LineStatement.SingleLineComment;
    }
    return processMultiLineComment(lineTrimmed, language, previousLineStatement);
  }
  
  
  /**
   * Determines whether a given line of code is part of a multiline comment.
   * This method checks if the line starts or ends with the multiline comment syntax of the given language,
   * or if the previous line was marked as the start or part of a multiline comment.
   * It's useful for parsing and analyzing source code to identify multiline comment sections.
   *
   * @param lineTrimmed The trimmed line of source code to be checked.
   * @param language The programming language which defines the multiline comment syntax.
   * @param previousLineStatement The classification of the previous line (e.g., start of multiline comment, part of multiline comment).
   * @return boolean Returns true if the line is part of a multiline comment, false otherwise.
   */
  private  boolean isMultiLineComment(String lineTrimmed, LanguageName language, LineStatement previousLineStatement) {
    boolean startsWithMultiLineComment = lineTrimmed.startsWith(language.getMultiLineCommentStart());
    boolean endsWithMultiLineComment = lineTrimmed.endsWith(language.getMultiLineCommentEnd());
    return startsWithMultiLineComment || endsWithMultiLineComment || previousLineStatement == LineStatement.StartComment || previousLineStatement == LineStatement.MultiLineComment;
  }
  
  
  /**
   * Calculates the index of the end of a multiline comment in a given line of code.
   * This method returns the position of the end of a multiline comment in the line.
   * @param lineTrimmed The trimmed line of source code to be analyzed.
   * @param language The programming language which defines the multiline comment syntax.
   * @param startsWithMultiLineComment A boolean indicating whether the line starts with a multiline comment.
   * @return int The index of the end of the multiline comment in the line. If the comment end is not found, returns -1.
   */
  private  int getEndCommentIndex(String lineTrimmed, LanguageName language, boolean startsWithMultiLineComment) {
    String commentEnd = language.getMultiLineCommentEnd();
    return startsWithMultiLineComment && lineTrimmed.endsWith(commentEnd) ?
            lineTrimmed.lastIndexOf(commentEnd) : lineTrimmed.indexOf(commentEnd);
  }
  
  
  /**
   * Determines the classification of a given line of source code.
   * This method classifies a line as the start or end of a multiline comment, a part of a multiline comment, or as regular code.
   *
   * @param lineTrimmed The trimmed line of source code to be analyzed.
   * @param language The programming language which defines the multiline comment syntax.
   * @param previousLineStatement The classification of the previous line, to provide context in case of multiline comments.
   * @return LineStatement The classification of the line, such as StartComment, EndComment, MultiLineComment, or Code.
   */
  private  LineStatement determineLineStatement(String lineTrimmed, LanguageName language, LineStatement previousLineStatement) {
    boolean startsWithMultiLineComment = lineTrimmed.startsWith(language.getMultiLineCommentStart());
    boolean endsWithMultiLineComment = lineTrimmed.endsWith(language.getMultiLineCommentEnd());
    
    if (startsWithMultiLineComment && !endsWithMultiLineComment) {
      return LineStatement.StartComment;
    } else if (previousLineStatement == LineStatement.StartComment || previousLineStatement == LineStatement.MultiLineComment) {
      return endsWithMultiLineComment ? LineStatement.EndComment : LineStatement.MultiLineComment;
    }
    return LineStatement.Code;
  }
  
  
  /**
   * Processes a line of code to determine its classification with respect to multiline comments.
   * This method decides whether a line is a part of a multiline comment or regular code.
   *
   * @param lineTrimmed The trimmed line of source code to be analyzed.
   * @param language The programming language which defines the multiline comment syntax.
   * @param previousLineStatement The classification of the previous line, to provide context for multiline comments.
   * @return LineStatement The classification of the line, such as Code, StartComment, EndComment, or MultiLineComment.
   */
  private  LineStatement processMultiLineComment(String lineTrimmed, LanguageName language, LineStatement previousLineStatement) {
    boolean isMultiLineComment = isMultiLineComment(lineTrimmed, language, previousLineStatement);
    if (isMultiLineComment) {
      int endCommentIndex = getEndCommentIndex(lineTrimmed, language, lineTrimmed.startsWith(language.getMultiLineCommentStart()));
      if (endCommentIndex != -1 && endCommentIndex + language.getMultiLineCommentEnd().length() < lineTrimmed.length()) {
        return LineStatement.Code;
      }
      return determineLineStatement(lineTrimmed, language, previousLineStatement);
    }
    return LineStatement.Code;
  }


  /**
   * Updates contributions based on new tags.
   *
   * @param tags The list of tags to analyze contributions for.
   * @return A list of contributions.
   */
  List<Contribution> updateContribution(List<Tag> tags) throws IOException {
    // Analyze the contributions for new tags
    List<Contribution> contributions = new ArrayList<>();
    for (int i = 0; i < tags.size() - 1; i++) {
      contributions.addAll(analyzeContributionForTags(tags.get(i), tags.get(i + 1)));
    }
    return contributions;
  }
}
