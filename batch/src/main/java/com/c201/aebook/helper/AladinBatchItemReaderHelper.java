package com.c201.aebook.helper;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.c201.aebook.api.batch.AladinBatchItemReader;
import com.c201.aebook.api.book.persistence.entity.BookEntity;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AladinBatchItemReaderHelper {

	private final String API_URL = "https://www.aladin.co.kr/ttb/api/ItemList.aspx";
	private final String SEARCH_TARGET = "Used";
	private final String SUB_SEARCH_TARGET = "Book";
	private final String outputType = "xml";
	private final int maxResults = 50;
	private final String queryType = "ItemNewAll";
	private final RestTemplate restTemplate;
	@Value("${aladin.api.key}")
	private String API_KEY;
	private int nextIndex = 0;
	private List<BookEntity> bookList = new ArrayList<>();
	private final int OUT_OF_STOCK_FILTER = 1;
	private final String COVER_SIZE = "Big";

	@Autowired
	public AladinBatchItemReaderHelper(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	public List<BookEntity> getDataFromApi() throws
		IOException,
		ParserConfigurationException,
		SAXException,
		ParseException {

		List<BookEntity> books = new ArrayList<>();
		for (int pages = 1; pages <= 20; pages++) {
			//알라딘 url 정보
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(API_URL)
				.queryParam("ttbkey", API_KEY)
				.queryParam("QueryType", queryType)
				.queryParam("MaxResults", maxResults)
				.queryParam("start", pages)
				.queryParam("SearchTarget", SEARCH_TARGET)
				.queryParam("Version", "20131101")
				.queryParam("SubSearchTarget", SUB_SEARCH_TARGET)
				.queryParam("OptResult", "usedList")
				.queryParam("Output", outputType)
				.queryParam("outofStockfilter", OUT_OF_STOCK_FILTER)
				.queryParam("Cover", COVER_SIZE);

			System.out.println("여기있어요 = " + builder.toUriString());

			NodeList itemNodes = getItemElementByUrl(builder, "item");
			if(itemNodes == null) throw new IOException();


			for (int i = 0; i < itemNodes.getLength(); i++) {
				Node itemNode = itemNodes.item(i);
				BookEntity entity = parseBook(itemNode);

				if(entity != null) {
					books.add(entity);
				}

			}

		}

		return books;
	}

	public BookEntity parseBook(Node itemNode) throws
		ParseException,
		ParserConfigurationException,
		IOException,
		SAXException {

		String adultBookCheck = getChildText(itemNode, "adult");

		if("true".equals(adultBookCheck)) return null;//성인 책인 경우

		String title = getChildText(itemNode, "title");
		String author = getChildText(itemNode, "author");
		String publisher = getChildText(itemNode, "publisher");
		String pubDate = getChildText(itemNode, "pubDate");
		String coverUrl = getChildText(itemNode, "cover");
		String description = getChildText(itemNode, "description");

		SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
		Date date = new Date();
		if(pubDate != null){
			date = format.parse(pubDate);
		}


		//하위 태그인 subInfo에 접근
		NodeList nodeList = itemNode.getChildNodes();
		Node subInfoNode = getChildNode(nodeList, "subInfo");

		NodeList subInfoList = subInfoNode.getChildNodes();
		Node usedListNode = getChildNode(subInfoList, "usedList");

		NodeList userUsedList = usedListNode.getChildNodes();

		Node userUsedNode = getChildNode(userUsedList, "userUsed");
		Node aladinUsedNode = getChildNode(userUsedList, "aladinUsed");
		Node spaceUsedNode = getChildNode(userUsedList, "spaceUsed");

		int userUsedPrice = parseToInteger(userUsedNode, "minPrice");//회원 직접 배송 중고의 보유 상품중 최저가 상품 판매가격
		int newUsedBookprice = parseToInteger(itemNode, "priceSales");//새로 들어온 중고책 가격
		int aladinUsedPrice = parseToInteger(aladinUsedNode, "minPrice");//알라딘 직접 배송 중고의 보유 상품중 최저가 상품 판매가격
		int spaceUsedPrice = parseToInteger(spaceUsedNode, "minPrice");//광활한 우주점(매장 배송) 중고의 보유 상품중 최저가 상품 판매가격

		//보유 상품수
		int aladinUsedItemCount = parseToInteger(aladinUsedNode, "itemCount");
		int spaceUsedItemCount = parseToInteger(spaceUsedNode, "itemCount");
		int userUsedItemCount = parseToInteger(userUsedNode, "itemCount");

		ArrayList<Integer> prices = new ArrayList<>();
		prices.add(newUsedBookprice);

		//보유 상품수가 0이 아닌 경우에만 최저가 비교에 사용함
		if (aladinUsedItemCount != 0) {
			prices.add(aladinUsedPrice);
		}
		if (spaceUsedItemCount != 0) {
			prices.add(spaceUsedPrice);
		}
		if (userUsedItemCount != 0) {
			prices.add(userUsedPrice);
		}

		//최저가 구함
		int minPriceResult = getMinPrice(prices);

		String isbn;
		String aladinUrl;

		long newUsedBookId = 0;
		if (itemNode.getAttributes().getNamedItem("itemId").getTextContent() != null) {
			newUsedBookId = Integer.parseInt(itemNode.getAttributes().getNamedItem("itemId").getTextContent());
		}

		Node newBookParentNode = getChildNode(subInfoList, "newBookList");
		NodeList newBookList = newBookParentNode.getChildNodes();
		Node newBookNode = getChildNode(newBookList, "newBook");
		long usedBookId = parseToInteger(newBookNode, "itemId");

		long minPriceBookId;

		//최저가 가격의 알라딘 url 얻어옴
		if (minPriceResult == newUsedBookprice) {
			aladinUrl = getChildText(itemNode, "link");
			isbn = getChildText(itemNode, "isbn");
			minPriceBookId = newUsedBookId;
		} else {
			if (minPriceResult == aladinUsedPrice) {
				aladinUrl = getChildText(aladinUsedNode, "link");

			} else if (minPriceResult == spaceUsedPrice) {
				aladinUrl = getChildText(spaceUsedNode, "link");
			} else {
				aladinUrl = getChildText(userUsedNode, "link");
			}
			minPriceBookId = usedBookId;
			//isbn 구함
			String subInfoIsbn = getChildText(newBookNode, "isbn");

			//도서 상세 페이지로 접속
			String detailUrl = "http://www.aladin.co.kr/ttb/api/ItemLookUp.aspx";
			UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(detailUrl)
				.queryParam("ttbkey", API_KEY)
				.queryParam("itemIdType", "ISBN")
				.queryParam("ItemId", subInfoIsbn)
				.queryParam("output", outputType)
				.queryParam("Version", "20131101");

			NodeList itemNodes = getItemElementByUrl(builder, "item");

			isbn = getChildText(itemNodes.item(0), "isbn13");
		}

		if(minPriceBookId == 0 || isbn == null || isbn.isEmpty() || aladinUrl == null || aladinUrl.isEmpty() || author == null || author.isEmpty() || minPriceResult == 0) return null;

		BookEntity book = BookEntity.builder()
			.id(minPriceBookId)
			.coverImageUrl(coverUrl)
			.isbn(isbn)
			.publishDate(date)
			.publisher(publisher)
			.title(title)
			.aladinUrl(aladinUrl)
			.author(author)
			.description(description)
			.price(minPriceResult)
			.build();

		return book;
	}

	public NodeList getItemElementByUrl(UriComponentsBuilder builder, String tagName) throws
		IOException,
		SAXException,
		ParserConfigurationException {

		ResponseEntity<String> response = restTemplate.exchange(builder.toUriString(), HttpMethod.GET, null,
			String.class);

		if (response == null || response.getBody() == null) {
			throw new RuntimeException("API response is null or has no body.");
		}

		String responseBody = response.getBody();

		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		InputSource inputSource = new InputSource(new StringReader(responseBody));
		Document document = documentBuilder.parse(inputSource);

		return document.getElementsByTagName(tagName);
	}

	/*
	 * 해당 태그의 값을 Integer로 변환
	 * */
	public int parseToInteger(Node nodeItem, String tagName) {
		if (getChildText(nodeItem, tagName) != null) {
			return Integer.parseInt(getChildText(nodeItem, tagName));
		}
		return 0;
	}

	/*
	 * 최저가 구함
	 * */
	public int getMinPrice(List<Integer> prices) {
		return prices.stream().filter(Objects::nonNull).min(Integer::compareTo).orElse(0);
	}

	/*
	 * 해당 태그 이름에 해당하는 노드 가져옴
	 * */
	public Node getChildNode(NodeList itemNode, String tagName) {
		for (int i = 0; i < itemNode.getLength(); i++) {
			Node node = itemNode.item(i);
			if (tagName.equals(node.getNodeName())) {
				return node;
			}
		}

		return null;
	}

	/*
	 * 해당 태그의 text를 가져옴
	 * */
	public String getChildText(Node itemNode, String tagName) {
		if (itemNode == null)
			return null;

		NodeList nodeList = itemNode.getChildNodes();
		for (int i = 0; i < nodeList.getLength(); i++) {
			Node node = nodeList.item(i);
			//해당 태그의 노드를 찾고, 그 노드의 텍스트 값 반환
			if (node.getNodeName().equals(tagName)) {
				return node.getTextContent();
			}
		}
		//해당 태그의 노드가 없는 경우 null 반환
		return null;
	}

}
