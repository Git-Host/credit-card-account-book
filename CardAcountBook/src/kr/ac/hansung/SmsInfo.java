package kr.ac.hansung;


//Card Information Class
public class SmsInfo {
	private String cardName;		//ī�� �̸�
	private String approvalType;	//���� ���� (üũ����, �ſ���� ��)
	private String price;			//���� ����
	private String cardNumber;		//ī�� ��ȣ
	private String approvalTime;	//���� �Ͻ�
	private String place;			//���� ���
	private String category;
	
	public String getCardName() { return cardName; }
	public String getApprovalType() { return approvalType; }
	public String getPrice() { return price; }
	public String getCardNumber() { return cardNumber; }
	public String getApprovalTime() { return approvalTime; }
	public String getPlace() { return place; }
	public String getCategory(){ return category; }
	
	public void setCardName(String cName) { cardName = cName; }
	public void setApprovalType(String aType) { approvalType = aType; }
	public void setPrice(String _price) { price = _price; }
	public void setCardNumber(String cNum) { cardNumber = cNum; }
	public void setApprovalTime(String aTime) { approvalTime = aTime; }
	public void setPlace(String _place) { place = _place; }
	public void setCategory(String _category){category = _category;}

	public SmsInfo(String cardName) {
		setCardName(cardName);
	}
	
	public SmsInfo(String approvalTime, String cardName, String place, String price) {
		this.approvalTime = approvalTime;
		this.cardName = cardName;
		this.place = place;
		this.price = price;
	}
}
