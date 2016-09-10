/*
   For step-by-step instructions on connecting your Android application to this backend module,
   see "App Engine Java Servlet Module" template documentation at
   https://github.com/GoogleCloudPlatform/gradle-appengine-templates/tree/master/HelloWorld
*/

package com.example.aliviamarciablount.myapplication.backend;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Logger;
import com.firebase.client.ValueEventListener;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class MyServlet extends HttpServlet {
    static Logger Log;

    static {
        Log = Logger.getLogger("com.example.aliviablount.myapplication.backend.MyServlet");
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        Log.info("Got cron message, constructing email.");

        //Create a new Firebase instance and subscribe on child events.
        Firebase firebase = new Firebase("https://todoapp-1e1dd.firebaseio.com/todoItems");
        firebase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Build the email message contents using every field from Firebase.
                final StringBuilder newItemMessage = new StringBuilder();
                newItemMessage.append("Good Morning!  You have the following todo items:\n");
                for (DataSnapshot todoItem : dataSnapshot.getChildren()) {
                    for (DataSnapshot field : todoItem.getChildren()) {
                        newItemMessage.append(field.getKey())
                                .append(":")
                                .append(field.getValue().toString())
                                .append("\n")
                    }
                }

                //Now Send the email
                Properties props = new Properties();
                Session session = Session.getDefaultInstance(props, null);
                try {
                    Message msg = new MimeMessage(session);
                    //Make sure you substitute your project-id in the email From field
                    msg.setFrom(new InternetAddress("nagger@todoapp-1e1dd.appspotmail.com",
                            "Todo Nagger"));
                    msg.addRecipient(Message.RecipientType.TO,
                            new InternetAddress("aliviablount@gmail.com", "Recipient"));
                    msg.setSubject("Good Morning!");
                    msg.setText(newItemMessage.toString());
                    Transport.send(msg);
                } catch (MessagingException | UnsupportedEncodingException e) {
                    Log.warning(e.getMessage());
                }
            }
            public void onCancelled(FirebaseError firebaseError) { }
        });
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {
        String name = req.getParameter("name");
        resp.setContentType("text/plain");
        if(name == null) {
            resp.getWriter().println("Please enter a name");
        }
        resp.getWriter().println("Hello " + name);
    }
}
