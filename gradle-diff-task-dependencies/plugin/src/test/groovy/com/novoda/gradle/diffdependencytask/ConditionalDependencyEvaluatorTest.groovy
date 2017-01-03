package com.novoda.gradle.diffdependencytask

import org.junit.Before
import org.junit.Test
import org.mockito.InjectMocks
import org.mockito.Mock

import static com.google.common.truth.Truth.assertThat
import static org.mockito.Mockito.when
import static org.mockito.MockitoAnnotations.initMocks

class ConditionalDependencyEvaluatorTest {

    @Mock
    private ConditionalDependencyRepository dependenciesRepository
    @Mock
    private GroovyCallable<List<String>> changedFilesProvider
    @InjectMocks
    private ConditionalDependencyEvaluator evaluator

    public static final ANY_TASK = null
    public static final ConditionalDependency BUILD_GRADLE_DEPENDENCY =
            new ConditionalDependency(ANY_TASK, [~'build\\.gradle'], 'banana')
    public static final ConditionalDependency ANDROID_DEPENDENCY =
            new ConditionalDependency(ANY_TASK, [~'android/.*'], 'testAndroid')
    public static final ConditionalDependency APPLE_DEPENDENCY =
            new ConditionalDependency(ANY_TASK, [~'apple/.*'], 'testApple')

    @Before
    void setUp() {
        initMocks(this)
    }

    @Test
    void whenDependenciesAreEmpty_andNoFilesWereChanged_shouldReturnNoTasks() {
        when(dependenciesRepository.getConditionalDependencies()).thenReturn([:])
        when(changedFilesProvider.call()).thenReturn([])

        Set<ConditionalDependency> actualDependencies = evaluator.call()

        assertThat(actualDependencies).isEmpty()
    }

    @Test
    void whenDependenciesAreEmpty_andSomeFilesWereChanged_shouldReturnNoTasks() {
        when(dependenciesRepository.getConditionalDependencies()).thenReturn([:])
        when(changedFilesProvider.call()).thenReturn(['build.gradle', 'src/main/kotlin/com/novoda/sample/Banana.kt'])

        Set<ConditionalDependency> actualDependencies = evaluator.call()

        assertThat(actualDependencies).isEmpty()
    }

    @Test
    void whenDependenciesAreSet_andNoFilesWereChanged_shouldReturnNoTasks() {
        when(dependenciesRepository.getConditionalDependencies()).thenReturn([
                (~'build\\.gradle'): [BUILD_GRADLE_DEPENDENCY]
        ])
        when(changedFilesProvider.call()).thenReturn([])

        Set<ConditionalDependency> actualDependencies = evaluator.call()

        assertThat(actualDependencies).isEmpty()
    }

    @Test
    void whenDependenciesAreSet_andOnlyMatchingFilesWereChanged_shouldReturnAllTasks() {
        when(dependenciesRepository.getConditionalDependencies()).thenReturn([
                (~'build\\.gradle'): [BUILD_GRADLE_DEPENDENCY]
        ])
        when(changedFilesProvider.call()).thenReturn(['build.gradle'])

        Set<ConditionalDependency> actualDependencies = evaluator.call()

        assertThat(actualDependencies).containsExactly(BUILD_GRADLE_DEPENDENCY)
    }

    @Test
    void whenDependenciesAreSet_andSomeMatchingFilesWereChanged_shouldReturnOnlyMatchingTasks() {
        when(dependenciesRepository.getConditionalDependencies()).thenReturn([
                (~'build\\.gradle'): [BUILD_GRADLE_DEPENDENCY],
                (~'android/.*')    : [ANDROID_DEPENDENCY],
                (~'apple/.*')      : [APPLE_DEPENDENCY]
        ])
        when(changedFilesProvider.call()).thenReturn(['build.gradle', 'android/settings.gradle'])

        Set<ConditionalDependency> actualDependencies = evaluator.call()

        assertThat(actualDependencies).containsExactly(BUILD_GRADLE_DEPENDENCY, ANDROID_DEPENDENCY)
    }

    @Test
    void whenDependenciesAreSet_andFilesWithSameNamesWereChangedAtDifferentPaths_shouldReturnOnlyTasksForSpecificPath() {
        when(dependenciesRepository.getConditionalDependencies()).thenReturn([
                (~'build\\.gradle'): [BUILD_GRADLE_DEPENDENCY],
                (~'android/.*')    : [ANDROID_DEPENDENCY]
        ])
        when(changedFilesProvider.call()).thenReturn(['android/build.gradle'])

        Set<ConditionalDependency> actualDependencies = evaluator.call()

        assertThat(actualDependencies).containsExactly(ANDROID_DEPENDENCY)
    }
}
