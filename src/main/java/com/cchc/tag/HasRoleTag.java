package com.cchc.tag;

import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

public class HasRoleTag extends SimpleTagSupport {

    private String value;

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public void doTag() throws JspException, IOException {
        Object roleObj = getJspContext().findAttribute("loginRole");
        if (roleObj == null || value == null) {
            return;
        }

        String currentRole = String.valueOf(roleObj);
        if (value.equalsIgnoreCase(currentRole) && getJspBody() != null) {
            getJspBody().invoke(null);
        }
    }
}
