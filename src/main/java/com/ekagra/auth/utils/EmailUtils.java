package com.ekagra.auth.utils;

import java.util.Hashtable;
import javax.naming.directory.*;

import org.springframework.stereotype.Component;

import javax.naming.*;

@Component
public class EmailUtils {

    public boolean isDomainValid(String email) {
        try {
            String domain = email.substring(email.indexOf("@") + 1);
            Hashtable<String, String> env = new Hashtable<>();
            env.put("java.naming.factory.initial", "com.sun.jndi.dns.DnsContextFactory");
            DirContext dirContext = new InitialDirContext(env);
            Attributes attrs = dirContext.getAttributes(domain, new String[]{"MX"});
            Attribute attr = attrs.get("MX");
            System.out.println("MX Records: " + attr);
            return attr != null;
        } catch (Exception e) {
            return false;
        }
    }
}

