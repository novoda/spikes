package com.novoda.voiceintegrationservice;

import android.app.VoiceInteractor;
import android.os.Bundle;
import android.service.voice.VoiceInteractionSession;
import android.service.voice.VoiceInteractionSessionService;

public class SessionService extends VoiceInteractionSessionService {
    @Override
    public VoiceInteractionSession onNewSession(final Bundle bundle) {
        return new VoiceInteractionSession(this) {
            @Override
            public void onConfirm(final Caller caller, final Request request, final CharSequence charSequence, final Bundle bundle) {

            }

            @Override
            public void onPickOption(final Caller caller, final Request request, final CharSequence charSequence, final VoiceInteractor.PickOptionRequest.Option[] options, final Bundle bundle) {

            }

            @Override
            public void onCommand(final Caller caller, final Request request, final String s, final Bundle bundle) {

            }

            @Override
            public void onCancel(final Request request) {

            }
        };
    }
}
