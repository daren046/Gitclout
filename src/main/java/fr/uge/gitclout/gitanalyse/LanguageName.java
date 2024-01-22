package fr.uge.gitclout.gitanalyse;

/**
 * This enum contains the names of the languages supported by the application.
 * It also contains the regexes used to detect comments in the source code.
 * @author Tagnan Tremellat
 * @version 1.0
 */
public enum LanguageName {
  JAVA("//", "/*", "*/"),
  PYTHON("#", "'''", "'''|\"\"\""),
  JAVASCRIPT("//", "/*", "*/"),
  C("//", "/*", "*/"),
  C_PLUS_PLUS("//", "/*", "*/"),
  C_SHARP("//", "/*", "*/"),
  HASKELL("--", "{-", "-}"),
  HTML("<!--", "<!--", "-->"),
  TYPESCRIPT("//", "/*", "*/"),
  OCAML("", "(*", "*)"),
  OTHER("", "", ""),
  MARKDOWN("" , "", ""),
  XML ("", "<!--", "-->"),
  RUBY("#", "=begin", "=end");

  private final String singleLineCommentRegex;
  private final String multiLineCommentStart;
  private final String multiLineCommentEnd;
  
  LanguageName(String singleLineCommentRegex, String multiLineCommentStart, String multiLineCommentEnd) {
    this.singleLineCommentRegex = singleLineCommentRegex;
    this.multiLineCommentStart = multiLineCommentStart;
    this.multiLineCommentEnd = multiLineCommentEnd;
  }


  public String getSingleLineCommentRegex() {
    return singleLineCommentRegex;
  }
  
  
  public String getMultiLineCommentStart() {
    return multiLineCommentStart;
  }


  public String getMultiLineCommentEnd() {
    return multiLineCommentEnd;
  }
}
  

