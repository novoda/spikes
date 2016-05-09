package com.novoda.firechat.chat.login.displayer;

public interface LoginDisplayer {

    void attach(LoginActionListener actionListener);

    void detach(LoginActionListener actionListener);

    public interface LoginActionListener {

        void onUserNameEntered(String userName);

    }

}
