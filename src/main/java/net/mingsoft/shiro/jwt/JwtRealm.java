package net.mingsoft.shiro.jwt;

import com.github.benmanes.caffeine.cache.Cache;
import net.mingsoft.fxxf.bean.entity.Permission;
import net.mingsoft.fxxf.bean.entity.Role;
import net.mingsoft.fxxf.bean.entity.User;
import net.mingsoft.utils.BeanUtil;
import net.mingsoft.utils.TokenUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

import java.util.List;

/**
 * Shiro 授权认证
 */
public class JwtRealm extends AuthorizingRealm {

    @Autowired
    private TokenUtil tokenUtil;

    @Autowired
    @Qualifier("tokenCache")
    private Cache<String, User> tokenCache;

    @Override
    public boolean supports(AuthenticationToken token) {
        //表示此Realm只支持JwtToken类型
        return token instanceof JwtToken;
    }

    /**
     * 授权
     *
     * @param principals
     * @return
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        SimpleAuthorizationInfo simpleAuthorInfo = new SimpleAuthorizationInfo();
        User user = (User) principals.getPrimaryPrincipal();
        Role role = user.getRole();
        if (BeanUtil.isNotBlank(role)) {
            simpleAuthorInfo.addRole(role.getName());
        }
        List<Permission> permissions = user.getPermissions();
        if (BeanUtil.isNotBlank(permissions)) {
            for (Permission permission : permissions) {
                simpleAuthorInfo.addStringPermission(permission.getPercode());
            }
        }
        return simpleAuthorInfo;
    }

    /**
     * 认证
     *
     * @param authenticationToken
     * @return
     * @throws AuthenticationException
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        JwtToken jwtToken = (JwtToken) authenticationToken;
        if (jwtToken == null) {
            throw new AuthenticationException("token不能为空");
        }
        // 获取token
        String token = jwtToken.getToken();
        // 从token中获取用户名
        String username = tokenUtil.getUsernameFromToken(token);
        if (StringUtils.isNotBlank(username)) {
            User user = tokenCache.getIfPresent(token);
            try {
                return new SimpleAuthenticationInfo(
                        user,
                        token,
                        getName()
                );
            } catch (Exception e) {
                throw new AuthenticationException(e);
            }
        }
        return null;
    }

    @Override
    public void clearCachedAuthorizationInfo(PrincipalCollection principals) {
        super.clearCachedAuthorizationInfo(principals);
    }

    @Override
    public void clearCachedAuthenticationInfo(PrincipalCollection principals) {
        super.clearCachedAuthenticationInfo(principals);
    }

    @Override
    public void clearCache(PrincipalCollection principals) {
        super.clearCache(principals);
    }

    public void clearAllCachedAuthorizationInfo() {
        getAuthorizationCache().clear();
    }

    public void clearAllCachedAuthenticationInfo() {
        getAuthenticationCache().clear();
    }

    public void clearAllCache() {
        clearAllCachedAuthenticationInfo();
        clearAllCachedAuthorizationInfo();
    }

}