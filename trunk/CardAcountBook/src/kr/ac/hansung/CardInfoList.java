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
	
	//Lotte 카드 이름, 이미지 리스트
	public int[] LotteCardName = {
			R.string.lotte_dc_supreme,
			R.string.lotte_dc_sweet,
			R.string.lotte_driving_pass,
			R.string.lotte_happy_point,
			R.string.lotte_lotte,
			R.string.Lotte_card
	};
	public int[] LotteCardImg = {
			R.drawable.lotte_dc_supreme,
			R.drawable.lotte_dc_sweet,
			R.drawable.lotte_driving_pass,
			R.drawable.lotte_happy_point,
			R.drawable.lotte_lotte,
			R.drawable.questionmark_card
	};
	//Woori 카드 이름 , 이미지 리스트
	public int[] WooriCardName = {
			R.string.woori_e,
			R.string.woori_green,
			R.string.woori_skypass,
			R.string.woori_v_oiling,
			R.string.woori_v_tiara,
			R.string.woori_v,
			R.string.Woori_card
			
	};
	public int[] WooriCardImg = {
			R.drawable.woori_e,
			R.drawable.woori_greeen,
			R.drawable.woori_skypass,
			R.drawable.woori_v_oiling,
			R.drawable.woori_v_tiara,
			R.drawable.woori_v,
			R.drawable.questionmark_card
			
	};
	//Samsung 카드 이름, 이미지 리스트
	public int[] SamsungCardName = {
			R.string.samsung_2,
			R.string.samsung_car_moa,
			R.string.samsung_cjone,
			R.string.samsung_costco,
			R.string.samsung_s_class,
			R.string.Samsung_card
	};
	public int[] SamsungCardImg = {
			R.drawable.samsung_2,
			R.drawable.samsung_car_moa,
			R.drawable.samsung_cjone,
			R.drawable.samsung_costco,
			R.drawable.samsung_s_class,
			R.drawable.questionmark_card
	};
	
	//Sinhan 카드 이름, 이미지 리스트
	public int[]SinhanCardName ={
			R.string.sinhan_4050,
			R.string.sinhan_achim,
			R.string.sinhan_gscaltex,
			R.string.sinhan_high_point,
			R.string.sinhan_lady,
			R.string.sinhan_love,
			R.string.sinhan_wisdom,
			R.string.Sinhan_card
			
	};
	public int[]SinhanCardImg ={
			R.drawable.sinhan_4050,
			R.drawable.sinhan_achim,
			R.drawable.sinhan_gscaltex,
			R.drawable.sinhan_high_point,
			R.drawable.sinhan_lady,
			R.drawable.sinhan_love,
			R.drawable.sinhan_wisdom,
			R.drawable.questionmark_card
	};
	
	//hyundai 카드 이름, 이미지 리스트
	public int[]HyundaiCardName = {
			R.string.hyundai_h3,
			R.string.hyundai_m,
			R.string.hyundai_m3,
			R.string.hyundai_r3,
			R.string.hyundai_v,
			R.string.hyundai_zero,
			R.string.Hyundai_card
	};
	public int[]HyundaiCardImg = {
			R.drawable.hyundai_h3,
			R.drawable.hyundai_m,
			R.drawable.hyundai_m3,
			R.drawable.hyundai_r3,
			R.drawable.hyundai_v,
			R.drawable.hyundai_zero,
			R.drawable.questionmark_card
			
	};
}
