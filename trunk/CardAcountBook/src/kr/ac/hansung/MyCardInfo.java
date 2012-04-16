package kr.ac.hansung;

public class MyCardInfo {
	private int cardImage;
	private int phoneImage;
	private String cardName;
	private String cardNumber;
	
	public int getCardImage() { return cardImage; }
	public int getPhoneImage() { return phoneImage; }
	public String getCardNumber() {	return cardNumber; }
	public String getCardName() { return cardName; }

	public void setCardImage(int cardImage) { this.cardImage = cardImage; }
	public void setPhoneImage(int phoneImage) { this.phoneImage = phoneImage; }
	public void setCardName(String cardName) { this.cardName = cardName; }
	public void setCardNumber(String cardNumber) { this.cardNumber = cardNumber; }
	
	public MyCardInfo() {};
	
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
