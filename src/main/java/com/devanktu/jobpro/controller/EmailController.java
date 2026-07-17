package com.devanktu.jobpro.controller;

import com.devanktu.jobpro.service.EmailService;
import com.devanktu.jobpro.service.SubscriberService;
import com.devanktu.jobpro.utils.annotation.ApiMessage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class EmailController {

    private final EmailService emailService;
    private final SubscriberService subscriberService;

    public EmailController(EmailService emailService, SubscriberService subscriberService) {
        this.emailService = emailService;
        this.subscriberService = subscriberService;
    }

    @GetMapping("/email")
    @ApiMessage("Send simple email")
    // @Scheduled(cron = "*/30 * * * * *")
    // @Transactional
    public String sendSimpleEmail() {
        //this.emailService.sendEmailSync("tudzvlx123@gmail.com", "test send email", "<h1><b>Hello</b></h1>", false, false);
        //this.emailService.sendEmailFromTemplateSync("tudzvlx123@gmail.com", "test send email", "job");
        this.subscriberService.sendSubscribersEmailJobs();
        return "ok";
    }

}
