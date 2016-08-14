package org.thiki.kanban.passwordRetrieval;

import freemarker.template.TemplateException;
import org.springframework.stereotype.Service;
import org.thiki.kanban.foundation.common.VerificationCodeService;
import org.thiki.kanban.foundation.exception.BusinessException;
import org.thiki.kanban.foundation.mail.MailService;
import org.thiki.kanban.registration.Registration;
import org.thiki.kanban.registration.RegistrationPersistence;

import javax.annotation.Resource;
import javax.mail.MessagingException;
import java.io.IOException;

/**
 * Created by xubt on 8/8/16.
 */
@Service
public class PasswordRetrievalService {

    private static final String passwordRetrievalEmailTemplate = "passwordRetrieval.ftl";
    @Resource
    private PasswordRetrievalPersistence passwordRetrievalPersistence;
    @Resource
    private VerificationCodeService verificationCodeService;
    @Resource
    private RegistrationPersistence registrationPersistence;

    @Resource
    private MailService mailService;

    public void createRetrievalRecord(RegisterEmail registerEmail) throws TemplateException, IOException, MessagingException {
        Registration registeredUser = registrationPersistence.findByEmail(registerEmail.getEmail());
        if (registeredUser == null) {
            throw new BusinessException(PasswordRetrievalCodes.EmailIsNotExists.code(), PasswordRetrievalCodes.EmailIsNotExists.message());
        }

        String verificationCode = verificationCodeService.generate();

        PasswordRetrieval passwordRetrieval = new PasswordRetrieval();
        passwordRetrieval.setEmail(registerEmail.getEmail());
        passwordRetrieval.setVerificationCode(verificationCode);
        passwordRetrievalPersistence.createRecord(passwordRetrieval);

        PasswordEmail passwordEmail = new PasswordEmail();
        passwordEmail.setReceiver(registeredUser.getEmail());
        passwordEmail.setUserName(registeredUser.getName());
        passwordEmail.setVerificationCode(verificationCode);
        mailService.sendMailByTemplate(passwordEmail, passwordRetrievalEmailTemplate);
    }
}
