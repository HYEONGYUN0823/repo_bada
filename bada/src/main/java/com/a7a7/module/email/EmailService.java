package com.a7a7.module.email;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.a7a7.module.member.MemberDto;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {
	@Autowired
	private JavaMailSender javaMailSender;
	
	@Value("${google_mail_username}")
	private String email;
	
	public void sendMailInquiryToAdmin(MemberDto memberDto, EmailDto emailDto, String phone, String inquiryType) throws Exception {
	    MimeMessage mimeMessage = javaMailSender.createMimeMessage();
	    MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, false, "UTF-8");

	    helper.setTo(email); // 관리자 이메일 주소
	    helper.setSubject(emailDto.getSubject());

	    String content = "<h3>회원 문의</h3>"
	            + "<p><strong>회원 정보:</strong><br/>"
	            + "이메일 : " + memberDto.getEmail() + "<br/>"
	            + "이름 : " + memberDto.getName() + "<br/>"
	            + "휴대전화 : " + phone + "<br/></p>"
	            + "<p><strong>문의 유형 :</strong>" + inquiryType + "</p>"
	            + emailDto.getMessage().replace("\n", "<br>");

	    helper.setText(content, true);
	    javaMailSender.send(mimeMessage);
	}
	
}
