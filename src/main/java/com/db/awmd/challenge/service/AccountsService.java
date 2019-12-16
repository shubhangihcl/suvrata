package com.db.awmd.challenge.service;

import com.db.awmd.challenge.domain.Account;
import com.db.awmd.challenge.repository.AccountsRepository;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public class AccountsService {

	@Getter
	private final AccountsRepository accountsRepository;
	
	private final EmailNotificationService notificationService = new EmailNotificationService();

	@Autowired
	public AccountsService(AccountsRepository accountsRepository) {
		this.accountsRepository = accountsRepository;
	}

	public void createAccount(Account account) {
		this.accountsRepository.createAccount(account);
	}

	public Account getAccount(String accountId) {
		return this.accountsRepository.getAccount(accountId);
	}

	public synchronized ResponseEntity<Object> fundTransfer(String toAccountNumber, String fromAccountNumber, Float amount){
		
		//get the account details based on the account number
		Account toAccount = this.accountsRepository.getAccount(toAccountNumber);
		Account fromAccount = this.accountsRepository.getAccount(fromAccountNumber);
		
		//check if the account details are correct, else return a message

		if (amount < 0) {
			return new ResponseEntity<Object>("Amount can not be negative", HttpStatus.BAD_REQUEST);
		}
		if (!Optional.ofNullable(toAccount).isPresent()) {
			return new ResponseEntity<Object>("To account number is invalid", HttpStatus.BAD_REQUEST);
		}
		if (!Optional.ofNullable(fromAccount).isPresent()) {
			return new ResponseEntity<Object>("From account number is invalid", HttpStatus.BAD_REQUEST);
		}
		if (fromAccount.getBalance().floatValue() < amount) {
			return new ResponseEntity<Object>("Insuficent fund present in account", HttpStatus.BAD_REQUEST);
		}
		
		//add the amount to the receiving account
		toAccount.setBalance(new BigDecimal(toAccount.getBalance().floatValue() + amount));
		//remove the amount to the receiving account
		fromAccount.setBalance(new BigDecimal(fromAccount.getBalance().floatValue() - amount));
		
		//update both the accounts and sending notification
		this.accountsRepository.updateAccount(toAccount);
		notificationService.notifyAboutTransfer(toAccount, "Your account is credited "+amount+" rupees from account number "+fromAccountNumber);
		this.accountsRepository.updateAccount(fromAccount);
		notificationService.notifyAboutTransfer(fromAccount, "Your account is debited "+amount+" rupees, transfered to account number "+toAccountNumber);
		return new ResponseEntity<Object>("Transfered Successfully", HttpStatus.OK);
	}
}
