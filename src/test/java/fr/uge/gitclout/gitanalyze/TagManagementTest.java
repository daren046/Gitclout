package fr.uge.gitclout.gitanalyze;

import fr.uge.gitclout.database.Tag;
import fr.uge.gitclout.gitanalyse.TagManagementService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class TagManagementTest {
    @SuppressWarnings("unchecked")
    @Test
    void testDeleteTag() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
      TagManagementService tagManagementService = new TagManagementService();
      Method method = TagManagementService.class.getDeclaredMethod("deleteTag", List.class, List.class);
      method.setAccessible(true);
      var oldTags = List.of(new Tag("1", "1"), new Tag("2", "2"), new Tag("3", "3"));
      var updateTags = List.of(new Tag("1", "1"), new Tag("2", "2"));
      List<Tag> result = (List<Tag>) method.invoke(tagManagementService, oldTags, updateTags);
      var expected = List.of(new Tag("3", "3"));
      assertEquals(expected.size(), result.size());
      oldTags = List.of(new Tag("1", "1"), new Tag("2", "2"), new Tag("3", "3"));
      updateTags = List.of(new Tag("1", "1"), new Tag("2", "2"), new Tag("3", "3"));
      result = (List<Tag>) method.invoke(tagManagementService, oldTags, updateTags);
      assertEquals(0, result.size());
      oldTags = List.of(new Tag("1", "1"), new Tag("2", "2"), new Tag("3", "3"));
      updateTags = List.of(new Tag("1", "1"), new Tag("2", "2"), new Tag("3", "3"), new Tag("4", "4"));
      result = (List<Tag>) method.invoke(tagManagementService, oldTags, updateTags);
      assertEquals(0, result.size());
    }
  @SuppressWarnings("unchecked")
  @Test
  void testUpdateTags() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
      var tagManagementService = new TagManagementService();
    Method method = TagManagementService.class.getDeclaredMethod("updateTags", List.class, List.class);
    method.setAccessible(true);
    var oldTags = List.of(new Tag("1", "1"), new Tag("2", "2"), new Tag("3", "3"));
    var updateTags = List.of(new Tag("1", "1"), new Tag("2", "2"));
    var result = (List<Tag>) method.invoke(tagManagementService, oldTags, updateTags);
    var expected = List.of( new Tag("2" , "2"),new Tag("3", "3"));
    assertEquals(expected.size(), result.size());
    oldTags = List.of(new Tag("1", "1"), new Tag("2", "2"), new Tag("3", "3"));
    updateTags = List.of(new Tag("1", "1"), new Tag("2", "2"), new Tag("3", "3"));
    result = (List<Tag>) method.invoke(tagManagementService, oldTags, updateTags);
    assert(result == null);
    oldTags = List.of(new Tag("1", "1"), new Tag("2", "2"), new Tag("3", "3"));
    updateTags = List.of(new Tag("1", "1"), new Tag("2", "2"), new Tag("3", "3"), new Tag("4", "4"));
    result = (List<Tag>) method.invoke(tagManagementService, oldTags, updateTags);
    expected = List.of(new Tag("3", "3") ,new Tag("4", "4"));
    assertEquals(expected.size(), result.size());
  }
  
  @SuppressWarnings("unchecked")
  @Test
  void testGetTrulyNewTagsWithLast() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
    var tagManagementService = new TagManagementService();
    Method method = TagManagementService.class.getDeclaredMethod("getTrulyNewTagsWithLast", List.class, List.class);
    method.setAccessible(true);
    var oldTags = List.of(new Tag("1", "1"), new Tag("2", "2"), new Tag("3", "3"));
    var updateTags = List.of(new Tag("1", "1"), new Tag("2", "2"));
    var result = (List<Tag>) method.invoke(tagManagementService, oldTags, updateTags);
    var expected = List.of(new Tag("3", "3"), new Tag("2", "2"));
    assertEquals(expected.size(), result.size());
    oldTags = List.of(new Tag("1", "1"), new Tag("2", "2"), new Tag("3", "3"));
    updateTags = List.of(new Tag("1", "1"), new Tag("2", "2"), new Tag("3", "3"));
    result = (List<Tag>) method.invoke(tagManagementService, oldTags, updateTags);
    expected = List.of(new Tag("3", "3"));
    assertEquals(expected.size(), result.size());
    oldTags = List.of(new Tag("1", "1"), new Tag("2", "2"), new Tag("3", "3"));
    updateTags = List.of(new Tag("1", "1"), new Tag("2", "2"), new Tag("3", "3"), new Tag("4", "4"));
    result = (List<Tag>) method.invoke(tagManagementService, oldTags, updateTags);
    expected = List.of(new Tag("4", "4"));
    assertEquals(expected.size(), result.size());
  }
  @SuppressWarnings("unchecked")
  @Test
  void testAddNewTagsWithoutDuplicates() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
    var tagManagementService = new TagManagementService();
    Method method = TagManagementService.class.getDeclaredMethod("getTrulyNewTagsWithLast", List.class, List.class);
    method.setAccessible(true);
    var oldTags = List.of(new Tag("1", "1"), new Tag("2", "2"), new Tag("3", "3"));
    var updateTags = List.of(new Tag("1", "1"), new Tag("2", "2"));
    var result = (List<Tag>) method.invoke(tagManagementService, oldTags, updateTags);
    var expected = List.of(new Tag("3", "3"), new Tag("2", "2"));
    assertEquals(expected.size(), result.size());
    oldTags = List.of(new Tag("1", "1"), new Tag("2", "2"), new Tag("3", "3"));
    updateTags = List.of(new Tag("1", "1"), new Tag("2", "2"), new Tag("3", "3"));
    result = (List<Tag>) method.invoke(tagManagementService, oldTags, updateTags);
    expected = List.of(new Tag("3", "3"));
    assertEquals(expected.size(), result.size());
    oldTags = List.of(new Tag("1", "1"), new Tag("2", "2"), new Tag("3", "3"));
    updateTags = List.of(new Tag("1", "1"), new Tag("2", "2"), new Tag("3", "3"), new Tag("4", "4"));
    result = (List<Tag>) method.invoke(tagManagementService, oldTags, updateTags);
    expected = List.of(new Tag("4", "4"));
    assertEquals(expected.size(), result.size());
  }
}
