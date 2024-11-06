package it.polito.wa2.group22.authservice.services

import it.polito.wa2.group22.authservice.exceptions.EmailServiceException
import it.polito.wa2.group22.authservice.utils.EmailResult
import it.polito.wa2.group22.authservice.utils.emailResultToMessage
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.SimpleMailMessage
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.stereotype.Service
import java.text.SimpleDateFormat
import java.util.Date

@Service
class EmailService {

    @Autowired
    lateinit var mailSender: JavaMailSender

    fun sendMail(emailAddress: String, username: String, activationCode: String, expiration: Date): EmailResult {
        if (username == "") {
            return EmailResult.MISSING_USERNAME
           // throw EmailServiceException(emailResultToMessage[EmailResult.MISSING_USERNAME]!!)
        }
        if (activationCode == "") {
           return EmailResult.MISSING_ACT_CODE
            //throw EmailServiceException(emailResultToMessage[EmailResult.MISSING_ACT_CODE]!!)
        }
        if (emailAddress == "") {
            return EmailResult.MISSING_EMAIL
           // throw EmailServiceException(emailResultToMessage[EmailResult.MISSING_EMAIL]!!)
        }

        val emailMessage = SimpleMailMessage()
        emailMessage.setFrom("group22.wa2@libero.it")
        emailMessage.setTo(emailAddress)
        emailMessage.setText(
            "Hello $username, \n"
                    + "here's your activation code: \n\n"
                    + "$activationCode \n\nThe activation code is valid until: ${
                SimpleDateFormat(
                    "yyyy-MM-dd hh:mm:ss"
                ).format(expiration)
            }.\n" + "Otherwise you can use the following link: <a href='https://www.youtube.com/watch?v=dQw4w9WgXcQ'>prova</a>"
        )
        emailMessage.setSubject("Activation code group22")
        mailSender.send(emailMessage)
        return EmailResult.SUCCESS
    }
}