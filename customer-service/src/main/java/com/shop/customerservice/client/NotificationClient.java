package com.shop.customerservice.client;

import com.shop.customerservice.dto.MailDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "notification-service", url = "${url.notificationClient}")
public interface NotificationClient {

    @PostMapping("api/v1/notification/send/storage/update")
    void sendUpdateStorageEmail(@RequestBody MailDTO mailDTO);
}
