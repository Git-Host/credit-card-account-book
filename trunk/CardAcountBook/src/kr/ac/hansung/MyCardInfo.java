package kr.ac.hansung;

/**
 * MyCardInfo.java
 * 카드 정보를 담기위한 Class
 * @author Junu Kim
 */
public class MyCardInfo {
	
	private int cardImage;
	private int phoneImage;
	private int cardPrimaryKey;
	private int paymentDay; 
	private int tAmount;
	private String cardName;
	private String reduceCardName;
	private String cardNumber;
	private String cardType;
	private String cardImageUri;
	
	// Getter
	public int getCardPrimaryKey() { return cardPrimaryKey; }
	public int getCardImage() { return cardImage; }
	public int getPhoneImage() { return phoneImage; }
	public String getCardNumber() {	return cardNumber; }
	public String getCardName() { return cardName; }
	public String getReduceCardName() { return reduceCardName; }
	public int getPaymentDay() { return paymentDay; }
	public String getCardType() { return cardType; }
	public int getTAmount() { return tAmount; }
	public String getCardImageUri() { return cardImageUri; }

	// Setter
	public void setCardPrimaryKey(int cardPrimaryKey) { this.cardPrimaryKey = cardPrimaryKey; }
	public void setCardImage(int cardImage) { this.cardImage = cardImage; }
	public void setPhoneImage(int phoneImage) { this.phoneImage = phoneImage; }
	public void setCardName(String cardName) { this.cardName = cardName; }
	public void setReduceCardName(String reduceCardName) { this.reduceCardName = reduceCardName; }
	public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
	public void setCardType(String cardType) { this.cardType = cardType; }
	public void setPaymentDay(int paymentDay) { this.paymentDay = paymentDay; }
	public void setTAmount(int tAmount) { this.tAmount = tAmount; }
	public void setCardImageUri(String cardImageUri) { this.cardImageUri = cardImageUri; }

	// Constructor
	public MyCardInfo() {};
	
	public MyCardInfo(String cardName, String cardNumber, int paymentDay, int tAmount, String cardType, int cardImage, String cardImageUri) {
		this.cardImage = cardImage;
		this.cardName = cardName;
		this.cardNumber = cardNumber;
		this.paymentDay = paymentDay; 
		this.tAmount = tAmount;
		this.cardType = cardType;
		this.cardImageUri = cardImageUri;
	}
	
	public MyCardInfo(int cardPrimaryKey, String cardName, String cardNumber, int paymentDay, int tAmount, String cardType, String cardImageUri) {
		this.cardPrimaryKey = cardPrimaryKey;
		this.cardName = cardName;
		this.cardNumber = cardNumber;
		this.paymentDay = paymentDay; 
		this.tAmount = tAmount;
		this.cardType = cardType;
		this.cardImageUri = cardImageUri;
	}
	
	public MyCardInfo(String cardName, String cardNumber) {
		setCardImage(R.drawable.nh_chaum);
		setPhoneImage(R.drawable.icon_phone_3);
		this.setCardName(cardName);
		this.setCardNumber(cardNumber);
	}
	
	public MyCardInfo(String cardName, String cardNumber, int cardImage) {
		setCardImage(cardImage);
		setPhoneImage(R.drawable.icon_phone_3);
		this.setCardName(cardName);
		this.setCardNumber(cardNumber);
	}
}
