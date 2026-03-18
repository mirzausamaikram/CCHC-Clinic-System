package com.cchc.tag;

import com.cchc.model.UserBean;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

public class UsernameTag extends SimpleTagSupport {

    @Override
    public void doTag() throws JspException, IOException {
        Object userObj = getJspContext().findAttribute("loginUser");
        if (userObj instanceof UserBean) {
            UserBean user = (UserBean) userObj;
            getJspContext().getOut().print(user.getUsername());
        }
    }
}
