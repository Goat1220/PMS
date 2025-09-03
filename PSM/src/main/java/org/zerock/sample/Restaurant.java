package org.zerock.sample;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import lombok.Data;
import lombok.Setter;

//어토네이션은 아래로 줄을 띄우면안됨!!!!!!
@Component // Java beans의 구성요소
@Data		//getter setter 만들어주는 lombok 어노테이션
public class Restaurant {
	
	@Setter(onMethod_ = @Autowired)//@Setter = 추가 지정(앞의 @Data 어노테이션으로 이미 getter, setter등 다 세팅이 되었지만 개발자가 setter를 한번 내가 따로 더 쓰고싶다 하면 이걸 추가해주면됨), 이 코드의 경우 의존성 주입을 위해 setter를 하나 더 넣으려고 사용, onMethod_ = @Autowired, lombok 이 없으면 setter위에 @Autowired만 적으면됨
	private Chef chef; //Restaurant의 프로퍼티는 Chef의 chef이다 (Restaurant는 Chef에 의존적이다.)
}
