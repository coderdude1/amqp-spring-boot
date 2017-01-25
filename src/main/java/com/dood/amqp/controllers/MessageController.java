package com.dood.amqp.controllers;

import com.dood.amqp.services.MessageSenderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/messages")
public class MessageController {
    private MessageSenderService messageSenderService;

    public MessageController(@Autowired MessageSenderService messageSenderService) {
        this.messageSenderService = messageSenderService;
    }

    @RequestMapping(value = "/sendSimpleQueue", method = RequestMethod.POST)
    public void sendSimpleTextMessageQueue(@RequestParam String message) {
        messageSenderService.sendSimpleQueueMesssage(message);
    }

    @RequestMapping(value = "/sendMessageAwareQueue", method = RequestMethod.POST)
    public void sendMessageAwareQueue(@RequestParam String message) {
        messageSenderService.sendMessageAwareMessage(message);
    }
}
