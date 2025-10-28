package io.spring.graphql;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.netflix.graphql.dgs.DgsQueryExecutor;
import io.spring.application.TagsQueryService;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;

@SpringBootTest
public class TagDatafetcherTest {

  @Autowired DgsQueryExecutor dgsQueryExecutor;

  @MockBean private TagsQueryService tagsQueryService;

  private List<String> tags;

  @BeforeEach
  public void setUp() {
    tags = Arrays.asList("java", "spring", "graphql", "testing", "dgs");
  }

  @Test
  public void should_get_all_tags_successfully() {
    when(tagsQueryService.allTags()).thenReturn(tags);

    String query = "query { tags }";

    List<String> result = dgsQueryExecutor.executeAndExtractJsonPath(query, "data.tags");
    assertThat(result).isNotNull();
    assertThat(result).hasSize(5);
    assertThat(result).containsExactlyInAnyOrder("java", "spring", "graphql", "testing", "dgs");
    verify(tagsQueryService).allTags();
  }

  @Test
  public void should_get_empty_tags_list() {
    when(tagsQueryService.allTags()).thenReturn(Collections.emptyList());

    String query = "query {" +
        "  tags" +
        "}";

    List<String> result = dgsQueryExecutor.executeAndExtractJsonPath(query, "data.tags");
    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
    verify(tagsQueryService).allTags();
  }

  @Test
  public void should_get_single_tag() {
    when(tagsQueryService.allTags()).thenReturn(Arrays.asList("java"));

    String query = "query {" +
        "  tags" +
        "}";

    List<String> result = dgsQueryExecutor.executeAndExtractJsonPath(query, "data.tags");
    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result).contains("java");
    verify(tagsQueryService).allTags();
  }

  @Test
  public void should_handle_tags_with_special_characters() {
    List<String> specialTags = Arrays.asList("c++", "c#", ".net", "node.js", "vue.js");
    when(tagsQueryService.allTags()).thenReturn(specialTags);

    String query = "query {" +
        "  tags" +
        "}";

    List<String> result = dgsQueryExecutor.executeAndExtractJsonPath(query, "data.tags");
    assertThat(result).isNotNull();
    assertThat(result).hasSize(5);
    assertThat(result).containsExactlyInAnyOrder("c++", "c#", ".net", "node.js", "vue.js");
    verify(tagsQueryService).allTags();
  }

  @Test
  public void should_handle_duplicate_tags() {
    List<String> duplicateTags = Arrays.asList("java", "spring", "java", "spring", "testing");
    when(tagsQueryService.allTags()).thenReturn(duplicateTags);

    String query = "query {" +
        "  tags" +
        "}";

    List<String> result = dgsQueryExecutor.executeAndExtractJsonPath(query, "data.tags");
    assertThat(result).isNotNull();
    assertThat(result).hasSize(5);
    assertThat(result).containsExactly("java", "spring", "java", "spring", "testing");
    verify(tagsQueryService).allTags();
  }

  @Test
  public void should_handle_long_tag_names() {
    List<String> longTags = Arrays.asList("very-long-tag-name-with-many-characters", "short", "medium-length-tag");
    when(tagsQueryService.allTags()).thenReturn(longTags);

    String query = "query {" +
        "  tags" +
        "}";

    List<String> result = dgsQueryExecutor.executeAndExtractJsonPath(query, "data.tags");
    assertThat(result).isNotNull();
    assertThat(result).hasSize(3);
    assertThat(result).containsExactlyInAnyOrder("very-long-tag-name-with-many-characters", "short", "medium-length-tag");
    verify(tagsQueryService).allTags();
  }
}
