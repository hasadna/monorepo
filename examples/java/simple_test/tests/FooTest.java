package examples.java.simple_test.tests;

import static org.junit.Assert.assertEquals;

import examples.java.simple_test.Foo;
import org.junit.Before;
import org.junit.Test;

public class FooTest {
  private Foo foo;

  @Before
  public void setup() {
    foo = new Foo();
  }

  @Test
  public void getFullNameTest() {
    assertEquals("John Smith", foo.getFullName("John", "Smith"));
  }
}

