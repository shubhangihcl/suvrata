package com.db.awmd.challenge;

import static org.mockito.Matchers.any;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.service.EmailNotificationService;

@RunWith(SpringRunner.class)
@SpringBootTest
public class NotificationServiceTest {
	
	@Mock
	private EmailNotificationService notificationServiceMock;
	
	@Test
	public void sendNotification() {
		notificationServiceMock.notifyAboutTransfer(any(Account.class), any(String.class));
	}

}
