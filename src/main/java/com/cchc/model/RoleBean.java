package com.cchc.model;

import java.io.Serializable;

public class RoleBean implements Serializable {

    private static final long serialVersionUID = 1L;

    private int roleId;
    private String roleName;
    private String roleDescription;

    public RoleBean() {
    }

    public int getRoleId() {
        return roleId;
    }

    public void setRoleId(int roleId) {
        this.roleId = roleId;
    }

    public String getRoleName() {
        return roleName;
    }

    public void setRoleName(String roleName) {
        this.roleName = roleName;
    }

    public String getRoleDescription() {
        return roleDescription;
    }

    public void setRoleDescription(String roleDescription) {
        this.roleDescription = roleDescription;
    }
}
