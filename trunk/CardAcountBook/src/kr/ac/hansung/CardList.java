package kr.ac.hansung;

public interface CardList {
	final static int CREDIT_CARD = 1;
	final static int CHECK_CARD = 2;
	
	
	
	public String[] cardName = {
			"NHÄ«µå", "KB±¹¹ÎÄ«µå", "KB±¹¹ÎÃ¼Å©"
			
	};
	
	
	public String[] creditPeriod = {
			"1", "1", "1"
			
	};
	
	
	public int[] targetPrice = {
			200000, 200000, 200000,  
			
	};
	
	
	public int[] paymentPlan = {
			CHECK_CARD, CHECK_CARD, CREDIT_CARD
			
	};
	
	
	public String[] phoneNumber = {
			"15881600", "15881788", "15881788"
			
	};
}
