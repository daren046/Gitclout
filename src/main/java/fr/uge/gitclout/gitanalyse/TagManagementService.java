package fr.uge.gitclout.gitanalyse;

import fr.uge.gitclout.database.Tag;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * This class is used to manage the tags of a repository.
 * @author Tagnan Tremellat
 * @version 1.0
 */
public class TagManagementService {

  public TagManagementService() {}
  
  /**
   * Updates the list of tags by deleting old tags that are not present in the updated tags.
   *
   * @param oldTags     The list of old tags.
   * @param updateTags  The list of updated tags.
   * @return A list of tags after deletion.
   */
  List<Tag> deleteTag(List<Tag> oldTags, List<Tag> updateTags) {
    Set<String> updateTagNames = updateTags.stream()
            .map(Tag::getSha1)
            .collect(Collectors.toSet());
    
    return oldTags.stream()
            .filter(oldTag -> !updateTagNames.contains(oldTag.getSha1()))
            .collect(Collectors.toList());
  }
  
  
  /**
   * Updates the list of tags by extracting new tags from the updated tags.
   *
   * @param oldTags     The list of old tags.
   * @param updateTags  The list of updated tags.
   * @return A list of new tags.
   */
  List<Tag> updateTags(List<Tag> oldTags, List<Tag> updateTags) {
    if (updateTags.size() == oldTags.size()) {
      for (Tag newTag : updateTags) {
        if (!oldTags.contains(newTag)) {
          return updateTags.subList(oldTags.size() - 1, updateTags.size());
        }
      }
      return null;
    }
    if(updateTags.size() < oldTags.size()){
      return updateTags;
    }
    if (oldTags.isEmpty()) {
      return updateTags;
    }
    return updateTags.subList(oldTags.size() - 1, updateTags.size());
  }
  
  
  List<Tag> getTrulyNewTagsWithLast(List<Tag> newTags, List<Tag> existingTags) {
    List<Tag> trulyNewTags = newTags.stream()
            .filter(tag -> !existingTags.contains(tag))
            .collect(Collectors.toList());
    
    if (!existingTags.isEmpty()) {
      trulyNewTags.add(0, existingTags.get(existingTags.size() - 1));
    }
    return trulyNewTags;
  }
  

  /**
   * Adds new tags to the List of tags without duplication .
   * @param repositoryTags  The list of old tags.
   * @param newTags updated tags
   */
  void addNewTagsWithoutDuplicates(List<Tag> repositoryTags, List<Tag> newTags) {
    Set<String> existingTagNames = repositoryTags.stream()
    .map(Tag::getName)
    .collect(Collectors.toSet());
  
    List<Tag> uniqueNewTags = newTags.stream()
      .filter(tag -> !existingTagNames.contains(tag.getName()))
            .toList();
      repositoryTags.addAll(uniqueNewTags);
  }
}
