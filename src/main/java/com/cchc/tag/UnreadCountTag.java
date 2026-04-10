package com.cchc.tag;

import com.cchc.dao.NotificationDAO;
import com.cchc.model.UserBean;
import jakarta.servlet.jsp.JspException;
import jakarta.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;

public class UnreadCountTag extends SimpleTagSupport {

    private NotificationDAO dao = new NotificationDAO();

    @Override
    public void doTag() throws JspException, IOException {
        Object obj = getJspContext().findAttribute("loginUser");
        if (obj == null) {
            obj = getJspContext().findAttribute("user");
        }

        int c = 0;
        try {
            if (obj instanceof UserBean) {
                UserBean u = (UserBean) obj;
                c = dao.countUnreadByUserId(u.getUserId());
            }
        } catch (Exception e) {
            c = 0;
        }

        getJspContext().getOut().print(c);
    }
}
