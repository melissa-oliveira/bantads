package com.bantads.msgerente.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bantads.msgerente.dto.MailStructure;
import com.bantads.msgerente.service.MailService;

@CrossOrigin
@RestController
public class MailController {
	
	@Autowired
	private MailService mailService;

	@PostMapping("/send/{mail}")
	public void sendEmail(@PathVariable String mail, @RequestBody MailStructure mailStructure) {
		mailService.sendMail(mail, mailStructure);
	}

}
