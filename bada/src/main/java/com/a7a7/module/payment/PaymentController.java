package com.a7a7.module.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import com.a7a7.module.accom.AccomService;
import com.a7a7.module.restaurant.RestaurantService;
import com.a7a7.module.sea.SeaService;

@Controller
public class PaymentController {
	@Autowired
	SeaService seaService;
	@Autowired
	RestaurantService restaurantService;
	@Autowired
	AccomService accomService;
	
//    @Value("${toss.payments.secret-key}")
//    private String secretKey;
//    
//    @PostMapping("/request")
//    public ResponseEntity<?> requestPayment(@RequestBody PaymentRequestDto requestDto) {
//        // 결제 요청 로직
//        return ResponseEntity.ok("결제 요청 성공");
//    }
	
//
//    @Autowired
//    private TossPayService tossPayService;
//
//    @GetMapping("/payment-success")
//    @ResponseBody
//    public String paymentSuccess(
//        @RequestParam("paymentKey") String paymentKey,
//        @RequestParam("orderId") String orderId,
//        @RequestParam("amount") int amount) {
//
//        String result = tossPayService.confirmPayment(paymentKey, orderId, amount);
//
//        return result; // 여기서 성공 페이지 보여주기 등으로 확장 가능
//    }
//
//    @GetMapping("/payment-fail")
//    @ResponseBody
//    public String paymentFail(@RequestParam String code, @RequestParam String message) {
//        return "결제 실패: " + message + " (코드: " + code + ")";
//    }
	
	@RequestMapping(value = "/paymentSuccess")
	public String paymentSuccess() {
		return "usr/payment/paymentSuccess";
	}
	
	/* ================================
	 				실패
	=================================== */
	
	@GetMapping("/fail/{type}/{id}")
	public String paymentFailPage(@PathVariable String type, @PathVariable String id, Model model) {
	    model.addAttribute("type", type);
	    model.addAttribute("id", id);
	    
	    return "usr/payment/paymentFail";  // 결제 실패 알림 페이지 (html)
	}
	
	@GetMapping("/fail/restaurant/{id}")
	public String paymentFailRestaurant(@PathVariable("id") String restaurantId) {
	    // 다시 해당 식당 상세페이지로 리다이렉트
	    return "redirect:/fail/restaurant/" + restaurantId;
	}

	@GetMapping("/fail/accom/{id}")
	public String paymentFailAccom(@PathVariable("id") String accomId) {
	    return "redirect:/bada/accom/" + accomId;
	}

	@GetMapping("/fail/travel/{id}")
	public String paymentFailTravel(@PathVariable("id") String sea_id) {
	    return "redirect:/bada/travel/" + sea_id;
	}
	
}