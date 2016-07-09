package com.novoda.buildproperties

import com.google.common.io.Resources
import org.junit.Before
import org.junit.Test

import static com.google.common.truth.Truth.assertThat
import static org.junit.Assert.fail

public class FilePropertiesEntriesTest {

  private static final File PROPERTIES_FILE = new File(Resources.getResource('any.properties').toURI())

  private FilePropertiesEntries entries

  @Before
  public void setUp() {
    entries = FilePropertiesEntries.create(PROPERTIES_FILE)
  }

  @Test
  public void shouldNotAccessPropertyValueWhenGettingEntry() {
    try {
      BuildProperties.Entry entry = entries['notThere']
    } catch (IllegalArgumentException ignored) {
      fail('Entry value should be evaluated lazily')
    }
  }

  @Test
  public void shouldCheckPropertyExistenceInstantly() {
    assertThat(entries.contains('notThere')).isFalse()
    assertThat(entries.contains('aProperty')).isTrue()
  }

  @Test
  public void shouldRetrieveValueWhenPropertyDefined() {
    def value = entries['aProperty'].value

    assertThat(value).isEqualTo('qwerty')
  }

  @Test
  public void shouldThrowIllegalArgumentExceptionWhenTryingToAccessValueOfUndefinedProperty() {
    try {
      entries['notThere'].value
      fail('IllegalArgumentException expected')
    } catch (IllegalArgumentException e) {
      assertThat(e.getMessage()).startsWith("No value defined for property 'notThere'")
    }
  }

  @Test
  public void shouldConvertToTrueBooleanWhenPropertyDefinedAsTrue() {
    Boolean value = entries['positive'].boolean
    assertThat(value).isTrue()
  }

  @Test
  public void shouldConvertToFalseWhenPropertyNotDefinedAsBoolean() {
    Boolean value = entries['string'].boolean
    assertThat(value).isFalse()
  }

  @Test
  public void shouldThrowNumberFormatExceptionWhenPropertyNotDefinedAsInteger() {
    try {
      entries['string'].int
    } catch (NumberFormatException e) {
      assertThat(e.getMessage()).contains('"hello world"')
    }
  }

  @Test
  public void shouldConvertToIntegerWhenPropertyDefinedAsInteger() {
    Integer value = entries['int'].int
    assertThat(value).isEqualTo(123456)
  }

  @Test
  public void shouldThrowNumberFormatExceptionWhenPropertyNotDefinedAsDouble() {
    try {
      entries['string'].double
    } catch (NumberFormatException e) {
      assertThat(e.getMessage()).contains('"hello world"')
    }
  }

  @Test
  public void shouldConvertToDoubleWhenPropertyDefinedAsDouble() {
    Double value = entries['double'].double
    assertThat(value).isEqualTo(0.001 as Double)
  }

  @Test
  public void shouldEnumerateAllKeysInPropertiesFile() {
    assertThat(Collections.list(entries.keys)).containsExactly('aProperty', 'another_PROPERTY', 'api.key', 'negative', 'positive', 'int', 'double', 'string')
  }

}
