package com.alarmapplication.www.data

import com.alarmapplication.www.R

//아시아 나라들, 수도
//Tirple : 여러 객체를 동시에 담을 수 있게함~ ( 사진,나라,수도 ) 한번에 담기
val asiaCountries: MutableList<Triple<Int, String, String>> = mutableListOf(
    Triple(R.drawable.bangladesh, "방글라데시", "다카"),
    Triple(R.drawable.bhutan, "부탄", "팀부"),
    Triple(R.drawable.bruneidarussalam, "브루나이 다루살람", "반다르스리브가완"),
    Triple(R.drawable.cambodia , "캄보디아", "프놈펜"),
    Triple(R.drawable.china , "중국", "베이징"),
    Triple(R.drawable.easttimor , "동티모르", "딜리"),
    Triple(R.drawable.india , "인도", "뉴델리"),
    Triple(R.drawable.indonesia , "인도네시아", "자카르타"),
    Triple(R.drawable.japan , "일본", "도쿄"),
    Triple(R.drawable.kazakhstan, "카자흐스탄", "누르술탄"),
    Triple(R.drawable.korea, "대한민국", "서울"),
    Triple(R.drawable.kyrgyzstan , "키르기스스탄", "비슈케크"),
    Triple(R.drawable.laos, "라오스", "비엔티안"),
    Triple(R.drawable.malaysia,"말레이시아", "쿠알라룸푸"),
    Triple(R.drawable.maldives , "몰디브", "말레"),
    Triple(R.drawable.mongolia , "몽골", "울란바토르"),
    Triple(R.drawable.myanmar , "미얀마", "양곤"),
    Triple(R.drawable.nepal , "네팔", "카트만두"),
    Triple(R.drawable.pakistan , "파키스탄", "이슬라마바드"),
    Triple(R.drawable.philippines , "필리핀", "마닐라"),
    Triple(R.drawable.singapore , "싱가포르", "싱가포르"),
    Triple(R.drawable.srilanka , "스리랑카", "콜롬보"),
    Triple(R.drawable.taiwan , "대만", "타이완"),
    Triple(R.drawable.tajikistan , "타지키스탄", "두샨베"),
    Triple(R.drawable.thailand , "태국", "방콕"),
    Triple(R.drawable.turkmenistan , "투르크메니스탄", "아시가바트"),
    Triple(R.drawable.uzbekistan , "우즈베키스탄", "타슈켄트"),
    Triple(R.drawable.vietnam, "베트남","호치민")
)

//유럽 나라들, 수도
val europeCountries: List<Triple<Int, String, String>> = listOf(
    Triple(R.drawable.albania, "알바니아", "티라나"),
    Triple(R.drawable.andorra, "안도라", "안도라라베야"),
    Triple(R.drawable.armenia, "아르메니아", "예레반"),
    Triple(R.drawable.austria, "오스트리아", "빈"),
    Triple(R.drawable.azerbaijan, "아제르바이잔", "바쿠"),
    Triple(R.drawable.belarus, "벨라루스", "민스크"),
    Triple(R.drawable.belgium, "벨기에", "브뤼셀"),
    Triple(R.drawable.bosnia_and_herzegovina, "보스니아 헤르체고비나", "사라예보"),
    Triple(R.drawable.bulgaria, "불가리아", "소피아"),
    Triple(R.drawable.croatia, "크로아티아", "자그레브"),
    Triple(R.drawable.cyprus, "키프로스", "니코시아"),
    Triple(R.drawable.czech, "체코", "프라하"),
    Triple(R.drawable.denmark, "덴마크", "코펜하겐"),
    Triple(R.drawable.estonia, "에스토니아", "탈린"),
    Triple(R.drawable.finland, "핀란드", "헬싱키"),
    Triple(R.drawable.france, "프랑스", "파리"),
    Triple(R.drawable.georgia, "조지아", "트빌리시"),
    Triple(R.drawable.germany, "독일", "베를린"),
    Triple(R.drawable.greece, "그리스", "아테네"),
    Triple(R.drawable.hungary, "헝가리", "부다페스트"),
    Triple(R.drawable.iceland, "아이슬란드", "레이캬비크"),
    Triple(R.drawable.ireland, "아일랜드", "더블린"),
    Triple(R.drawable.italy, "이태리", "로마"),
    Triple(R.drawable.kosovo, "코소보", "프리슈티나"),
    Triple(R.drawable.latvia, "라트비아", "리가"),
    Triple(R.drawable.lithuania, "리투아니아", "빌뉴스"),
    Triple(R.drawable.luxembourg, "룩셈부르크", "룩셈부르크"),
    Triple(R.drawable.macedonia, "마케도니아", "베르기나"),
    Triple(R.drawable.malta, "몰타", "발레타"),
    Triple(R.drawable.moldova, "모르도비야 공화국", "사란스크"),
    Triple(R.drawable.montenegero, "몬테네그로", "포드고리차"),
    Triple(R.drawable.netherlands, "네덜란드", "암스테르담"),
    Triple(R.drawable.norway, "노르웨이", "오슬로"),
    Triple(R.drawable.poland, "폴란드", "바르샤바"),
    Triple(R.drawable.portugal, "포르투칼", "리스본"),
    Triple(R.drawable.romania, "루마니아", "부쿠레슈티"),
    Triple(R.drawable.russia, "러시아", "모스크바"),
    Triple(R.drawable.san_marino, "산마리노", "산마리노"),
    Triple(R.drawable.serbia, "세르비아", "베오그라드"),
    Triple(R.drawable.slovakia, "슬로바키아", "브라티슬라바"),
    Triple(R.drawable.slovenia, "슬로베니아", "류블랴나"),
    Triple(R.drawable.spain, "스페인", "마드리드"),
    Triple(R.drawable.state_della_citta_del_vaticano, "바티칸 시국", "바티칸시"),
    Triple(R.drawable.sweden, "스웨덴", "스톡홀롬"),
    Triple(R.drawable.switzerland, "스위스", "취리히"),
    Triple(R.drawable.ukraine, "우크라이나", "키예프"),
    Triple(R.drawable.unitedkingdom, "영국", "런던")
)