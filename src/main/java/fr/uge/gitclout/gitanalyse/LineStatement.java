package fr.uge.gitclout.gitanalyse;

/**
 * This enum contains the different types of statements that can be found in a source code file.
 * @author Tagnan Tremellat
 * @version 1.0
 */
public enum LineStatement {
  StartComment,
  EndComment,
  Code,
  SingleLineComment,
  MultiLineComment
}
