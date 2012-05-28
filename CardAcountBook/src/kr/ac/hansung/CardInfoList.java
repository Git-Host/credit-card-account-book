package kr.ac.hansung;

/**
 * CardInfoList.java CardExpandableListActivity에서 카드리스트를 보여주기 위한 interface
 * @author Junu Kim
 */
public interface CardInfoList {

	// KB국민 카드 이름, 이미지 리스트
	public int[] kbCardName = { 
			R.string.kb_kookmin_good_shopping,
			R.string.kb_kookmin_goodday,
			R.string.kb_kookmin_olleh,
			R.string.kb_kookmin_star,
			R.string.kb_kookmin_wise,
			R.string.KB_card
	};

	public int[] kbCardImg = { 
			R.drawable.kb_kookmin_good_shopping,
			R.drawable.kb_kookmin_goodday,
			R.drawable.kb_kookmin_olleh,
			R.drawable.kb_kookmin_star,
			R.drawable.kb_kookmin_wise,
			R.drawable.questionmark_card
	};
	
	// NH 카드 이름, 이미지 리스트
	public int[] nhCardName = { 
			R.string.nh_chaum_card_chun,
			R.string.nh_chaum_check,
			R.string.nh_chaum,
			R.string.nh_ok_check,
			R.string.nh_shoppingsave,
			R.string.NH_card
	};

	public int[] nhCardImg = { 
			R.drawable.nh_chaum_card_chun,
			R.drawable.nh_chaum_check,
			R.drawable.nh_chaum,
			R.drawable.nh_ok_check,
			R.drawable.nh_shoppingsave,
			R.drawable.questionmark_card
	};
}
