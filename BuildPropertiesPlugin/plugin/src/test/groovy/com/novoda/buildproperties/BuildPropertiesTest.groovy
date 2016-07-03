package com.novoda.buildproperties

import com.google.common.io.Resources
import org.junit.Before
import org.junit.Test

import static com.google.common.truth.Truth.assertThat
import static org.junit.Assert.fail

public class BuildPropertiesTest {

  private static final File PROPERTIES_FILE = new File(Resources.getResource('any.properties').toURI())

  private BuildProperties buildProperties

  @Before
  public void setUp() {
    buildProperties = BuildProperties.create('any', PROPERTIES_FILE)
  }

  @Test
  public void shouldAccessPropertiesLazily() {
    BuildProperties.Entry entry1 = buildProperties['notThere']
    BuildProperties.Entry entry2 = buildProperties['aProperty']
  }

  @Test
  public void shouldRetrieveValueWhenPropertyDefined() {
    def value = buildProperties['aProperty'].value

    assertThat(value).isEqualTo('qwerty')
  }

  @Test
  public void shouldThrowIllegalArgumentExceptionWhenTryingToAccessValueOfUndefinedProperty() {
    try {
      buildProperties['notThere'].value
      fail('IllegalArgumentException expected')
    } catch (IllegalArgumentException e) {
      assertThat(e.getMessage()).startsWith("No value defined for property 'notThere'")
    }
  }

  @Test
  public void shouldConvertToTrueBooleanWhenPropertyDefinedAsTrue() {
    Boolean value = buildProperties['positive'].boolean
    assertThat(value).isTrue()
  }

  @Test
  public void shouldConvertToFalseWhenPropertyNotDefinedAsBoolean() {
    Boolean value = buildProperties['string'].boolean
    assertThat(value).isFalse()
  }

  @Test
  public void shouldThrowNumberFormatExceptionWhenPropertyNotDefinedAsInteger() {
    try {
      buildProperties['string'].int
    } catch (NumberFormatException e) {
      assertThat(e.getMessage()).contains('"hello world"')
    }
  }

  @Test
  public void shouldConvertToIntegerWhenPropertyDefinedAsInteger() {
    Integer value = buildProperties['int'].int
    assertThat(value).isEqualTo(123456)
  }

  @Test
  public void shouldThrowNumberFormatExceptionWhenPropertyNotDefinedAsDouble() {
    try {
      buildProperties['string'].double
    } catch (NumberFormatException e) {
      assertThat(e.getMessage()).contains('"hello world"')
    }
  }

  @Test
  public void shouldConvertToDoubleWhenPropertyDefinedAsDouble() {
    Double value = buildProperties['double'].double
    assertThat(value).isEqualTo(0.001 as Double)
  }

}
