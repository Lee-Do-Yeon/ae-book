package com.c201.aebook.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Component;

import com.c201.aebook.utils.exception.CustomException;
import com.c201.aebook.utils.exception.ErrorCode;

@Component
public class RegexValidator {
	/**
	 * [문희주] ISBN 정규 표현식 검사
	 * @param isbn
	 */
	public void validateIsbn(String isbn) {
		if (isbn == null) {
			throw new NullPointerException();
		}

		Pattern pattern = Pattern.compile("^[A-Za-z0-9]{10}$|^\\d{13}$");
		Matcher matcher = pattern.matcher((isbn));

		if (!matcher.find()) {
			throw new CustomException(ErrorCode.INVALID_ISBN);
		}
	}
}
