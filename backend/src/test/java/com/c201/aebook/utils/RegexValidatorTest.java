package com.c201.aebook.utils;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.c201.aebook.utils.exception.CustomException;
import com.c201.aebook.utils.exception.ErrorCode;

@ExtendWith(MockitoExtension.class)
public class RegexValidatorTest {
	@InjectMocks
	private RegexValidator regexValidator;

	@BeforeAll
	protected static void init() {
		System.out.println("=== RegexValidator Test Init ===");
	}

	@AfterAll
	protected static void close() {
		System.out.println("=== RegexValidator Test Close ===");
	}

	@BeforeEach
	protected void setUp() throws Exception {
		System.out.println("=== Start ===");
	}

	@AfterEach
	protected void finish() throws Exception {
		System.out.println("=== Finish ===");
	}

	@Test
	@DisplayName("testValidateIsbn: Happy Case 10")
	public void testValidateIsbn1() {

		// given
		String isbn = "A123456789";

		// when
		Assertions.assertDoesNotThrow(() -> {
			regexValidator.validateIsbn(isbn);
		});

		// then
	}

	@Test
	@DisplayName("testValidateIsbn: Happy Case 13")
	public void testValidateIsbn2() {

		// given
		String isbn = "1234567891011";

		// when
		Assertions.assertDoesNotThrow(() -> {
			regexValidator.validateIsbn(isbn);
		});

		// then
	}

	@Test
	@DisplayName("testValidateIsbn: Sad Case Lower 10 Only Number")
	public void testValidateIsbn3() {

		// given
		String isbn = "123456789";

		// when
		Throwable throwable = Assertions.assertThrows(CustomException.class, () -> {
			regexValidator.validateIsbn(isbn);
		});

		// then
		Assertions.assertAll("결과값 검증", () -> {
			Assertions.assertEquals(ErrorCode.INVALID_ISBN, ((CustomException)throwable).getErrorCode());
		});
	}

	@Test
	@DisplayName("testValidateIsbn: Sad Case Lower 10 With Char")
	public void testValidateIsbn4() {

		// given
		String isbn = "A23456789";

		// when
		Throwable throwable = Assertions.assertThrows(CustomException.class, () -> {
			regexValidator.validateIsbn(isbn);
		});

		// then
		Assertions.assertAll("결과값 검증", () -> {
			Assertions.assertEquals(ErrorCode.INVALID_ISBN, ((CustomException)throwable).getErrorCode());
		});
	}

	@Test
	@DisplayName("testValidateIsbn: Sad Case 10 With Special Symbol")
	public void testValidateIsbn5() {

		// given
		String isbn = "123456789*";

		// when
		Throwable throwable = Assertions.assertThrows(CustomException.class, () -> {
			regexValidator.validateIsbn(isbn);
		});

		// then
		Assertions.assertAll("결과값 검증", () -> {
			Assertions.assertEquals(ErrorCode.INVALID_ISBN, ((CustomException)throwable).getErrorCode());
		});
	}

	@Test
	@DisplayName("testValidateIsbn: Sad Case 13 With Char")
	public void testValidateIsbn6() {

		// given
		String isbn = "1234567890sad";

		// when
		Throwable throwable = Assertions.assertThrows(CustomException.class, () -> {
			regexValidator.validateIsbn(isbn);
		});

		// then
		Assertions.assertAll("결과값 검증", () -> {
			Assertions.assertEquals(ErrorCode.INVALID_ISBN, ((CustomException)throwable).getErrorCode());
		});
	}

	@Test
	@DisplayName("testValidateIsbn: Sad Case 13 With Special Symbol")
	public void testValidateIsbn7() {

		// given
		String isbn = "123456789012*";

		// when
		Throwable throwable = Assertions.assertThrows(CustomException.class, () -> {
			regexValidator.validateIsbn(isbn);
		});

		// then
		Assertions.assertAll("결과값 검증", () -> {
			Assertions.assertEquals(ErrorCode.INVALID_ISBN, ((CustomException)throwable).getErrorCode());
		});
	}

	@Test
	@DisplayName("testValidateIsbn: Sad Case Upper 13")
	public void testValidateIsbn8() {

		// given
		String isbn = "12345678901112";

		// when
		Throwable throwable = Assertions.assertThrows(CustomException.class, () -> {
			regexValidator.validateIsbn(isbn);
		});

		// then
		Assertions.assertAll("결과값 검증", () -> {
			Assertions.assertEquals(ErrorCode.INVALID_ISBN, ((CustomException)throwable).getErrorCode());
		});
	}

	@Test
	@DisplayName("testValidateIsbn: Sad Case ISBN Null")
	public void testValidateIsbn9() {

		// given
		String isbn = null;

		// when
		Throwable throwable = Assertions.assertThrows(NullPointerException.class, () -> {
			regexValidator.validateIsbn(isbn);
		});

		// then
	}
}
