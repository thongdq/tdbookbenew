package td.book.tdbook.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class UserRoleId implements Serializable {

    @Column(name = "USER_ID")
    private Long userId;

    @Column(name = "ROLE_ID")
    private Long roleId;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getRoleId() {
        return roleId;
    }

    public void setRoleId(Long roleId) {
        this.roleId = roleId;
    }

    //You need the @Embeddable type to be Serializable
    //The @Embeddable type must override the default equals and hashCode methods based on the two Primary Key identifier values.
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UserRoleId other = (UserRoleId)obj;
        return Objects.equals(getUserId(), other.getUserId()) && Objects.equals(getRoleId(), other.getRoleId());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result
                + ((userId == null) ? 0 : userId.hashCode());
        result = prime * result
                + ((roleId == null) ? 0 : roleId.hashCode());
        return result;
    }

}
