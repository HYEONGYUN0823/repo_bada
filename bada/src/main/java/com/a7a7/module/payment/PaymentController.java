package com.a7a7.module.payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
public class PaymentController {
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
}