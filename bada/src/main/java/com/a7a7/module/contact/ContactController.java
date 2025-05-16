package com.a7a7.module.contact;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.a7a7.common.config.MemberDetails;
import com.a7a7.module.accom.AccomDto;
import com.a7a7.module.accom.AccomService;
import com.a7a7.module.email.EmailDto;
import com.a7a7.module.email.EmailService;
import com.a7a7.module.member.MemberDto;
import com.a7a7.module.member.MemberService;
import com.a7a7.module.restaurant.RestaurantDto;
import com.a7a7.module.restaurant.RestaurantService;
import com.a7a7.module.sea.SeaDto;
import com.a7a7.module.sea.SeaService;

@Controller
public class ContactController {

    @Autowired
    RestaurantService restaurantService;
    @Autowired
    AccomService accomService;
    @Autowired
    EmailService emailService;
    @Autowired
    MemberService memberService;
    @Autowired
    SeaService seaService;

    @PostMapping("/inquiry")
    public ResponseEntity<String> sendInquiry(
            Authentication auth,
            @RequestBody EmailDto emailDto) {

        try {
            if (auth == null || !auth.isAuthenticated()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
            }

            MemberDetails memberDetails = (MemberDetails) auth.getPrincipal();
            MemberDto memberDto = new MemberDto();
            memberDto.setEmail(memberDetails.getUsername());
            memberDto.setName(memberDetails.getLoginName());
            
            String phone = emailDto.getPhone();
            String inquiryType = emailDto.getInquiryType();

            emailService.sendMailInquiryToAdmin(memberDto, emailDto, phone, inquiryType);

            return ResponseEntity.ok("문의가 전송되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("메일 전송 실패");
        }
    }
    
    // 여행지 문의 페이지
    @RequestMapping(value = "/sea/contact")
    public String seaContact(@RequestParam("sea_id") String sea_id, Model model, Authentication auth) {
    	if (auth == null || !auth.isAuthenticated()) {
    		return "redirect:/bada/signIn";
    	}
    	
    	MemberDetails memberDetails = (MemberDetails) auth.getPrincipal();
    	model.addAttribute("memberName", memberDetails.getLoginName());
    	model.addAttribute("memberEmail", memberDetails.getUsername());
    	
    	System.out.println("@@@@@@@@@@@@@@@@@@@@");
    	System.out.println("sea_id");
    	SeaDto sea = seaService.seaView(sea_id);
    	System.out.println("@@@@@@@@@@@@@@@@@@@@");
    	model.addAttribute("item", sea);
    	model.addAttribute("title", sea.getSareaDtlNm());
    	System.out.println("========================" + sea.getSareaDtlNm() + "================================================================");
    	
    	return "usr/contact/contact";
    }

    // 식당 문의 페이지
    @RequestMapping(value = "/restaurant/contact")
    public String restaurantContact(@RequestParam("restaurantId") String restaurantId, Model model, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/bada/signIn";
        }

        MemberDetails memberDetails = (MemberDetails) auth.getPrincipal();
        model.addAttribute("memberName", memberDetails.getLoginName());
        model.addAttribute("memberEmail", memberDetails.getUsername());

        RestaurantDto restaurant = restaurantService.findRestaurantById(restaurantId);
        model.addAttribute("item", restaurant);
        model.addAttribute("title", restaurant.getTitle());
        
        return "usr/contact/contact";
    }

    // 숙소 문의 페이지
    @RequestMapping(value = "/accom/contact")
    public String accomContact(@RequestParam("accomId") String accomId, Model model, Authentication auth) {
        if (auth == null || !auth.isAuthenticated()) {
            return "redirect:/bada/signIn";
        }

        MemberDetails memberDetails = (MemberDetails) auth.getPrincipal();
        model.addAttribute("memberName", memberDetails.getLoginName());
        model.addAttribute("memberEmail", memberDetails.getUsername());

        AccomDto accom = accomService.findAccomById(accomId);
        model.addAttribute("item", accom);
        model.addAttribute("title", accom.getTitle());
        
        return "usr/contact/contact";
    }
}
