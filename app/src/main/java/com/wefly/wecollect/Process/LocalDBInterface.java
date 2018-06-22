package com.wefly.wecollect.Process;

import com.wefly.wecollect.model.Recipient;

public interface LocalDBInterface {
    //Sauvegarde les destinataires
    public void saveRecipient(Recipient recipient);
}
