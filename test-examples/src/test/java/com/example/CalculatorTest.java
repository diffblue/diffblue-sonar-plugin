/*
 * Copyright 2026 Diffblue Limited.
 *
 * Licensed under the Apache License, Version 2.0 (the "License").
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example;

import static org.junit.jupiter.api.Assertions.*;

import com.diffblue.cover.annotations.ManagedByDiffblue;
import com.diffblue.cover.annotations.MethodsUnderTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

class CalculatorTest {
  private Calculator calculator;

  @BeforeEach
  void setUp() {
    calculator = new Calculator();
  }

  /**
   * Test {@link Calculator#add(int, int)}.
   *
   * <ul>
   *   <li>When one.
   *   <li>Then return three.
   * </ul>
   *
   * <p>Method under test: {@link Calculator#add(int, int)}
   */
  @Test
  @DisplayName("Test add(int, int); when one; then return three")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"int Calculator.add(int, int)"})
  void testAdd_whenOne_thenReturnThree() {
    // Arrange, Act and Assert
    assertEquals(3, new Calculator().add(1, 2));
  }

  /**
   * Test {@link Calculator#add(int, int)}.
   *
   * <ul>
   *   <li>When three.
   *   <li>Then return five.
   * </ul>
   *
   * <p>Method under test: {@link Calculator#add(int, int)}
   */
  @Test
  @DisplayName("Test add(int, int); when three; then return five")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"int Calculator.add(int, int)"})
  void testAdd_whenThree_thenReturnFive() {
    // Arrange, Act and Assert
    assertEquals(5, new Calculator().add(3, 2));
  }

  /**
   * Test {@link Calculator#add(int, int)}.
   *
   * <ul>
   *   <li>When two.
   *   <li>Then return four.
   * </ul>
   *
   * <p>Method under test: {@link Calculator#add(int, int)}
   */
  @Test
  @DisplayName("Test add(int, int); when two; then return four")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"int Calculator.add(int, int)"})
  void testAdd_whenTwo_thenReturnFour() {
    // Arrange, Act and Assert
    assertEquals(4, new Calculator().add(2, 2));
  }

  /**
   * Test {@link Calculator#add(int, int)}.
   *
   * <ul>
   *   <li>When zero.
   *   <li>Then return two.
   * </ul>
   *
   * <p>Method under test: {@link Calculator#add(int, int)}
   */
  @Test
  @DisplayName("Test add(int, int); when zero; then return two")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"int Calculator.add(int, int)"})
  void testAdd_whenZero_thenReturnTwo() {
    // Arrange, Act and Assert
    assertEquals(2, new Calculator().add(0, 2));
  }

  /**
   * Test {@link Calculator#subtract(int, int)}.
   *
   * <ul>
   *   <li>When minus one.
   *   <li>Then return minus four.
   * </ul>
   *
   * <p>Method under test: {@link Calculator#subtract(int, int)}
   */
  @Test
  @DisplayName("Test subtract(int, int); when minus one; then return minus four")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"int Calculator.subtract(int, int)"})
  void testSubtract_whenMinusOne_thenReturnMinusFour() {
    // Arrange, Act and Assert
    assertEquals(-4, new Calculator().subtract(-1, 3));
  }

  /**
   * Test {@link Calculator#subtract(int, int)}.
   *
   * <ul>
   *   <li>When one.
   *   <li>Then return minus two.
   * </ul>
   *
   * <p>Method under test: {@link Calculator#subtract(int, int)}
   */
  @Test
  @DisplayName("Test subtract(int, int); when one; then return minus two")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"int Calculator.subtract(int, int)"})
  void testSubtract_whenOne_thenReturnMinusTwo() {
    // Arrange, Act and Assert
    assertEquals(-2, new Calculator().subtract(1, 3));
  }

  /**
   * Test {@link Calculator#subtract(int, int)}.
   *
   * <ul>
   *   <li>When three.
   *   <li>Then return zero.
   * </ul>
   *
   * <p>Method under test: {@link Calculator#subtract(int, int)}
   */
  @Test
  @DisplayName("Test subtract(int, int); when three; then return zero")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"int Calculator.subtract(int, int)"})
  void testSubtract_whenThree_thenReturnZero() {
    // Arrange, Act and Assert
    assertEquals(0, new Calculator().subtract(3, 3));
  }

  /**
   * Test {@link Calculator#subtract(int, int)}.
   *
   * <ul>
   *   <li>When zero.
   *   <li>Then return minus three.
   * </ul>
   *
   * <p>Method under test: {@link Calculator#subtract(int, int)}
   */
  @Test
  @DisplayName("Test subtract(int, int); when zero; then return minus three")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"int Calculator.subtract(int, int)"})
  void testSubtract_whenZero_thenReturnMinusThree() {
    // Arrange, Act and Assert
    assertEquals(-3, new Calculator().subtract(0, 3));
  }

  /**
   * Test {@link Calculator#multiply(int, int)}.
   *
   * <ul>
   *   <li>When minus one.
   *   <li>Then return minus three.
   * </ul>
   *
   * <p>Method under test: {@link Calculator#multiply(int, int)}
   */
  @Test
  @DisplayName("Test multiply(int, int); when minus one; then return minus three")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"int Calculator.multiply(int, int)"})
  void testMultiply_whenMinusOne_thenReturnMinusThree() {
    // Arrange, Act and Assert
    assertEquals(-3, new Calculator().multiply(-1, 3));
  }

  /**
   * Test {@link Calculator#multiply(int, int)}.
   *
   * <ul>
   *   <li>When one.
   *   <li>Then return three.
   * </ul>
   *
   * <p>Method under test: {@link Calculator#multiply(int, int)}
   */
  @Test
  @DisplayName("Test multiply(int, int); when one; then return three")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"int Calculator.multiply(int, int)"})
  void testMultiply_whenOne_thenReturnThree() {
    // Arrange, Act and Assert
    assertEquals(3, new Calculator().multiply(1, 3));
  }

  /**
   * Test {@link Calculator#multiply(int, int)}.
   *
   * <ul>
   *   <li>When three.
   *   <li>Then return nine.
   * </ul>
   *
   * <p>Method under test: {@link Calculator#multiply(int, int)}
   */
  @Test
  @DisplayName("Test multiply(int, int); when three; then return nine")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"int Calculator.multiply(int, int)"})
  void testMultiply_whenThree_thenReturnNine() {
    // Arrange, Act and Assert
    assertEquals(9, new Calculator().multiply(3, 3));
  }

  /**
   * Test {@link Calculator#multiply(int, int)}.
   *
   * <ul>
   *   <li>When zero.
   *   <li>Then return zero.
   * </ul>
   *
   * <p>Method under test: {@link Calculator#multiply(int, int)}
   */
  @Test
  @DisplayName("Test multiply(int, int); when zero; then return zero")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"int Calculator.multiply(int, int)"})
  void testMultiply_whenZero_thenReturnZero() {
    // Arrange, Act and Assert
    assertEquals(0, new Calculator().multiply(0, 3));
  }

  /**
   * Test {@link Calculator#divide(int, int)}.
   *
   * <ul>
   *   <li>When minus one.
   *   <li>Then return zero.
   * </ul>
   *
   * <p>Method under test: {@link Calculator#divide(int, int)}
   */
  @Test
  @DisplayName("Test divide(int, int); when minus one; then return zero")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"int Calculator.divide(int, int)"})
  void testDivide_whenMinusOne_thenReturnZero() {
    // Arrange, Act and Assert
    assertEquals(0, new Calculator().divide(-1, 3));
  }

  /**
   * Test {@link Calculator#divide(int, int)}.
   *
   * <ul>
   *   <li>When one.
   *   <li>Then return zero.
   * </ul>
   *
   * <p>Method under test: {@link Calculator#divide(int, int)}
   */
  @Test
  @DisplayName("Test divide(int, int); when one; then return zero")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"int Calculator.divide(int, int)"})
  void testDivide_whenOne_thenReturnZero() {
    // Arrange, Act and Assert
    assertEquals(0, new Calculator().divide(1, 3));
  }

  /**
   * Test {@link Calculator#divide(int, int)}.
   *
   * <ul>
   *   <li>When three.
   *   <li>Then return one.
   * </ul>
   *
   * <p>Method under test: {@link Calculator#divide(int, int)}
   */
  @Test
  @DisplayName("Test divide(int, int); when three; then return one")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"int Calculator.divide(int, int)"})
  void testDivide_whenThree_thenReturnOne() {
    // Arrange, Act and Assert
    assertEquals(1, new Calculator().divide(3, 3));
  }

  /**
   * Test {@link Calculator#divide(int, int)}.
   *
   * <ul>
   *   <li>When zero.
   *   <li>Then throw {@link IllegalArgumentException}.
   * </ul>
   *
   * <p>Method under test: {@link Calculator#divide(int, int)}
   */
  @Test
  @DisplayName("Test divide(int, int); when zero; then throw IllegalArgumentException")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"int Calculator.divide(int, int)"})
  void testDivide_whenZero_thenThrowIllegalArgumentException() {
    // Arrange, Act and Assert
    assertThrows(IllegalArgumentException.class, () -> new Calculator().divide(3, 0));
  }

  /**
   * Test {@link Calculator#modulo(int, int)}.
   *
   * <ul>
   *   <li>When minus one.
   *   <li>Then return minus one.
   * </ul>
   *
   * <p>Method under test: {@link Calculator#modulo(int, int)}
   */
  @Test
  @DisplayName("Test modulo(int, int); when minus one; then return minus one")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"int Calculator.modulo(int, int)"})
  void testModulo_whenMinusOne_thenReturnMinusOne() {
    // Arrange, Act and Assert
    assertEquals(-1, new Calculator().modulo(-1, 3));
  }

  /**
   * Test {@link Calculator#modulo(int, int)}.
   *
   * <ul>
   *   <li>When one.
   *   <li>Then return one.
   * </ul>
   *
   * <p>Method under test: {@link Calculator#modulo(int, int)}
   */
  @Test
  @DisplayName("Test modulo(int, int); when one; then return one")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"int Calculator.modulo(int, int)"})
  void testModulo_whenOne_thenReturnOne() {
    // Arrange, Act and Assert
    assertEquals(1, new Calculator().modulo(1, 3));
  }

  /**
   * Test {@link Calculator#modulo(int, int)}.
   *
   * <ul>
   *   <li>When three.
   *   <li>Then return zero.
   * </ul>
   *
   * <p>Method under test: {@link Calculator#modulo(int, int)}
   */
  @Test
  @DisplayName("Test modulo(int, int); when three; then return zero")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"int Calculator.modulo(int, int)"})
  void testModulo_whenThree_thenReturnZero() {
    // Arrange, Act and Assert
    assertEquals(0, new Calculator().modulo(3, 3));
  }

  /**
   * Test {@link Calculator#modulo(int, int)}.
   *
   * <ul>
   *   <li>When zero.
   *   <li>Then return zero.
   * </ul>
   *
   * <p>Method under test: {@link Calculator#modulo(int, int)}
   */
  @Test
  @DisplayName("Test modulo(int, int); when zero; then return zero")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"int Calculator.modulo(int, int)"})
  void testModulo_whenZero_thenReturnZero() {
    // Arrange, Act and Assert
    assertEquals(0, new Calculator().modulo(0, 3));
  }

  /**
   * Test {@link Calculator#power(int, int)}.
   *
   * <ul>
   *   <li>When one.
   *   <li>Then return three.
   * </ul>
   *
   * <p>Method under test: {@link Calculator#power(int, int)}
   */
  @Test
  @DisplayName("Test power(int, int); when one; then return three")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"int Calculator.power(int, int)"})
  void testPower_whenOne_thenReturnThree() {
    // Arrange, Act and Assert
    assertEquals(3, new Calculator().power(3, 1));
  }

  /**
   * Test {@link Calculator#power(int, int)}.
   *
   * <ul>
   *   <li>When three.
   *   <li>Then return twenty-seven.
   * </ul>
   *
   * <p>Method under test: {@link Calculator#power(int, int)}
   */
  @Test
  @DisplayName("Test power(int, int); when three; then return twenty-seven")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"int Calculator.power(int, int)"})
  void testPower_whenThree_thenReturnTwentySeven() {
    // Arrange, Act and Assert
    assertEquals(27, new Calculator().power(3, 3));
  }

  /**
   * Test {@link Calculator#power(int, int)}.
   *
   * <ul>
   *   <li>When two.
   *   <li>Then return nine.
   * </ul>
   *
   * <p>Method under test: {@link Calculator#power(int, int)}
   */
  @Test
  @DisplayName("Test power(int, int); when two; then return nine")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"int Calculator.power(int, int)"})
  void testPower_whenTwo_thenReturnNine() {
    // Arrange, Act and Assert
    assertEquals(9, new Calculator().power(3, 2));
  }

  /**
   * Test {@link Calculator#power(int, int)}.
   *
   * <ul>
   *   <li>When zero.
   *   <li>Then return one.
   * </ul>
   *
   * <p>Method under test: {@link Calculator#power(int, int)}
   */
  @Test
  @DisplayName("Test power(int, int); when zero; then return one")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"int Calculator.power(int, int)"})
  void testPower_whenZero_thenReturnOne() {
    // Arrange, Act and Assert
    assertEquals(1, new Calculator().power(3, 0));
  }

  /**
   * Test {@link Calculator#factorial(int)}.
   *
   * <ul>
   *   <li>When minus one.
   *   <li>Then throw {@link IllegalArgumentException}.
   * </ul>
   *
   * <p>Method under test: {@link Calculator#factorial(int)}
   */
  @Test
  @DisplayName("Test factorial(int); when minus one; then throw IllegalArgumentException")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"int Calculator.factorial(int)"})
  void testFactorial_whenMinusOne_thenThrowIllegalArgumentException() {
    // Arrange, Act and Assert
    assertThrows(IllegalArgumentException.class, () -> new Calculator().factorial(-1));
  }

  /**
   * Test {@link Calculator#factorial(int)}.
   *
   * <ul>
   *   <li>When one.
   *   <li>Then return one.
   * </ul>
   *
   * <p>Method under test: {@link Calculator#factorial(int)}
   */
  @Test
  @DisplayName("Test factorial(int); when one; then return one")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"int Calculator.factorial(int)"})
  void testFactorial_whenOne_thenReturnOne() {
    // Arrange, Act and Assert
    assertEquals(1, new Calculator().factorial(1));
  }

  /**
   * Test {@link Calculator#factorial(int)}.
   *
   * <ul>
   *   <li>When two.
   *   <li>Then return two.
   * </ul>
   *
   * <p>Method under test: {@link Calculator#factorial(int)}
   */
  @Test
  @DisplayName("Test factorial(int); when two; then return two")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"int Calculator.factorial(int)"})
  void testFactorial_whenTwo_thenReturnTwo() {
    // Arrange, Act and Assert
    assertEquals(2, new Calculator().factorial(2));
  }

  /**
   * Test {@link Calculator#factorial(int)}.
   *
   * <ul>
   *   <li>When zero.
   *   <li>Then return one.
   * </ul>
   *
   * <p>Method under test: {@link Calculator#factorial(int)}
   */
  @Test
  @DisplayName("Test factorial(int); when zero; then return one")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"int Calculator.factorial(int)"})
  void testFactorial_whenZero_thenReturnOne() {
    // Arrange, Act and Assert
    assertEquals(1, new Calculator().factorial(0));
  }

  /**
   * Test {@link Calculator#gcd(int, int)}.
   *
   * <ul>
   *   <li>When minus one.
   *   <li>Then return minus one.
   * </ul>
   *
   * <p>Method under test: {@link Calculator#gcd(int, int)}
   */
  @Test
  @DisplayName("Test gcd(int, int); when minus one; then return minus one")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"int Calculator.gcd(int, int)"})
  void testGcd_whenMinusOne_thenReturnMinusOne() {
    // Arrange, Act and Assert
    assertEquals(-1, new Calculator().gcd(-1, 3));
  }

  /**
   * Test {@link Calculator#gcd(int, int)}.
   *
   * <ul>
   *   <li>When one.
   *   <li>Then return one.
   * </ul>
   *
   * <p>Method under test: {@link Calculator#gcd(int, int)}
   */
  @Test
  @DisplayName("Test gcd(int, int); when one; then return one")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"int Calculator.gcd(int, int)"})
  void testGcd_whenOne_thenReturnOne() {
    // Arrange, Act and Assert
    assertEquals(1, new Calculator().gcd(1, 3));
  }

  /**
   * Test {@link Calculator#gcd(int, int)}.
   *
   * <ul>
   *   <li>When three.
   *   <li>Then return three.
   * </ul>
   *
   * <p>Method under test: {@link Calculator#gcd(int, int)}
   */
  @Test
  @DisplayName("Test gcd(int, int); when three; then return three")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"int Calculator.gcd(int, int)"})
  void testGcd_whenThree_thenReturnThree() {
    // Arrange, Act and Assert
    assertEquals(3, new Calculator().gcd(3, 3));
  }

  /**
   * Test {@link Calculator#gcd(int, int)}.
   *
   * <ul>
   *   <li>When zero.
   *   <li>Then return three.
   * </ul>
   *
   * <p>Method under test: {@link Calculator#gcd(int, int)}
   */
  @Test
  @DisplayName("Test gcd(int, int); when zero; then return three")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"int Calculator.gcd(int, int)"})
  void testGcd_whenZero_thenReturnThree() {
    // Arrange, Act and Assert
    assertEquals(3, new Calculator().gcd(3, 0));
  }

  /**
   * Test {@link Calculator#lcm(int, int)}.
   *
   * <ul>
   *   <li>When minus one.
   *   <li>Then return three.
   * </ul>
   *
   * <p>Method under test: {@link Calculator#lcm(int, int)}
   */
  @Test
  @DisplayName("Test lcm(int, int); when minus one; then return three")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"int Calculator.lcm(int, int)"})
  void testLcm_whenMinusOne_thenReturnThree() {
    // Arrange, Act and Assert
    assertEquals(3, new Calculator().lcm(-1, 3));
  }

  /**
   * Test {@link Calculator#lcm(int, int)}.
   *
   * <ul>
   *   <li>When one.
   *   <li>Then return three.
   * </ul>
   *
   * <p>Method under test: {@link Calculator#lcm(int, int)}
   */
  @Test
  @DisplayName("Test lcm(int, int); when one; then return three")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"int Calculator.lcm(int, int)"})
  void testLcm_whenOne_thenReturnThree() {
    // Arrange, Act and Assert
    assertEquals(3, new Calculator().lcm(1, 3));
  }

  /**
   * Test {@link Calculator#lcm(int, int)}.
   *
   * <ul>
   *   <li>When three.
   *   <li>Then return three.
   * </ul>
   *
   * <p>Method under test: {@link Calculator#lcm(int, int)}
   */
  @Test
  @DisplayName("Test lcm(int, int); when three; then return three")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"int Calculator.lcm(int, int)"})
  void testLcm_whenThree_thenReturnThree() {
    // Arrange, Act and Assert
    assertEquals(3, new Calculator().lcm(3, 3));
  }

  /**
   * Test {@link Calculator#lcm(int, int)}.
   *
   * <ul>
   *   <li>When zero.
   *   <li>Then return zero.
   * </ul>
   *
   * <p>Method under test: {@link Calculator#lcm(int, int)}
   */
  @Test
  @DisplayName("Test lcm(int, int); when zero; then return zero")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"int Calculator.lcm(int, int)"})
  void testLcm_whenZero_thenReturnZero() {
    // Arrange, Act and Assert
    assertEquals(0, new Calculator().lcm(3, 0));
  }

  @Test
  @DisplayName("isPrime should return false for numbers less than or equal to 1")
  void testIsPrime_NumbersLessThanOrEqualToOne() {
    assertFalse(calculator.isPrime(-10), "Negative numbers should not be prime");
    assertFalse(calculator.isPrime(-1), "-1 should not be prime");
    assertFalse(calculator.isPrime(0), "0 should not be prime");
    assertFalse(calculator.isPrime(1), "1 should not be prime");
  }

  @Test
  @DisplayName("isPrime should return true for 2 (the smallest prime)")
  void testIsPrime_Two() {
    assertTrue(calculator.isPrime(2), "2 should be prime");
  }

  /**
   * Test {@link Calculator#isPrime(int)}.
   *
   * <ul>
   *   <li>When five.
   *   <li>Then return {@code true}.
   * </ul>
   *
   * <p>Method under test: {@link Calculator#isPrime(int)}
   */
  @Test
  @DisplayName("Test isPrime(int); when five; then return 'true'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean Calculator.isPrime(int)"})
  void testIsPrime_whenFive_thenReturnTrue() {
    // Arrange, Act and Assert
    assertTrue(new Calculator().isPrime(5));
  }

  /**
   * Test {@link Calculator#isPrime(int)}.
   *
   * <ul>
   *   <li>When four.
   *   <li>Then return {@code false}.
   * </ul>
   *
   * <p>Method under test: {@link Calculator#isPrime(int)}
   */
  @Test
  @DisplayName("Test isPrime(int); when four; then return 'false'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean Calculator.isPrime(int)"})
  void testIsPrime_whenFour_thenReturnFalse() {
    // Arrange, Act and Assert
    assertFalse(new Calculator().isPrime(4));
  }

  /**
   * Test {@link Calculator#isPrime(int)}.
   *
   * <ul>
   *   <li>When one.
   *   <li>Then return {@code false}.
   * </ul>
   *
   * <p>Method under test: {@link Calculator#isPrime(int)}
   */
  @Test
  @DisplayName("Test isPrime(int); when one; then return 'false'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean Calculator.isPrime(int)"})
  void testIsPrime_whenOne_thenReturnFalse() {
    // Arrange, Act and Assert
    assertFalse(new Calculator().isPrime(1));
  }

  /**
   * Test {@link Calculator#isPrime(int)}.
   *
   * <ul>
   *   <li>When two.
   *   <li>Then return {@code true}.
   * </ul>
   *
   * <p>Method under test: {@link Calculator#isPrime(int)}
   */
  @Test
  @DisplayName("Test isPrime(int); when two; then return 'true'")
  @Tag("ContributionFromDiffblue")
  @ManagedByDiffblue
  @MethodsUnderTest({"boolean Calculator.isPrime(int)"})
  void testIsPrime_whenTwo_thenReturnTrue() {
    // Arrange, Act and Assert
    assertTrue(new Calculator().isPrime(2));
  }

  @Test
  @DisplayName("isPrime should return true for small prime numbers")
  void testIsPrime_SmallPrimes() {
    assertTrue(calculator.isPrime(3), "3 should be prime");
    assertTrue(calculator.isPrime(5), "5 should be prime");
    assertTrue(calculator.isPrime(7), "7 should be prime");
    assertTrue(calculator.isPrime(11), "11 should be prime");
    assertTrue(calculator.isPrime(13), "13 should be prime");
    assertTrue(calculator.isPrime(17), "17 should be prime");
    assertTrue(calculator.isPrime(19), "19 should be prime");
    assertTrue(calculator.isPrime(23), "23 should be prime");
    assertTrue(calculator.isPrime(29), "29 should be prime");
  }

  @Test
  @DisplayName("isPrime should return false for small composite numbers")
  void testIsPrime_SmallComposites() {
    assertFalse(calculator.isPrime(4), "4 should not be prime");
    assertFalse(calculator.isPrime(6), "6 should not be prime");
    assertFalse(calculator.isPrime(8), "8 should not be prime");
    assertFalse(calculator.isPrime(9), "9 should not be prime");
    assertFalse(calculator.isPrime(10), "10 should not be prime");
    assertFalse(calculator.isPrime(12), "12 should not be prime");
    assertFalse(calculator.isPrime(15), "15 should not be prime");
    assertFalse(calculator.isPrime(21), "21 should not be prime");
    assertFalse(calculator.isPrime(25), "25 should not be prime");
  }

  @Test
  @DisplayName("isPrime should return true for larger prime numbers")
  void testIsPrime_LargerPrimes() {
    assertTrue(calculator.isPrime(31), "31 should be prime");
    assertTrue(calculator.isPrime(37), "37 should be prime");
    assertTrue(calculator.isPrime(41), "41 should be prime");
    assertTrue(calculator.isPrime(43), "43 should be prime");
    assertTrue(calculator.isPrime(47), "47 should be prime");
    assertTrue(calculator.isPrime(97), "97 should be prime");
  }

  @Test
  @DisplayName("isPrime should return false for larger composite numbers")
  void testIsPrime_LargerComposites() {
    assertFalse(calculator.isPrime(49), "49 (7*7) should not be prime");
    assertFalse(calculator.isPrime(51), "51 (3*17) should not be prime");
    assertFalse(calculator.isPrime(100), "100 should not be prime");
    assertFalse(calculator.isPrime(121), "121 (11*11) should not be prime");
  }

  @Test
  @DisplayName("isPrime should handle perfect squares correctly")
  void testIsPrime_PerfectSquares() {
    assertFalse(calculator.isPrime(4), "4 (2*2) should not be prime");
    assertFalse(calculator.isPrime(9), "9 (3*3) should not be prime");
    assertFalse(calculator.isPrime(16), "16 (4*4) should not be prime");
    assertFalse(calculator.isPrime(25), "25 (5*5) should not be prime");
    assertFalse(calculator.isPrime(36), "36 (6*6) should not be prime");
    assertFalse(calculator.isPrime(49), "49 (7*7) should not be prime");
    assertFalse(calculator.isPrime(64), "64 (8*8) should not be prime");
    assertFalse(calculator.isPrime(81), "81 (9*9) should not be prime");
  }
}
