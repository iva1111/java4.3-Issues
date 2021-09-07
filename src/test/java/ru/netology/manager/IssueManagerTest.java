package ru.netology.manager;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.netology.domain.Issue;
import ru.netology.domain.NotFoundException;
import ru.netology.repository.IssueRepository;

import java.util.Collection;
import java.util.List;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
class CRUDIssueManagerTest {
    private IssueRepository repository = new IssueRepository();
    private IssueManager manager = new IssueManager(repository);
    private Issue issue1 = new Issue(1, true, "Anna", Set.of("bug", "status"), "Olga", 8);
    private Issue issue2 = new Issue(2, false, "Anna", Set.of("bug", "test"), "Olga", 12);
    private Issue issue3 = new Issue(3, true, "Pavel", Set.of("bug", "status", "test"), "Olga", 10);
    private Issue issue4 = new Issue(4, false, "Rita", Set.of("bug"), "Olga", 18);
    private Issue issue5 = new Issue(5, true, "Anna", Set.of("bug", "status"), "Olga", 23);
    private Issue issue6 = new Issue(6, false, "Nik", Set.of("bug", "open"), "Artur", 20);
    private Issue issue7 = new Issue(7, true, "Anna", Set.of("new"), "Artur", 30);

    @Nested
    class WhenRepoIsEmpty {
        @Test
        void addIssues() {
            manager.add(issue1);
            Collection<Issue> expected = List.of(issue1);
            Collection<Issue> actual = repository.getAll();
            Assertions.assertIterableEquals(expected, actual);
        }

        @Test
        void getAllOpen() {
            Collection<Issue> expected = List.of();
            Collection<Issue> actual = manager.getAllOpen();
            Assertions.assertIterableEquals(expected, actual);
        }

        @Test
        void getAllClosed() {
            Collection<Issue> expected = List.of();
            Collection<Issue> actual = manager.getAllClosed();
            Assertions.assertIterableEquals(expected, actual);
        }
    }

    @Nested
    class WhenOneInside {
        @BeforeEach
        void setUp() {
            manager.add(issue1);
        }

        @Test
        void addIssues() {
            manager.add(issue2);
            Collection<Issue> expected = List.of(issue1, issue2);
            Collection<Issue> actual = manager.getAll();
            Assertions.assertIterableEquals(expected, actual);
        }

        @Test
        void getAllOpen() {
            Collection<Issue> expected = List.of(issue1);
            Collection<Issue> actual = manager.getAllOpen();
            Assertions.assertIterableEquals(expected, actual);
        }

        @Test
        void getAllClosed() {
            Collection<Issue> expected = List.of();
            Collection<Issue> actual = manager.getAllClosed();
            Assertions.assertIterableEquals(expected, actual);
        }
    }

    @Nested
    public class WhenMoreThenOneInside {

        @BeforeEach
        void setUp() {
            manager.add(issue1);
            manager.add(issue2);
            manager.add(issue3);
            manager.add(issue4);
            manager.add(issue5);
            manager.add(issue6);
        }

        @Test
        void shouldAdd() {
            manager.add(issue7);
            Collection<Issue> expected = List.of(issue1, issue2, issue3, issue4, issue5, issue6, issue7);
            Collection<Issue> actual = manager.getAll();
            Assertions.assertIterableEquals(expected, actual);
        }

        @Test
        void shouldGetAll() {
            Collection<Issue> expexted = List.of(issue1, issue2, issue3, issue4, issue5, issue6);
            Collection<Issue> actual = manager.getAll();
            Assertions.assertIterableEquals(expexted, actual);
        }

        @Test
        void shouldRemoveWhenExist() {
            manager.remove(issue3);
            Collection<Issue> expexted = List.of(issue1, issue2, issue4, issue5, issue6);
            Collection<Issue> actual = manager.getAll();
            Assertions.assertIterableEquals(expexted, actual);
        }

        @Test
        void shouldGetAllOpen() {
            Collection<Issue> expected = List.of(issue1, issue3, issue5);
            Collection<Issue> actual = manager.getAllOpen();
            Assertions.assertIterableEquals(expected, actual);
        }

        @Test
        void shouldGetAllClosed() {
            Collection<Issue> expected = List.of(issue2, issue4, issue6);
            Collection<Issue> actual = manager.getAllClosed();
            Assertions.assertIterableEquals(expected, actual);
        }

        @Test
        void shouldFilterByAuthorWhenExist() {
            Collection<Issue> expected = List.of(issue1, issue2, issue5);
            Collection<Issue> actual = manager.filterByAuthor("Anna");
            Assertions.assertIterableEquals(expected, actual);
        }

        @Test
        void shouldFilterByAuthorWhenNotExist() {
            Collection<Issue> expected = List.of();
            Collection<Issue> actual = manager.filterByAuthor("Olga");
            Assertions.assertIterableEquals(expected, actual);
        }

        @Test
        void shouldFilterByALabelWhenExist() {
            Collection<Issue> expected = List.of(issue1, issue3, issue5);
            Collection<Issue> actual = manager.filterByLabel(Set.of("bug", "status"));
            Assertions.assertIterableEquals(expected, actual);
        }

        @Test
        void shouldFilterByLabelWhenNotExist() {
            Collection<Issue> expected = List.of();
            Collection<Issue> actual = manager.filterByLabel(Set.of("Nik"));
            Assertions.assertIterableEquals(expected, actual);
        }

        @Test
        void shouldFilterByAssigneeWhenExist() {
            Collection<Issue> expected = List.of(issue1, issue2, issue3, issue4, issue5);
            Collection<Issue> actual = manager.filterByAssignee("Olga");
            Assertions.assertIterableEquals(expected, actual);
        }

        @Test
        void shouldFilterByAssigneeWhenNotExist() {
            Collection<Issue> expected = List.of();
            Collection<Issue> actual = manager.filterByAssignee("Adam");
            Assertions.assertIterableEquals(expected, actual);
        }

        @Test
        void shouldUpdateWhenOpen() {
            manager.updateIssue(3);
            boolean actual = issue3.isOpen();
            Assertions.assertFalse(actual);
        }

        @Test
        void shouldUpdateWhenClosed() {
            manager.updateIssue(4);
            boolean actual = issue4.isOpen();
            Assertions.assertTrue(actual);
        }

        @Test
        void shouldUpdateWhenNotExist() {
            Assertions.assertThrows(NotFoundException.class, () -> manager.updateIssue(7));
        }

        @Test
        void shouldSortByCreateDaysAgo() {
            Collection<Issue> expected = List.of(issue1, issue3, issue2, issue4, issue6, issue5);
            Collection<Issue> actual = manager.sortByCreateDaysAgo();
            Assertions.assertIterableEquals(expected, actual);
        }
    }
}
