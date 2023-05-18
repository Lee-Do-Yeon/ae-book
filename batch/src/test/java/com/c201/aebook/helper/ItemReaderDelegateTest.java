package com.c201.aebook.helper;

import static org.mockito.Mockito.*;

import java.awt.print.Book;
import java.io.IOException;
import java.sql.Date;
import java.util.Optional;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.c201.aebook.api.book.persistence.entity.BookEntity;

@ExtendWith(MockitoExtension.class)
class ItemReaderDelegateTest {

	@Mock
	private RestTemplate restTemplate;

	@InjectMocks
	private ItemReaderDelegate subject;

	@BeforeEach
	protected void setUp() throws Exception {
		ReflectionTestUtils.setField(subject, "restTemplate", restTemplate);
	}


	@Test
	@DisplayName("happy case")
	public void testReplaceCharacterFromText(){
		//given
		String input = "This is a &quot;test&quot; string.";
		String expectedOutput = "This is a \"test\" string.";

		//when
		String output = subject.replaceCharacterFromText(input);


		//then
		Assertions.assertEquals(output, expectedOutput);
	}



	@Test
	public void testGetItemElementByUrl() throws ParserConfigurationException, IOException, SAXException {
		//given
		String dummyUrl = "http://example.com";
		String dummyResponse = "<response><item><subInfo>Some information</subInfo></item></response>";
		String dummyTagName = "item";

		//mocking restTemplate.exchange 메서드
		ResponseEntity<String> responseEntityMock = mock(ResponseEntity.class);
		when(responseEntityMock.getBody()).thenReturn(dummyResponse);
		when(restTemplate.exchange(eq(dummyUrl), eq(HttpMethod.GET), isNull(), eq(String.class)))
			.thenReturn(responseEntityMock);


		//when
		NodeList result = subject.getElementsByUrl(UriComponentsBuilder.fromHttpUrl(dummyUrl), dummyTagName);

		//then
		Assertions.assertNotNull(result);

		Node itemNode = result.item(0);
		Assertions.assertEquals("Some information", itemNode.getTextContent());
	}

	@Test
	@DisplayName("happy case")
	public void testParseBook() throws Exception{
		//given
		Node itemNode = createMockItemNode();
		String dummyResponse = "<item itemId=\"249297227\">\n" +
			"    <title>[중고] 주린이도 술술 읽는 친절한 주식책</title>\n" +
			"    <link>http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=249297227&amp;partner=openAPI&amp;start=api</link>\n" +
			"    <author>최정희, 이슬기 (지은이)</author>\n" +
			"    <pubDate>2020-09-01</pubDate>\n" +
			"    <description>주식을 막 시작해서 모든 것이 막막한 사람들에게 든든한 길라잡이 역할을 한다. 주식이 여전히 어려운 주린이들이 투자에 본격적으로 나서기 전에 꼭 알아야 할 최소한의 필수 지식을 엄선해 술술 풀어냈다.</description>\n" +
			"    <isbn>K912632497</isbn>\n" +
			"    <isbn13>9791160022988</isbn13>\n" +
			"    <priceSales>13500</priceSales>\n" +
			"    <cover>https://image.aladin.co.kr/product/24929/72/coversum/k912632497_1.jpg</cover>\n" +
			"    <publisher>메이트북스</publisher>\n" +
			"    <adult>false</adult>\n" +
			"    <fixedPrice>true</fixedPrice>\n" +
			"    <customerReviewRank>9</customerReviewRank>\n" +
			"    <subInfo>\n" +
			"        <subTitle/>\n" +
			"        <originalTitle/>\n" +
			"        <itemPage>308</itemPage>\n" +
			"    </subInfo>\n" +
			"</item>";
		ResponseEntity<String> mockResponse = ResponseEntity.ok().body(dummyResponse);
		BDDMockito.when(restTemplate.exchange(Mockito.anyString(), Mockito.eq(HttpMethod.GET), Mockito.any(), Mockito.eq(String.class)))
			.thenReturn(mockResponse);

		//when
		Optional<BookEntity> result = subject.getParsing(itemNode);

		//then
		Assertions.assertTrue(result.isPresent());

		BookEntity book = result.get();

		Assertions.assertEquals("[중고] 주린이도 술술 읽는 친절한 주식책", book.getTitle());
		Assertions.assertEquals("최정희, 이슬기 (지은이)", book.getAuthor());
		Assertions.assertEquals("메이트북스", book.getPublisher());
		Assertions.assertEquals("https://image.aladin.co.kr/product/24929/72/coversum/k912632497_1.jpg", book.getCoverImageUrl());

	}


	/*
	 * 테스트를 위한 가상의 node 데이터를 만듬
	 * */
	private Node createMockItemNode() throws ParserConfigurationException {
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = factory.newDocumentBuilder();
		Document document = builder.newDocument();

		// item 요소
		Element itemElement = document.createElement("item");
		itemElement.setAttribute("itemId", "316982043");

		// title 요소
		Element titleElement = document.createElement("title");
		titleElement.setTextContent("[중고] 주린이도 술술 읽는 친절한 주식책");
		itemElement.appendChild(titleElement);

		// link 요소
		Element linkElement = document.createElement("link");
		linkElement.setTextContent("http://www.aladin.co.kr/shop/wproduct.aspx?ItemId=316982043&partner=openAPI&start=api");
		itemElement.appendChild(linkElement);

		// author 요소
		Element authorElement = document.createElement("author");
		authorElement.setTextContent("최정희, 이슬기 (지은이)");
		itemElement.appendChild(authorElement);

		// pubDate 요소
		Element pubDateElement = document.createElement("pubDate");
		pubDateElement.setTextContent("2020-09-01");
		itemElement.appendChild(pubDateElement);

		// description 요소
		Element descriptionElement = document.createElement("description");
		descriptionElement.setTextContent("주식을 막 시작해서 모든 것이 막막한 사람들에게 든든한 길라잡이 역할을 한다. 주식이 여전히 어려운 주린이들이 투자에 본격적으로 나서기 전에 꼭 알아야 할 최소한의 필수 지식을 엄선해 술술 풀어냈다.");
		itemElement.appendChild(descriptionElement);

		// isbn 요소
		Element isbnElement = document.createElement("isbn");
		isbnElement.setTextContent("U134901717");
		itemElement.appendChild(isbnElement);

		// priceSales 요소
		Element priceSalesElement = document.createElement("priceSales");
		priceSalesElement.setTextContent("7000");
		itemElement.appendChild(priceSalesElement);

		// priceStandard 요소
		Element priceStandardElement = document.createElement("priceStandard");
		priceStandardElement.setTextContent("15000");
		itemElement.appendChild(priceStandardElement);

		// cover 요소
		Element coverElement = document.createElement("cover");
		coverElement.setTextContent("https://image.aladin.co.kr/product/24929/72/coversum/k912632497_1.jpg");
		itemElement.appendChild(coverElement);

		// publisher 요소
		Element publisherElement = document.createElement("publisher");
		publisherElement.setTextContent("메이트북스");
		itemElement.appendChild(publisherElement);

		// adult 요소
		Element adultElement = document.createElement("adult");
		adultElement.setTextContent("false");
		itemElement.appendChild(adultElement);


		// subInfo 요소
		Element subInfoElement = document.createElement("subInfo");
		itemElement.appendChild(subInfoElement);

		// usedType 요소
		Element usedTypeElement = document.createElement("usedType");
		usedTypeElement.setTextContent("userUsed");
		subInfoElement.appendChild(usedTypeElement);

		// newBookList 요소
		Element newBookListElement = document.createElement("newBookList");
		subInfoElement.appendChild(newBookListElement);

		// newBook 요소
		Element newBookElement = document.createElement("newBook");
		newBookListElement.appendChild(newBookElement);

		// itemId 요소
		Element newBookItemIdElement = document.createElement("itemId");
		newBookItemIdElement.setTextContent("249297227");
		newBookElement.appendChild(newBookItemIdElement);

		// isbn 요소
		Element newBookIsbnElement = document.createElement("isbn");
		newBookIsbnElement.setTextContent("K912632497");
		newBookElement.appendChild(newBookIsbnElement);

		// priceSales 요소
		Element newBookPriceSalesElement = document.createElement("priceSales");
		newBookPriceSalesElement.setTextContent("13500");
		newBookElement.appendChild(newBookPriceSalesElement);

		// link 요소
		Element newBookLinkElement = document.createElement("link");
		newBookLinkElement.setTextContent("https://www.aladin.co.kr/shop/wproduct.aspx?ItemId=249297227&partner=openAPI");
		newBookElement.appendChild(newBookLinkElement);

		// usedList 요소
		Element usedListElement = document.createElement("usedList");
		subInfoElement.appendChild(usedListElement);

		// aladinUsed 요소
		Element aladinUsedElement = document.createElement("aladinUsed");
		usedListElement.appendChild(aladinUsedElement);

		// itemCount 요소
		Element aladinUsedItemCountElement = document.createElement("itemCount");
		aladinUsedItemCountElement.setTextContent("2");
		aladinUsedElement.appendChild(aladinUsedItemCountElement);

		// minPrice 요소
		Element aladinUsedMinPriceElement = document.createElement("minPrice");
		aladinUsedMinPriceElement.setTextContent("9600");
		aladinUsedElement.appendChild(aladinUsedMinPriceElement);

		// link 요소
		Element aladinUsedLinkElement = document.createElement("link");
		// Type=2&partner=openAPI
		aladinUsedLinkElement.setTextContent("https://www.aladin.co.kr/shop/UsedShop/wuseditemall.aspx?ItemId=249297227&TabType=2&partner=openAPI");
		aladinUsedElement.appendChild(aladinUsedLinkElement);

		// userUsed 요소
		Element userUsedElement = document.createElement("userUsed");
		usedListElement.appendChild(userUsedElement);

		// itemCount 요소
		Element userUsedItemCountElement = document.createElement("itemCount");
		userUsedItemCountElement.setTextContent("83");
		userUsedElement.appendChild(userUsedItemCountElement);

		// minPrice 요소
		Element userUsedMinPriceElement = document.createElement("minPrice");
		userUsedMinPriceElement.setTextContent("5400");
		userUsedElement.appendChild(userUsedMinPriceElement);

		// link 요소
		Element userUsedLinkElement = document.createElement("link");
		userUsedLinkElement.setTextContent("https://www.aladin.co.kr/shop/UsedShop/wuseditemall.aspx?ItemId=249297227&TabType=1&partner=openAPI");
		userUsedElement.appendChild(userUsedLinkElement);

		// spaceUsed 요소
		Element spaceUsedElement = document.createElement("spaceUsed");
		usedListElement.appendChild(spaceUsedElement);

		// itemCount 요소
		Element spaceUsedItemCountElement = document.createElement("itemCount");
		spaceUsedItemCountElement.setTextContent("15");
		spaceUsedElement.appendChild(spaceUsedItemCountElement);

		// minPrice 요소
		Element spaceUsedMinPriceElement = document.createElement("minPrice");
		spaceUsedMinPriceElement.setTextContent("8800");
		spaceUsedElement.appendChild(spaceUsedMinPriceElement);

		// link 요소
		Element spaceUsedLinkElement = document.createElement("link");
		spaceUsedLinkElement.setTextContent("https://www.aladin.co.kr/shop/UsedShop/wuseditemall.aspx?ItemId=249297227&TabType=3&partner=openAPI");
		spaceUsedElement.appendChild(spaceUsedLinkElement);

		return itemElement;
	}



}