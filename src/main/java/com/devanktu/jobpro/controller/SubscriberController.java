package com.devanktu.jobpro.controller;

import com.devanktu.jobpro.domain.Subscriber;
import com.devanktu.jobpro.service.SubscriberService;
import com.devanktu.jobpro.utils.SecurityUtil;
import com.devanktu.jobpro.utils.annotation.ApiMessage;
import com.devanktu.jobpro.utils.exception.IdInvalidException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1")
public class SubscriberController {

    private final SubscriberService subscriberService;

    public SubscriberController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @PostMapping("/subscribers")
    @ApiMessage("Create a subscribers")
    public ResponseEntity<Subscriber> createSubscriber(@Valid @RequestBody Subscriber subscriber) throws IdInvalidException {
        //check email
        boolean isExist = this.subscriberService.isExistsByEmail(subscriber.getEmail());
        if (isExist == true)
            throw new IdInvalidException("Email " + subscriber.getEmail() + " đã tồn tại");
        return ResponseEntity.status(HttpStatus.CREATED).body(this.subscriberService.createSubscriber(subscriber));
    }

    @PutMapping("/subscribers")
    @ApiMessage("Update a subscribers")
    public ResponseEntity<Subscriber> updateSubscriber(@RequestBody Subscriber subscriber) throws IdInvalidException {
        //check id
        Subscriber dbSubscriber = this.subscriberService.findById(subscriber.getId());
        if (dbSubscriber == null) {
            throw new IdInvalidException("Id " + subscriber.getId() + " không tồn tại");
        }
        return ResponseEntity.ok().body(this.subscriberService.updateSubscriber(dbSubscriber, subscriber));
    }

    @PostMapping("/subscribers/skills")
    @ApiMessage("Get subscriber's skill")
    public ResponseEntity<Subscriber> getSubscribersSkill() throws IdInvalidException {
        String email = SecurityUtil.getCurrentUserLogin().isPresent() == true
                ? SecurityUtil.getCurrentUserLogin().get()
                : "";

        return ResponseEntity.ok().body(this.subscriberService.findByEmail(email));
    }

}
