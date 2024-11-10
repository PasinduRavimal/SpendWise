package com.spendWise.util;

import com.spendWise.models.Account;

import javafx.util.*;

public class StringAccountConverter extends StringConverter<Account> {

    @Override
    public String toString(Account account) {
        if (account == null) {
            return null;
        }
        return account.getAccountName();
    }

    @Override
    public Account fromString(String string) {
        try {
            Account account = Account.getAccountByName(string);
            return account;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
